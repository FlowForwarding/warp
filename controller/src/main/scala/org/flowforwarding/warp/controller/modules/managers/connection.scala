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

import spire.math.ULong

import org.flowforwarding.warp.controller.bus.{ServiceBus, ControllerBus, ServiceRequest}
import org.flowforwarding.warp.controller.modules.{MessageConsumer, Service}
import org.flowforwarding.warp.controller.driver_interface.MessageDriverFactory

object ConnectionManager{
  case class ConnectBroadcast(nodeId: ULong, ip: InetAddress, port: Int, nodeType: Option[String]) extends ServiceRequest
  case class DisconnectBroadcast(nodeId: ULong) extends ServiceRequest
  case class GetNodesBroadcast(controllerAddress: Option[InetAddress]) extends ServiceRequest

  case class Connect(nodeId: ULong, ip: InetAddress, port: Int, nodeType: Option[String]) extends ServiceRequest
  case class Disconnect(nodeId: ULong) extends ServiceRequest
  case class GetNodes(controllerAddress: Option[InetAddress]) extends ServiceRequest

  trait ConnectionManagerResponse
  case object Done extends ConnectionManagerResponse
  case object NodeNotFound extends ConnectionManagerResponse
  case object InvalidParams extends ConnectionManagerResponse
  case class Nodes(ids: Set[(ULong, String)]) extends ConnectionManagerResponse
}

class ConnectionManager(val bus: ServiceBus) extends Service {
  import ConnectionManager._

  private def reduceCommandResponses(iterable: Array[Any]) =
    if (iterable exists { _ == Done }) Done
    else if (iterable forall { _ == NodeNotFound }) NodeNotFound
    else InvalidParams

  private def reduceNodes(nodes: Array[Any]) =
    if (nodes exists { _ == InvalidParams }) InvalidParams
    else Nodes(nodes.foldLeft(Set.empty[(ULong, String)]) { case (ids1, Nodes(ids2)) => ids1 ++ ids2 })

  override protected def handleRequest(e: ServiceRequest): Future[Any] = e match {
    case Connect(nodeId, ip, port, nodeType) => askAll(ConnectBroadcast(nodeId, ip, port, nodeType)) map reduceCommandResponses
    case Disconnect(nodeId) => askAll(DisconnectBroadcast(nodeId)) map reduceCommandResponses
    case GetNodes(address) => askAll(GetNodesBroadcast(address)) map reduceNodes
  }

  override protected def compatibleWith(factory: MessageDriverFactory[_]): Boolean = true

  override def started() = { registerService { case req @ (_: Connect | _: Disconnect | _: GetNodes) => true } }
}

trait ConnectionService extends Service with MessageConsumer {
  import ConnectionManager._

  protected val bus: ControllerBus

  override protected def handleRequest(e: ServiceRequest): Future[Any] = e match {
    case ConnectBroadcast(nodeId, ip, port, nodeType) => onConnect(nodeId, ip, port, nodeType)
    case DisconnectBroadcast(nodeId) => onDisconnect(nodeId)
    case GetNodesBroadcast(address) => onGetNodes(address)
  }

  def onConnect(nodeId: ULong, ip: InetAddress, port: Int, nodeType: Option[String]): Future[ConnectionManagerResponse]
  def onDisconnect(nodeId: ULong): Future[ConnectionManagerResponse]
  def onGetNodes(controllerAddress: Option[InetAddress]): Future[ConnectionManagerResponse]

  override def started() = { registerService { case req @ (_: ConnectBroadcast | _: DisconnectBroadcast | _: GetNodesBroadcast) => true } }
}