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

object InventoryMessages{

  // NOTE: containerName parameter is skipped since this feature is not implemented in warp

  trait InventoryServiceRequest extends ServiceRequest

  case class GetNodes() extends InventoryServiceRequest
  case class GetNodeConnectors(node: Node[_]) extends InventoryServiceRequest

  case class AddNodeProperty(node: Node[_], property: Property[_]) extends InventoryServiceRequest
  case class RemoveNodeProperty(node: Node[_], propertyName: String) extends InventoryServiceRequest

  case class AddNodeConnectorProperty(connector: NodeConnector[_, _ <: Node[_]], property: Property[_]) extends InventoryServiceRequest
  case class RemoveNodeConnectorProperty(connector: NodeConnector[_, _ <: Node[_]], propertyName: String) extends InventoryServiceRequest

  case object SaveConfiguration extends InventoryServiceRequest  // TODO: Implement

  case class Nodes(nodes: Map[_ <: Node[_], Set[Property[_]]]) extends ServiceResponse
  case class Connectors(connectors: Map[_ <: NodeConnector[_, _ <: Node[_]], Set[Property[_]]]) extends ServiceResponse
  case class PropertyValue(p: Property[_]) extends ServiceResponse
}

import InventoryMessages._

class InventoryManager(val bus: ServiceBus) extends AbstractManager[InventoryServiceRequest] {

  private val reduceNodes = reduceResponses { responses =>
    Nodes(responses.collect { case ns: Nodes => ns }
                   .foldLeft(Map.empty[Node[_], Set[Property[_]]]) { case (ids1, Nodes(ids2)) => ids1 ++ ids2})
  } _

  override protected def handleRequest(e: ServiceRequest): Future[Any] = e match {
    case GetNodes()                                           => askAll(new GetNodes()                                             with Broadcast) map reduceNodes
    case GetNodeConnectors(node)                              => askFirst(new GetNodeConnectors(node)                              with Broadcast)

    case AddNodeProperty(node, property)                      => askFirst(new AddNodeProperty(node, property)                      with Broadcast)
    case RemoveNodeProperty(node, propertyName)               => askFirst(new RemoveNodeProperty(node, propertyName)               with Broadcast)

    case AddNodeConnectorProperty(connector, property)        => askFirst(new AddNodeConnectorProperty(connector, property)        with Broadcast)
    case RemoveNodeConnectorProperty(connector, propertyName) => askFirst(new RemoveNodeConnectorProperty(connector, propertyName) with Broadcast)
  }
}

trait InventoryService[NodeType <: Node[_], ConnectorType <: NodeConnector[_, NodeType]] extends AbstractService[NodeType, ConnectorType] {
  self: NodeTag[NodeType, ConnectorType] =>

  def handleRequestImpl: PartialFunction[ServiceRequest, Future[Any]] = {
    case GetNodes()                                                                        => getNodes()
    case GetNodeConnectors(node)                              if checkNode(node)           => getNodeConnectors(castNode(node))

    case AddNodeProperty(node, property)                      if checkNode(node)           => addNodeProperty(castNode(node), property)
    case RemoveNodeProperty(node, propertyName)               if checkNode(node)           => removeNodeProperty(castNode(node), propertyName)

    case AddNodeConnectorProperty(connector, property)        if checkConnector(connector) => addNodeConnectorProperty(castConnector(connector), property)
    case RemoveNodeConnectorProperty(connector, propertyName) if checkConnector(connector) => removeNodeConnectorProperty(castConnector(connector), propertyName)
  }

  def getNodes(): Future[ServiceResponse]
  def getNodeConnectors(node: NodeType): Future[ServiceResponse]

  def addNodeProperty(node: NodeType, property: Property[_]): Future[ServiceResponse]
  def removeNodeProperty(node: NodeType, propertyName: String): Future[ServiceResponse]

  def addNodeConnectorProperty(connector: ConnectorType, property: Property[_]): Future[ServiceResponse]
  def removeNodeConnectorProperty(connector: ConnectorType, propertyName: String): Future[ServiceResponse]
}