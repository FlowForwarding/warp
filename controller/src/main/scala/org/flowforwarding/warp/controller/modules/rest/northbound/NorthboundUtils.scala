/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.rest.northbound

import java.util.concurrent.TimeoutException
import org.flowforwarding.warp.controller.modules.managers.{MacAddress, Property, NodeConnector, Node}
import spray.http.HttpCharsets._
import spray.http.MediaTypes._
import spray.json._

import scala.concurrent.{ExecutionContext, Future}
import spray.http.{ContentType, HttpEntity, HttpResponse}

import scala.util.{Failure, Success}

object NorthboundUtils{
  implicit def fuRecoverExt(f: Future[HttpResponse]) = new {
    def withServiceErrorReport(serviceUnavailableMessage: String)(implicit context: ExecutionContext) = f recover {
      case e: TimeoutException => HttpResponse(503, serviceUnavailableMessage)
      case e =>
        val err = if(e.getMessage == "Boxed Error" && e.getCause != null) e.getCause else e
        HttpResponse(500, err.getStackTrace.mkString(err.getMessage + "\n", "\n", ""))
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
        println("Creation of node failed: " + t.getMessage)
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
        println("Creation of node or connector failed: " + t.getMessage)
        Future.successful(HttpResponse(404, creationFailedMessage))
    }
  
  def jsonOk(js: JsValue) =  HttpResponse(200, HttpEntity(contentType = ContentType(`application/json`, `UTF-8`), string = js.toString))

  implicit def nodeWriter[N <: Node[_]] = new JsonWriter[N] {
    def write(node: N): JsValue =
      JsObject("type" -> JsString(node.protocol), "id" -> JsString(node.idStr))
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
        case MacAddress(address) => address map { b =>
          val s = (b.toLong & 0xff).toHexString
          if(s.length == 2) s else '0' + s
        } mkString ":"
        case other => other.value.toString
      }
      JsObject("value" -> JsString(value))
    }
  }

  implicit def propertiesWrite = new JsonWriter[Set[Property[_]]] {
    def write(properties: Set[Property[_]]): JsValue =
      JsObject(properties.toList map (p => p.name -> propertyWriter.write(p)) )
  }
}