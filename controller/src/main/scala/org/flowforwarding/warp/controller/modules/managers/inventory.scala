/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.managers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.flowforwarding.warp.controller.bus.{ServiceBus, ServiceRequest}
import org.flowforwarding.warp.controller.modules.managers.AbstractService._
import org.flowforwarding.warp.controller.modules.managers.sal.{NodeConnector, Node, Property}

object InventoryManager{

  // NOTE: containerName parameter is skipped since this feature is not implemented in warp

  trait InventoryServiceRequest extends ServiceRequest

  case class GetNodes() extends InventoryServiceRequest
  case class GetNodeConnectors(node: Node[_]) extends InventoryServiceRequest

  case class AddNodeProperty(node: Node[_], property: Property[_]) extends InventoryServiceRequest
  case class RemoveNodeProperty(node: Node[_], propertyName: String) extends InventoryServiceRequest
  case class GetNodeProperty(node: Node[_], propertyName: String) extends InventoryServiceRequest

  case class AddNodeConnectorProperty(connector: NodeConnector[_, _ <: Node[_]], property: Property[_]) extends InventoryServiceRequest
  case class RemoveNodeConnectorProperty(connector: NodeConnector[_, _ <: Node[_]], propertyName: String) extends InventoryServiceRequest
  //case class GetNodeConnectorProperty(connector: NodeConnector[_, _ <: Node[_]], propertyName: String) extends InventoryServiceRequest

  //case object SaveConfiguration extends ServiceRequest

  case class Nodes(nodes: Map[Node[_], Set[Property[_]]]) extends ServiceResponse
  case class Connectors(connectors: Map[NodeConnector[_, _ <: Node[_]], Set[Property[_]]]) extends ServiceResponse
  case class PropertyValue(p: Property[_]) extends ServiceResponse
}

import InventoryManager._

class InventoryManager(val bus: ServiceBus) extends AbstractManager[InventoryServiceRequest] {

  private def reduceNodes(nodes: Array[Any]) = {
    if (nodes contains InvalidParams) InvalidParams
    else Nodes(nodes.foldLeft(Map.empty[Node[_], Set[Property[_]]]) { case (ids1, Nodes(ids2)) => ids1 ++ ids2})
  }

  private def reduceNodeConnectors(connectors: Array[Any]) =
    connectors collectFirst {
      case c: Connectors => c
    } getOrElse {
      InvalidParams
    }

  private def reduceNodeProperties(properties: Array[Any]) =
    properties collectFirst {
      case c: PropertyValue => c
    } getOrElse {
      InvalidParams
    }

  override protected def handleRequest(e: ServiceRequest): Future[Any] = e match {
    case GetNodes()                                           => askAll(new GetNodes()                                           with Broadcast) map reduceNodes
    case GetNodeConnectors(node)                              => askAll(new GetNodeConnectors(node)                              with Broadcast) map reduceNodeConnectors

    case AddNodeProperty(node, property)                      => askAll(new AddNodeProperty(node, property)                      with Broadcast) map reduceServiceResponses
    case RemoveNodeProperty(node, propertyName)               => askAll(new RemoveNodeProperty(node, propertyName)               with Broadcast) map reduceServiceResponses
    case GetNodeProperty(node, propertyName)                  => askAll(new GetNodeProperty(node, propertyName)                  with Broadcast) map reduceNodeProperties

    case AddNodeConnectorProperty(connector, property)        => askAll(new AddNodeConnectorProperty(connector, property)        with Broadcast) map reduceServiceResponses
    case RemoveNodeConnectorProperty(connector, propertyName) => askAll(new RemoveNodeConnectorProperty(connector, propertyName) with Broadcast) map reduceServiceResponses
    //case GetNodeConnectorProperty(connector, propertyName)    => askAll(new GetNodeConnectorProperty(connector, propertyName)    with Broadcast) map reduceCommandResponses
  }
}

trait InventoryService[NodeType <: Node[_], ConnectorType <: NodeConnector[_, NodeType]] extends AbstractService[NodeType, ConnectorType] {
  self: NodeTag[NodeType, ConnectorType] =>

  val handleRequestImpl: PartialFunction[ServiceRequest, Future[Any]] = {
    case GetNodes()                                                                        => getNodes()
    case GetNodeConnectors(node)                              if checkNode(node)           => getNodeConnectors(castNode(node))

    case AddNodeProperty(node, property)                      if checkNode(node)           => addNodeProperty(castNode(node), property)
    case RemoveNodeProperty(node, propertyName)               if checkNode(node)           => removeNodeProperty(castNode(node), propertyName)
    case GetNodeProperty(node, propertyName)                  if checkNode(node)           => getNodeProperty(castNode(node), propertyName)

    case AddNodeConnectorProperty(connector, property)        if checkConnector(connector) => addNodeConnectorProperty(castConnector(connector), property)
    case RemoveNodeConnectorProperty(connector, propertyName) if checkConnector(connector) => removeNodeConnectorProperty(castConnector(connector), propertyName)
    //case GetNodeConnectorProperty(connector, propertyName) if checkConnector(connector)    => getNodeConnectorProperty(castConnector(connector), propertyName)
  }

  def getNodes(): Future[ServiceResponse]
  def getNodeConnectors(node: NodeType): Future[ServiceResponse]

  def addNodeProperty(node: NodeType, property: Property[_]): Future[ServiceResponse]
  def removeNodeProperty(node: NodeType, propertyName: String): Future[ServiceResponse]
  def getNodeProperty(node: NodeType, propertyName: String): Future[ServiceResponse]

  def addNodeConnectorProperty(connector: ConnectorType, property: Property[_]): Future[ServiceResponse]
  def removeNodeConnectorProperty(connector: ConnectorType, propertyName: String): Future[ServiceResponse]
  //def getNodeConnectorProperty(connector: ConnectorType, propertyName: String): Future[ServiceResponse]
}