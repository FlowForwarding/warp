/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.opendaylight_rest.northbound

import java.util.concurrent.TimeoutException

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

import spire.math.{UInt, UShort}

import spray.http.HttpCharsets._
import spray.http.MediaTypes._
import spray.http.{ContentType, HttpEntity, HttpResponse}
import spray.json._

import com.typesafe.scalalogging.StrictLogging

import org.flowforwarding.warp.controller.bus.ServiceNotFoundException
import org.flowforwarding.warp.controller.modules.opendaylight_rest.sal._

object NorthboundUtils extends StrictLogging{
  implicit def fuRecoverExt(f: Future[HttpResponse]) = new {
    def withServiceErrorReport(serviceUnavailableMessage: String)(implicit context: ExecutionContext) = f recover {
      case _: TimeoutException | _: ServiceNotFoundException => HttpResponse(503, serviceUnavailableMessage)
      case e =>
        val err = if(e.getMessage == "Boxed Error" && e.getCause != null) e.getCause else e
        HttpResponse(500, err.getStackTrace.mkString(err.getClass.getSimpleName + "\n" + err.getMessage + "\n", "\n", ""))
    }
  }

  def processNode(serviceUnavailableMessage: String, creationFailedMessage: String)
                 (nodeType: String, nodeId: String)
                 (action: Node[_] => Future[HttpResponse])
                 (implicit context: ExecutionContext) =
    Node.create(nodeType, nodeId) match {
      case Success(n) =>
        action(n) withServiceErrorReport serviceUnavailableMessage
      case Failure(t) =>
        logger.error("Creation of node failed", t)
        Future.successful(HttpResponse(404, creationFailedMessage))
    }

  def processNodeConnector(serviceUnavailableMessage: String, creationFailedMessage: String)
                          (nodeType: String, nodeId: String, connectorType: String, connectorId: String)
                          (action: NodeConnector[_, _ <: Node[_]] => Future[HttpResponse])
                          (implicit context: ExecutionContext) =
    Node.create(nodeType, nodeId).flatMap(node => NodeConnector.create(node, connectorType, connectorId)) match {
      case Success(nc) =>
        action(nc) withServiceErrorReport serviceUnavailableMessage
      case Failure(t) =>
        logger.error("Creation of node or connector failed", t)
        Future.successful(HttpResponse(404, creationFailedMessage))
    }
  
  def jsonOk(js: JsValue) = HttpResponse(200, HttpEntity(contentType = ContentType(`application/json`, `UTF-8`), string = js.toString))

  implicit def nodeFormat = new JsonFormat[Node[_]] {
    def write(node: Node[_]): JsValue =
      JsObject("type" -> JsString(node.protocol), "id" -> JsString(node.idStr))

    override def read(json: JsValue): Node[_] = json match {
      case JsObject(nd) =>
        val (JsString(t), JsString(id)) = (nd("type"), nd("id"))
        Node.create(t, id).get
    }
  }

  implicit class PimpedNode(n: Node[_]){
    def toJson[N]: JsValue = nodeFormat.write(n)
  }

  implicit def nodeConnectorWriter = new JsonWriter[NodeConnector[_, _ <: Node[_]]] {
    def write(connector: NodeConnector[_, _ <: Node[_]]): JsValue =
      JsObject("node" -> connector.node.toJson, "id" -> JsString(connector.idStr), "type" -> JsString(connector.`type`))
  }

  // nodeConnector.toJson doesn't work because of wildcards (?)
  // here is a workaround
  implicit class PimpedConnector(c: NodeConnector[_, _ <: Node[_]]){
    def toJson[_, N <: Node[_]]: JsValue = nodeConnectorWriter.write(c)
  }

  implicit def propertyWriter = new JsonWriter[Property[_]] {
    def write(p: Property[_]): JsValue = {
      val value = p match {
        case mac: MAC => mac.toString
        case other => other.value.toString
      }
      JsObject("value" -> JsString(value))
    }
  }

  implicit def propertiesWriter = new JsonWriter[Set[Property[_]]] {
    def write(properties: Set[Property[_]]): JsValue =
      JsObject(properties.toSeq map (p => p.name -> propertyWriter.write(p)) toMap)
  }

  implicit def flowFormat = new JsonFormat[(Node[_], Flow)] {
    override def write(flow: (Node[_], Flow)): JsValue = {
      def jsStr(a: Any) = JsString(a.toString)

      val (node, Flow(cookie, name, installInHw, priority, idleTimeout, hardTimeout, matchFields, actions)) = flow

      val fields = List("installInHw" -> jsStr(installInHw),
        "node" -> node.toJson,
        "actions" -> jsStr(actions.mkString(","))) ++
        cookie.map(v => "cookie" -> jsStr(v)).toList ++
        name.map(v => "name" -> jsStr(v)).toList ++
        priority.map(v => "priority" -> jsStr(v)).toList ++
        idleTimeout.map(v => "idleTimeout" -> jsStr(v)).toList ++
        hardTimeout.map(v => "hardTimeout" -> jsStr(v)).toList ++
        matchFields.map(v => v.`type` -> jsStr(v.stringValue))

      JsObject(fields.toMap)
    }

    override def read(json: JsValue): (Node[_], Flow) = json match {
      case JsObject(fs) =>
        (fs get "cookie",
          fs get "name",
          fs("node"),
          fs get "installInHw",
          fs get "priority",
          fs get "idleTimeout",
          fs get "hardTimeout",
          fs filterKeys MatchField.names.contains,
          fs("actions")) match {

          case (jsCookie,
          flowName,
          jsNode,
          jsInstallInHw,
          jsPriority,
          jsIdleTimeout,
          jsHardTimeout,
          matchFields,
          JsArray(actions)) if actions.nonEmpty =>

            val node = jsNode.convertTo[Node[_]]
            def ncString(port: String) = NodeConnector.toString(node, port)

            val flow = Flow(
              jsCookie map { case JsString(id) => id.toLong},
              flowName map {
                _.toString
              },
              jsInstallInHw.fold(false) { case JsString(install) => install.toBoolean},
              jsPriority map { case JsString(priority) => UShort(priority.toInt)},
              jsIdleTimeout map { case JsString(idleTimeout) => UInt(idleTimeout.toLong)},
              jsHardTimeout map { case JsString(hardTimeout) => UInt(hardTimeout.toLong)},
              matchFields map {
                case (name, JsString(value)) =>
                  val v = if (name == MatchField.IngressPortName) ncString(value) else value
                  MatchField(name, v).get
              } toSet,
              actions flatMap {
                case JsString(action) =>
                  val Array(name, args@_*) = action split '='
                  args.headOption
                    .fold(Array(Option.empty[String])) { a => a split ',' map Option.apply}
                    .map { arg =>
                      val otp = arg map { p => if (name == Action.OutputName) ncString(p) else p}
                      Action(name, otp).get
                  }
              })
            (node, flow)
        }
    }
  }

  // nodeConnector.toJson doesn't work because of wildcards (?)
  // here is a workaround
  implicit class PimpedFlow(n: (Node[_], Flow)){
    def toJson[N]: JsValue = flowFormat.write(n)
  }
}