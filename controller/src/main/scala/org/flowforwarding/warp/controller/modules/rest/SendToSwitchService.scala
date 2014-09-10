/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.rest

import scala.util.{Failure, Success, Try}
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

import spray.httpx.unmarshalling._
import spray.json._
import spray.http._

import HttpCharsets._
import MediaTypes._

import org.flowforwarding.warp.controller._
import org.flowforwarding.warp.controller.api.dynamic.{DynamicDriver, DynamicBuilderInput, DynamicStructure}
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.driver_interface.OfpFeaturesExtractor
import spire.math.{UInt, UByte, ULong}
import org.flowforwarding.warp.controller.bus.{MessageEnvelope, ControllerBus}
import org.flowforwarding.warp.controller.modules.MessageConsumer
import org.flowforwarding.warp.controller.ModuleManager._
import org.flowforwarding.warp.controller.SwitchConnector.{SwitchOutgoingMessage, SwitchIncomingMessage}
import org.flowforwarding.warp.controller.api.fixed.text_view._

case class SendToSwitchRequest(dpid: ULong, version: UByte, reply: Boolean, message: JsObject)
//case class ReceivedFromSwitchResponse(dpid: Long, version: Short, message: DynamicStructure)

object SendToSwitchService extends DefaultJsonProtocol {
  implicit def uLongJF = new JsonFormat[ULong] {
    override def read(json: JsValue): ULong = ULong(implicitly[JsonFormat[Long]].read(json))

    override def write(obj: ULong): JsValue = implicitly[JsonFormat[Long]].write(obj.signed)
  }

  implicit def uByteJF = new JsonFormat[UByte] {
    override def read(json: JsValue): UByte = UByte(implicitly[JsonFormat[Byte]].read(json))

    override def write(obj: UByte): JsValue = implicitly[JsonFormat[Byte]].write(obj.signed)
  }

  implicit val RequestFormat = jsonFormat4(SendToSwitchRequest)

  implicit val toRequestUnmarshaller = new FromRequestUnmarshaller[SendToSwitchRequest]{
    def apply(req: HttpRequest): Deserialized[SendToSwitchRequest] = {
      try{
        Right(req.entity.data.asString.parseJson.convertTo[SendToSwitchRequest])
      }
      catch{
        case t: Throwable => Left(MalformedContent("", t))
      }
    }
  }
}

import SendToSwitchService._

class SendToSwitchService(val bus: ControllerBus, serverPrefix: String) extends RestApiService(serverPrefix) with MessageConsumer {
  override val servicePrefix = "send"
  override def route = post { entity(as[SendToSwitchRequest]) { request => complete(send(request)) } }

  private implicit def dynamicStructureFormat(implicit driver: MessagesDescriptionHelper[_]) = new JsonFormat[DynamicStructure] {
    def read(json: JsValue): DynamicStructure = driver.buildDynamic(parse(json.asJsObject)).get
    def write(obj: DynamicStructure): JsValue = fromView(driver.toConcreteMessage(obj).get.textView)

    def parse(json: JsObject): BITextView = {
      def toInputMember(json: JsValue): BITextViewItem = json match {
        case structure: JsObject => parse(structure)
        case JsNumber(n) => Num(n.toBigInt())
        case JsString(s) => Str(s)
        case JsBoolean(b) => Str(b.toString)
        case JsArray(values) =>
          val members = values map toInputMember
          BITextViewItems(members)
      }

      // Structure: { "OFPName": { "f1": v1, "f2": v2 } } (two nested js objects)
      val (structureName, JsObject(structureFields)) = json.fields.head
      new BITextView(structureName, structureFields mapValues toInputMember)
    }

    def fromView(view: BITextView): JsObject = {
      def fromStructureTextItem(item: BITextViewItem): JsValue = item match {
        case Str(s) => JsString(s)
        case Num(n) => JsNumber(n)
        case v: BITextView => fromView(v)
        case BITextViewItems(vs) => JsArray(vs map fromStructureTextItem)
      }
      JsObject(view.structureName -> JsObject(view.data mapValues fromStructureTextItem))
    }
  }

  def send(req: SendToSwitchRequest): Future[HttpResponse] = {
    val SendToSwitchRequest(dpid, version, needReply, message) = req

    askFirst(DriverByVersion(version)) flatMap {
      case response: DriverFoundResponse =>
        implicit val driver = response.driver.asInstanceOf[MessagesDescriptionHelper[_] with OfpFeaturesExtractor[DynamicStructure]]

        Try { message.convertTo[DynamicStructure] } match {
          case Success(msg) =>
            val msg = message.convertTo[DynamicStructure]
            publishMessage(SwitchOutgoingMessage(dpid, msg))

            if(needReply) {
              val p = Promise[SwitchIncomingMessage[DynamicStructure]]()
              awaitingResponses((dpid, driver.getXid(msg))) = p
              p.future map { m => HttpResponse(200, HttpEntity(contentType = ContentType(`application/json`, `UTF-8`), string = m.msg.toJson.toString))}
            } else {
              Future.successful(HttpResponse(200, "Message published")) // todo: make sure message was sent
            }
          case Failure(t) =>
            t.printStackTrace()
            println("Internal error: " + t.getMessage)
            Future.successful(HttpResponse(StatusCodes.InternalServerError, HttpEntity("Internal error: " + t.getMessage)))
        }
    }
    // make http responses (think about multipart http messages for multipart ofp messages)
  }

  // dpid x xid => promise of response
  private val awaitingResponses = scala.collection.mutable.Map[(ULong, UInt), Promise[SwitchIncomingMessage[DynamicStructure]]]()

  override def started() = {
    subscribe("incomingResponses") { case m: SwitchIncomingMessage[_] => true }
    super.started()
  }

  protected def handleEvent(e: MessageEnvelope): Unit = e match {
    case m @ SwitchIncomingMessage(dpid, driver, msg: DynamicStructure) =>
      awaitingResponses.remove((dpid, driver.getXid(msg))) foreach {
        _ complete Try { m.asInstanceOf[SwitchIncomingMessage[DynamicStructure]] }
      }
  }
}
