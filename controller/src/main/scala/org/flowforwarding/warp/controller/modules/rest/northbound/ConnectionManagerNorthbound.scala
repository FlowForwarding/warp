/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.rest.northbound

import java.net.InetAddress

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import spray.json._
import spray.http._

import HttpCharsets._
import MediaTypes._

import org.flowforwarding.warp.controller.bus.ControllerBus
import org.flowforwarding.warp.controller.modules.rest.RestApiService
import org.flowforwarding.warp.controller.modules.managers.{ConnectionMessages, AbstractService}
import org.flowforwarding.warp.controller.modules.rest.northbound.NorthboundUtils._

import scala.util.{Failure, Success}

// TODO: check authorization
class ConnectionManagerNorthbound(val bus: ControllerBus, serverPrefix: String) extends RestApiService(serverPrefix) {
  import ConnectionMessages._
  import AbstractService._

  override val servicePrefix = "/connectionmanager"

  override def route =
    //  PUT /node/{nodeId}/address/{ipAddress}/port/{port}
    path("node" / Segment / "address" / Segment / "port" / IntNumber) { (id, ip, port) =>
      put {
        complete(handleConnect("OF", id, InetAddress.getByName(ip), port))
      }
    } ~
    // DELETE /node/{nodeType}/{nodeId}
    path("node" / Segment / Segment) { (nodeType, nodeId) =>
      delete {
        complete(handleDisconnect(nodeType, nodeId))
      }
    } ~
    // PUT /node/{nodeType}/{nodeId}/address/{ipAddress}/port/{port}
    path("node" / Segment / Segment / "address" / Segment / "port" / IntNumber) { (`type`, id, ip, port) =>
      put {
        complete(handleConnect(`type`, id, InetAddress.getByName(ip), port))
      }
    } ~
    // GET /nodes
    path("nodes") {
      get {
        parameters('controller ?) { controllerAddress =>
          complete(handleGetNodes(controllerAddress map InetAddress.getByName))
        }
      }
    }

  private val pn = processNode("Connection Manager Service not available", "Could not connect to the Node with the specified parameters") _

  private def handleConnect(nodeType: String, nodeId: String, ip: InetAddress, port: Int): Future[HttpResponse] =
    pn(nodeType, nodeId) { n =>
      askFirst(Connect(n, ip, port)) map {
        case Done => HttpResponse(200, "Node connected successfully")
        case InvalidParams => HttpResponse(406, "Invalid IP Address or Port parameter passed")
        case NotFound => HttpResponse(404, "Could not connect to the Node with the specified parameters")
      }
    }

  private def handleDisconnect(nodeType: String, nodeId: String): Future[HttpResponse] =
    pn(nodeType, nodeId) { n =>
      askFirst(Disconnect(n)) map {
        case Done => HttpResponse(200, "Node disconnected successfully")
        case NotFound => HttpResponse(404, "Could not find a connection with the specified Node identifier")
      }
    }

  private def handleGetNodes(controllerAddress: Option[InetAddress]): Future[HttpResponse] =
    askFirst(GetNodes(controllerAddress)) map {
      case Nodes(nodes) =>
        val jsNodes = nodes map { _.toJson }
        jsonOk(JsObject("node" -> JsArray(jsNodes.toList)))
      case InvalidParams => HttpResponse(406, "Invalid Controller IP Address passed")
    } withServiceErrorReport "Connection Manager"
}
