/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.managers

import java.net.InetAddress

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.flowforwarding.warp.controller.bus.{ServiceBus, ServiceRequest}
import org.flowforwarding.warp.controller.modules.Service
import org.flowforwarding.warp.controller.modules.managers.AbstractService._
import org.flowforwarding.warp.controller.driver_interface.MessageDriverFactory

object ConnectionManager{
  trait ConnectionServiceRequest extends ServiceRequest
  case class ConnectWithUnknownType(id: String, ip: InetAddress, port: Int) extends ConnectionServiceRequest
  case class Connect(node: Node[_], ip: InetAddress, port: Int) extends ConnectionServiceRequest
  case class Disconnect(node: Node[_]) extends ConnectionServiceRequest
  case class GetNodes(controllerAddress: Option[InetAddress]) extends ConnectionServiceRequest

  case class Nodes(ids: Seq[Node[_]]) extends ServiceResponse
}

class ConnectionManager(val bus: ServiceBus) extends Service {
  import ConnectionManager._

  private def reduceCommandResponses(iterable: Array[Any]) =
    if (iterable contains Done) Done
    else if (iterable forall { _ == NodeNotFound }) NodeNotFound
    else InvalidParams

  private def reduceNodes(nodes: Array[Any]) =
    if (nodes contains InvalidParams) InvalidParams
    else Nodes(nodes.foldLeft(Seq.empty[Node[_]]) { case (ids1, Nodes(ids2)) => ids1 ++ ids2 })

  override protected def handleRequest(e: ServiceRequest): Future[Any] = e match {
    case Connect(node, ip, port) => askAll(new Connect(node, ip, port) with Broadcast) map reduceServiceResponses
    case Disconnect(node)        => askAll(new Disconnect(node)        with Broadcast) map reduceServiceResponses
    case GetNodes(address)       => askAll(new GetNodes(address)       with Broadcast) map reduceNodes
  }

  override protected def compatibleWith(factory: MessageDriverFactory[_]): Boolean = true

  override def started() = { registerService { case req: ConnectionServiceRequest => !req.isInstanceOf[Broadcast] } }
}

trait ConnectionService[NodeType <: Node[_], ConnectorType <: NodeConnector[_, NodeType]] extends AbstractService[NodeType, ConnectorType] {
  self: NodeTag[NodeType, ConnectorType] =>
  import ConnectionManager._

  val handleRequestImpl: PartialFunction[ServiceRequest, Future[Any]] = {
    case Connect(n, ip, port) if checkNode(n) => connect(castNode(n), ip, port)
    case Disconnect(n) if checkNode(n) => disconnect(castNode(n))
    case GetNodes(address) => getNodes(address)
  }

  def connect(node: NodeType, ip: InetAddress, port: Int): Future[ServiceResponse]
  def disconnect(node: NodeType): Future[ServiceResponse]
  def getNodes(controllerAddress: Option[InetAddress]): Future[ServiceResponse]
}