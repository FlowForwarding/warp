/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.rest.northbound

import java.math.BigInteger
import java.net.InetAddress
import java.util.concurrent.TimeoutException

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import spire.math.ULong

import spray.json._
import spray.http._

import HttpCharsets._
import MediaTypes._

import org.flowforwarding.warp.controller.bus.ControllerBus
import org.flowforwarding.warp.controller.modules.rest.RestApiService
import org.flowforwarding.warp.controller.modules.managers.{ConnectionManager, ConnectionService}
import org.flowforwarding.warp.controller.modules.managers.ConnectionManager.NodeNotFound


// TODO: check authorization
class ConnectionManagerNorthbound(val bus: ControllerBus, serverPrefix: String) extends RestApiService(serverPrefix) {
  import ConnectionManager._

  override val servicePrefix = "/connectionmanager"

  override def route =
    //  PUT /node/{nodeId}/address/{ipAddress}/port/{port}
    path("node" / Segment / "address" / Segment / "port" / IntNumber) { (id, ip, port) =>
      put { complete(handleConnect(ULong(stringToLong(id)), InetAddress.getByName(ip), port, None)) }
    } ~
    // DELETE /node/{nodeType}/{nodeId}
    path("node" / Segment / Segment) { (nodeType, nodeId) =>
      delete { complete(handleDisconnect(ULong(stringToLong(nodeId)))) }
    } ~
    // PUT /node/{nodeType}/{nodeId}/address/{ipAddress}/port/{port}
    path("node" / Segment / Segment / "address" / Segment / "port" / IntNumber) { (`type`, id, ip, port) =>
      put { complete(handleConnect(ULong(stringToLong(id)), InetAddress.getByName(ip), port, Some(`type`))) }
    } ~
    // GET /nodes
    path("nodes") {
      get {
        parameters('controller ?) { controllerAddress =>
          complete(handleGetNodes(controllerAddress map InetAddress.getByName))
        }
      }
    }

  def stringToLong(values: String): Long =
    new BigInteger(values.replaceAll(":", ""), 16).longValue

  def longToHexString(value: Long): String = {
    val arr = value.toHexString.toCharArray
    val padded = Seq.fill(16 - arr.length)('0') ++ arr
    padded.grouped(2).map(_ mkString "").mkString(":")
  }

  private def aggregateNodes(nodes: Set[(ULong, String)]) = {
    val jsNodes = nodes map { case (id, t) => JsObject(("type", JsString(t)), ("id", JsString(longToHexString(id.toLong)))) }
    JsObject(("node", JsArray(jsNodes.toList)))
  }

  private val handleError: PartialFunction[Throwable, HttpResponse]  = {
    case e: TimeoutException => HttpResponse(503, "Connection Manager Service not available")
    case e => HttpResponse(500, e.getStackTrace.mkString(e.getMessage, "\n", ""))
  }

  private def handleConnect(nodeId: ULong, ip: InetAddress, port: Int, nodeType: Option[String]): Future[HttpResponse] = {
    askFirst(Connect(nodeId, ip, port, nodeType)) map {
      case Done          => HttpResponse(200, "Node connected successfully")
      case InvalidParams => HttpResponse(404, "Invalid IP Address or Port parameter passed")
      case NodeNotFound  => HttpResponse(406, "Could not connect to the Node with the specified parameters")
    }
  } recover handleError

  private def handleDisconnect(nodeId: ULong): Future[HttpResponse] = askFirst(Disconnect(nodeId)) map {
    case Done         => HttpResponse(200, "Node disconnected successfully")
    case NodeNotFound => HttpResponse(404, "Could not find a connection with the specified Node identifier")
  } recover handleError

  private def handleGetNodes(controllerAddress: Option[InetAddress]): Future[HttpResponse] = askFirst(GetNodes(controllerAddress)) map {
    case Nodes(nodes)  => HttpResponse(200, HttpEntity(contentType = ContentType(`application/json`, `UTF-8`), string = aggregateNodes(nodes).toString))
    case InvalidParams => HttpResponse(406, "Invalid Controller IP Address passed")
  } recover handleError
}
