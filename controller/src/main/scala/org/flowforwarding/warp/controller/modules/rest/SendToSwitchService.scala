/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.rest

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import spray.httpx.unmarshalling._
import spray.json._
import spray.http._

import HttpCharsets._
import MediaTypes._

import spire.math.{UByte, ULong}

import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.text_view._
import org.flowforwarding.warp.controller.bus.ControllerBus
import org.flowforwarding.warp.controller.SwitchConnector._

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

class SendToSwitchService(val bus: ControllerBus, serverPrefix: String) extends RestApiService(serverPrefix) with FixedStructuresSender {
  override val servicePrefix = "/send"
  override def route = post { entity(as[SendToSwitchRequest]) { request => complete(send(request)) } }

  implicit private def writeJson(msg: FixedOfpMessage): JsValue = {
    def fromView(view: BITextView): JsObject = {
      def fromStructureTextItem(item: BITextViewItem): JsValue = item match {
        case Str(s) => JsString(s)
        case Num(n) => JsNumber(n)
        case v: BITextView => fromView(v)
        case BITextViewItems(vs) => JsArray(vs map fromStructureTextItem)
      }
      JsObject(view.structureName -> JsObject(view.data mapValues fromStructureTextItem))
    }
    fromView(msg.textView)
  }

  implicit private def readJson(json: JsValue): BITextView = {
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
    parse(json.asJsObject)
  }

  def send(req: SendToSwitchRequest): Future[HttpResponse] = {
    val SendToSwitchRequest(dpid, version, needReply, message) = req
    sendTextView[JsValue, JsValue](version, dpid, message, needReply) map {
      case SendingSuccessful =>
        HttpResponse(StatusCodes.OK, "Message published")
      case SingleMessageSwitchResponse(json) =>
        HttpResponse(StatusCodes.OK, HttpEntity(contentType = ContentType(`application/json`, `UTF-8`), string = json.toString))
      case MultipartMessageSwitchResponse(json) =>
        // think about multipart http messages for multipart ofp messages
        HttpResponse(StatusCodes.OK, HttpEntity(contentType = ContentType(`application/json`, `UTF-8`), string = JsArray(json: _*).toString))
      case SendingFailed(t) =>
        log.error("Unable to send message", t)
        HttpResponse(StatusCodes.InternalServerError, HttpEntity("Internal error: " + t.getMessage))
    }
  }
}
