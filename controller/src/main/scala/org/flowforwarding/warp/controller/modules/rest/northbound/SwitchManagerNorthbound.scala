/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.rest.northbound

import org.flowforwarding.warp.controller.modules.managers.sal.Property

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import spray.json._
import spray.http._

import org.flowforwarding.warp.controller.bus.ControllerBus
import org.flowforwarding.warp.controller.modules.managers._
import org.flowforwarding.warp.controller.modules.rest.RestApiService
import org.flowforwarding.warp.controller.modules.rest.northbound.NorthboundUtils._

import scala.util.{Failure, Success}

// TODO: check authorization
class SwitchManagerNorthbound(val bus: ControllerBus, serverPrefix: String) extends RestApiService(serverPrefix) {

  import InventoryMessages._
  import AbstractService._

  override val servicePrefix = "/switchmanager"

  override def route =
    // GET /{containerName}/nodes
    path(Segment / "nodes") { containerName =>
      get {
        complete(handleGetNodes())
      }
    } ~
    // POST /{containerName}/save
    path(Segment / "save") { containerName =>
      post {
        complete(handleSave())
      }
    } ~
    // GET /{containerName}/node/{nodeType}/{nodeId}     
    path(Segment / "node" / Segment / Segment) { (containerName, ntype, nid) =>
      get {
        complete(handleGetNodeConnectors(ntype, nid))
      }
    } ~
    // DELETE /{containerName}/node/{nodeType}/{nodeId}/property/{propertyName}    
    path(Segment / "node" / Segment / Segment / "property" / Segment)  { (containerName, ntype, nid, pname) =>
      delete {
        complete(handleRemoveNodeProperty(ntype, nid, pname))
      }
    } ~
    // PUT /{containerName}/node/{nodeType}/{nodeId}/property/{propertyName}/{propertyValue}   
    path(Segment / "node" / Segment / Segment / "property" / Segment / Segment) { (containerName, ntype, nid, name, value) =>
      put {
        complete(handleAddNodeProperty(ntype, nid, name, value))
      }
    } ~
    // DELETE /{containerName}/nodeconnector/{nodeType}/{nodeId}/{nodeConnectorType}/{nodeConnectorId}/property/{propertyName}    
    path(Segment / "nodeconnector" / Segment / Segment / Segment / Segment / "property" / Segment) { (containerName, ntype, nid, ctype, cid, name) =>
      delete {
        complete(handleRemoveNodeConnectorProperty(ntype, nid, ctype, cid, name))
      }
    } ~
    // PUT /{containerName}/nodeconnector/{nodeType}/{nodeId}/{nodeConnectorType}/{nodeConnectorId}/property/{propertyName}/{propertyValue}  
    path(Segment / "nodeconnector" / Segment / Segment / Segment / Segment / "property" / Segment / Segment) { (containerName, ntype, nid, ctype, cid, name, value) =>
      put {
        complete(handleAddNodeConnectorProperty(ntype, nid, ctype, cid, name, value))
      }
    }

  private val pn = processNode("Inventory Manager", "The Container Name or node or configuration name is not found") _
  private val pnc = processNodeConnector("Inventory Manager", "The Container Name or node or configuration name is not found") _

  def handleGetNodes(): Future[HttpResponse] = {
    askFirst(GetNodes()) map {
      case Nodes(nodes) =>
        val allProps = nodes map { case (n, props) => JsObject("node" -> n.toJson, "properties" -> props.toJson)}
        jsonOk(JsObject("nodeProperties" -> JsArray(allProps.toList)))
      case InvalidParams(msg) =>
        HttpResponse(406, msg)
    } withServiceErrorReport "Inventory Manager"
  }

  def handleGetNodeConnectors(nodeType: String, nodeId: String): Future[HttpResponse] =
    pn(nodeType, nodeId) { n =>
      askFirst(GetNodeConnectors(n)) map {
        case Connectors(connectors) =>
          val allProps = connectors map { case (c, props) =>
            JsObject("nodeconnector" -> c.toJson, "properties" -> props.toJson) }
          jsonOk(JsObject("nodeConnectorProperties" -> JsArray(allProps.toList)))
        case NotFound => HttpResponse(404, "Could not find a connection with the specified Node identifier")
      } withServiceErrorReport "Inventory Manager"
    }

  def handleAddNodeProperty(nodeType: String, nodeId: String, propertyName: String, propertyValue: String): Future[HttpResponse] =
    pn(nodeType, nodeId) { n =>
      Property(propertyName, propertyValue) match {
        case Success(p) =>
          askFirst(AddNodeProperty(n, p)) map {
            case Done =>               HttpResponse(201, "Operation successful")
            case NotFound =>           HttpResponse(404, "The Container Name or nodeId or configuration name is not found")
            case NotAcceptable(msg) => HttpResponse(406, msg)
            case Conflict(msg) =>      HttpResponse(409, msg)
          }
        case Failure(f) =>
          Future.successful(HttpResponse(404, "Wrong property"))
      }
    }

  def handleRemoveNodeProperty(nodeType: String, nodeId: String, propertyName: String): Future[HttpResponse] = pn(nodeType, nodeId) { n =>
    askFirst(RemoveNodeProperty(n, propertyName)) map {
      case Done =>     HttpResponse(204, "Property removed successfully")
      case NotFound => HttpResponse(404, "The Container Name or nodeId or configuration name is not found")
    }
  }

  def handleAddNodeConnectorProperty(nodeType: String, nodeId: String,
                                     connectorType: String, connectorId: String,
                                     propertyName: String, propertyValue: String): Future[HttpResponse] =
    pnc(nodeType, nodeId, connectorType, connectorId) { nc =>
      Property(propertyName, propertyValue) match {
        case Success(p) =>
          askFirst(AddNodeConnectorProperty(nc, p)) map {
            case Done =>     HttpResponse(201, "Operation successful")
            case NotFound => HttpResponse(404, "The Container Name or nodeId or configuration name is not found")
          }
        case Failure(f) =>
          Future.successful(HttpResponse(404, "Wrong property"))
      }
    }

  def handleRemoveNodeConnectorProperty(nodeType: String, nodeId: String,
                                        connectorType: String, connectorId: String,
                                        propertyName: String): Future[HttpResponse] =
    pnc(nodeType, nodeId, connectorType, connectorId) { n =>
      askFirst(RemoveNodeConnectorProperty(n, propertyName)) map {
        case Done =>     HttpResponse(204, "Property removed successfully")
        case NotFound => HttpResponse(404, "The Container Name or nodeId or configuration name is not found")
      }
    }

  def handleSave(): Future[HttpResponse] =
    Future.successful(HttpResponse(500, "Failed to save switch configuration. Action is not implemented."))
}
