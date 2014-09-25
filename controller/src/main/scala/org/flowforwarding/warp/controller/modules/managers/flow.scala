/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.managers

import org.flowforwarding.warp.controller.modules.managers
import org.flowforwarding.warp.controller.modules.managers.sal._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.flowforwarding.warp.controller.bus.{ServiceBus, ServiceRequest}
import org.flowforwarding.warp.controller.modules.managers.AbstractService._

object FlowsManager{
  trait FlowProgrammerServiceRequest extends ServiceRequest

  case class GetContainerFlows()                         extends FlowProgrammerServiceRequest
  case class GetNodeFlows(node: Node[_])                 extends FlowProgrammerServiceRequest
  case class GetFlow(node: Node[_], flowName: String)    extends FlowProgrammerServiceRequest
  case class AddFlow(node: Node[_], flow: Flow)          extends FlowProgrammerServiceRequest
  case class RemoveFlow(node: Node[_], flowName: String) extends FlowProgrammerServiceRequest
  case class ToggleFlow(node: Node[_], flowName: String) extends FlowProgrammerServiceRequest

  case class ContainerFlows(flows: Map[Node[_], Seq[Flow]]) extends ServiceResponse
  case class NodeFlows(flows: Seq[Flow]) extends ServiceResponse
  case class NodeFlow(flow: Flow) extends ServiceResponse
}

import FlowsManager._

class FlowsManager(val bus: ServiceBus) extends AbstractManager[FlowProgrammerServiceRequest] {
  import InventoryManager._
  import AbstractService._

  private def reduceContainerFlows(flows: Array[Any]) = {
    if (flows contains InvalidParams) InvalidParams
    else if (flows.nonEmpty && (flows forall { _ == NotFound })) NotFound
    else ContainerFlows(flows.collect { case cfs: ContainerFlows => cfs }
                             .foldLeft(Map.empty[Node[_], Seq[Flow]]) { case (fs1, ContainerFlows(fs2)) => fs1 ++ fs2 })
  }

  private def reduceNodeFlows(flows: Array[Any]) = {
    if (flows contains InvalidParams) InvalidParams
    else if (flows.nonEmpty && (flows forall { _ == NotFound })) NotFound
    else NodeFlows(flows.collect { case nfs: NodeFlows => nfs }
                        .foldLeft(Seq.empty[Flow]) { case (fs1, NodeFlows(fs2)) => fs1 ++ fs2 })
  }

  private def reduceNodeFlow(flows: Array[Any]) = {
    flows collectFirst { case nf: NodeFlow => nf } getOrElse NotFound
  }

  private def withValidation(node: Node[_], flow: Flow, afterValidation: (Node[_], Flow) => Future[ServiceResponse]) = {
    val matchInPort = flow.matchFields collectFirst { case IngressPort(nc) => nc }
    val actionsPorts = flow.actions collect {
      // we have default container only
      // case (_ : Flood | _: FloodAll) if !GlobalConstants.DEFAULT.toString().equals(getContainerName()) => ???
      case Output(nc) =>  nc
      case Enqueue(nc) => nc
    }

    val checkNodePresence = askFirst(GetNodes()) map {
      case Nodes(nodes) => nodes contains node
    }

    val checkPortsPresence = askFirst(GetNodeConnectors(node)) map {
      case Connectors(cs) => (actionsPorts ++ matchInPort) forall {
        case nc: NodeConnector[_, _] => cs contains nc
        case _ => false
      }
    }

    Future.sequence(Seq(checkNodePresence, checkPortsPresence)) flatMap {
      case Seq(false, _)   =>
        println("Node is not present in this container")
        Future.successful(InvalidParams) //String.format("Node %s is not present in this container", node));
      case Seq(_, false)   =>
        println("Port is not present in this container")
        Future.successful(InvalidParams) //String.format("Port %s is not present in this container", node));
      case Seq(true, true) => afterValidation(node, flow)
    }
  }

  override protected def handleRequest(e: ServiceRequest): Future[Any] = e match {
    case GetContainerFlows()        => askAll(new GetContainerFlows()        with Broadcast) map reduceContainerFlows
    case GetNodeFlows(node)         => askAll(new GetNodeFlows(node)         with Broadcast) map reduceNodeFlows
    case GetFlow(node, flowName)    => askAll(new GetFlow(node, flowName)    with Broadcast) map reduceNodeFlow // expects one-element sequence
    case AddFlow(node, flow)        => withValidation(node, flow, (n, f) => this.askAll(new AddFlow(n, f) with Broadcast) map reduceServiceResponses)
    case RemoveFlow(node, flowName) => askAll(new RemoveFlow(node, flowName) with Broadcast) map reduceServiceResponses
    case ToggleFlow(node, flowName) => askAll(new ToggleFlow(node, flowName) with Broadcast) map reduceServiceResponses
  }
}

trait FlowsService[NodeType <: Node[_], ConnectorType <: NodeConnector[_, NodeType]] extends AbstractService[NodeType, ConnectorType] {
  self: NodeTag[NodeType, ConnectorType] =>

  val handleRequestImpl: PartialFunction[ServiceRequest, Future[Any]] = {
    case GetContainerFlows()                     => getContainerFlows()
    case GetNodeFlows(n)         if checkNode(n) => getNodeFlows(castNode(n))
    case GetFlow(n, flowName)    if checkNode(n) => getFlow(castNode(n), flowName)
    case AddFlow(n, flow)        if checkNode(n) => addFlow(castNode(n), flow)
    case RemoveFlow(n, flowName) if checkNode(n) => removeFlow(castNode(n), flowName)
    case ToggleFlow(n, flowName) if checkNode(n) => toggleFlow(castNode(n), flowName)
  }

  def getContainerFlows(): Future[ServiceResponse]
  def getNodeFlows(node: NodeType): Future[ServiceResponse]
  def getFlow(node: NodeType, flowName: String): Future[ServiceResponse]
  def addFlow(node: NodeType, flow: Flow): Future[ServiceResponse]
  def removeFlow(node: NodeType, flowName: String): Future[ServiceResponse]
  def toggleFlow(node: NodeType, flowName: String): Future[ServiceResponse]
}