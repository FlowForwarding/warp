/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.rest.northbound

import scala.util.{Try, Failure, Success}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import spray.json._
import spray.http._

import org.flowforwarding.warp.controller.bus.ControllerBus
import org.flowforwarding.warp.controller.modules.managers.{FlowsManager, AbstractService}
import org.flowforwarding.warp.controller.modules.managers.sal.{Node, Flow}
import org.flowforwarding.warp.controller.modules.rest.RestApiService
import org.flowforwarding.warp.controller.modules.rest.northbound.NorthboundUtils._


// TODO: check authorization
class FlowProgrammerNorthbound(val bus: ControllerBus, serverPrefix: String) extends RestApiService(serverPrefix) {
  import FlowsManager._
  import AbstractService._

  override val servicePrefix = "/flowprogrammer"

  override def route =
    //  GET /{containerName}
    path(Segment) { (containerName) =>
      get {
        complete(handleGetContainerFlows())
      }
    } ~
    // GET /{containerName}/node/{nodeType}/{nodeId}
    path(Segment / "node" / Segment / Segment) { (containerName, nodeType, nodeId) =>
      get {
        complete(handleGetNodeFlows(nodeType, nodeId))
      }
    } ~
    // /{containerName}/node/{nodeType}/{nodeId}/staticFlow/{name}
    path( Segment / "node" / Segment / Segment / "staticFlow" / Segment) { (containerName, `type`, id, flowName) =>
      get {
        complete(handleGetFlow(`type`, id, flowName))
      } ~
      put {
        entity(as[String]) { request => complete(handleAddFlow(`type`, id, flowName, request)) }
      } ~
      delete {
        complete(handleRemoveFlow(`type`, id, flowName))
      } ~
      post {
        complete(handleToggleFlow(`type`, id, flowName))
      }
    }

  private val pn = processNode("One or more of Controller Services are unavailable", "The containerName or NodeId or Configuration name is not found") _

  def flowToJs(node: Node[_])(flow: Flow)  = {
    JsObject((node, flow).toJson.asInstanceOf[JsObject].fields + ("node" -> node.toJson))
  }

  def handleGetContainerFlows() =
    askFirst(GetContainerFlows()) map {
      case ContainerFlows(flows) =>
        val jsFlows = flows flatMap { case (n, fs) => fs map flowToJs(n) }
        jsonOk(JsObject("flowConfig" -> JsArray(jsFlows.toList)))
      case NotFound => HttpResponse(404, "The containerName is not found")
    } withServiceErrorReport "One or more of Controller Services are unavailable"

  def handleGetNodeFlows(nodeType: String, nodeId: String) =
    pn(nodeType, nodeId) { n =>
      askFirst(GetNodeFlows(n)) map {
        case NodeFlows(flows) => jsonOk(JsObject("flowConfig" -> JsArray(flows map flowToJs(n): _*)))
        case NotFound => HttpResponse(404, "The containerName or NodeId or Configuration name is not found")
      }
    }

  def handleGetFlow(nodeType: String, nodeId: String, flowName: String) =
    pn(nodeType, nodeId) { n =>
      askFirst(GetFlow(n, flowName)) map {
        case NodeFlow(flow) => jsonOk(flowToJs(n)(flow))
        case NotFound => HttpResponse(404, "The containerName or NodeId or Configuration name is not found")
      }
    }

  def handleAddFlow(nodeType: String, nodeId: String, flowName: String, flowData: String) =
    pn(nodeType, nodeId) { n =>
      Try { flowData.parseJson.convertTo[(Node[_], Flow)] } match {
        case Success((_, flow)) =>
          askFirst(AddFlow(n, flow.copy(name = Some(flowName)))) map {
            case Done =>          HttpResponse(200, "Static Flow modified successfully") // 201 Created	Flow Config processed successfully
            case NotFound =>      HttpResponse(404, "The Container Name or nodeId is not found")
            case NotAcceptable => HttpResponse(406, "Cannot operate on Default Container when other Containers are active")
            case Conflict =>      HttpResponse(409, "Failed to create Static Flow entry due to Conflicting Name or configuration")
            case InvalidParams => HttpResponse(500,	"Failed to create Static Flow entry. Failure Reason included in HTTP Error response")
          }
        case Failure(f) =>
          f.printStackTrace()
          Future.successful(HttpResponse(400,	"Failed to create Static Flow entry due to invalid flow configuration"))
      }
    }

  def handleRemoveFlow(nodeType: String, nodeId: String, flowName: String) =
    pn(nodeType, nodeId) { n =>
      askFirst(RemoveFlow(n, flowName)) map {
        case Done =>          HttpResponse(204, "Flow Config deleted successfully")
        case NotFound =>      HttpResponse(404, "The Container Name or Node-id or Flow Name passed is not found")
        case NotAcceptable => HttpResponse(406, "Failed to delete Flow config due to invalid operation. Failure details included in HTTP Error response")
        //  500 Internal Server Error	Failed to delete Flow config. Failure Reason included in HTTP Error response
      }
    }

  def handleToggleFlow(nodeType: String, nodeId: String, flowName: String) =
    pn(nodeType, nodeId) { n =>
      askFirst(ToggleFlow(n, flowName)) map {
        case Done =>          HttpResponse(200, "Flow Config processed successfully")
        case NotFound =>      HttpResponse(404, "The Container Name or Node-id or Flow Name passed is not found")
        case NotAcceptable => HttpResponse(406, "Failed to delete Flow config due to invalid operation. Failure details included in HTTP Error response")
        //  500 Internal Server Error	Failed to delete Flow config. Failure Reason included in HTTP Error response
      }
    }
}
