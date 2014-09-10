/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.managers.impl.openflow.v13

import java.net.InetAddress

import scala.concurrent.Future

import spire.math.ULong

import org.flowforwarding.warp.controller.SwitchConnector
import org.flowforwarding.warp.controller.bus.ControllerBus
import org.flowforwarding.warp.controller.modules.managers.{ConnectionManager, ConnectionService}
import org.flowforwarding.warp.controller.api.fixed.{IncomingMessagePredicate, BuilderInput}
import org.flowforwarding.warp.controller.api.fixed.v13.Ofp13MessageHandlers
import org.flowforwarding.warp.controller.api.fixed.v13.messages.async.PortStatus
import org.flowforwarding.warp.controller.api.dynamic.DynamicStructureBuilder

class Ofp13ConnectionService(controllerBus: ControllerBus) extends Ofp13MessageHandlers(controllerBus) with ConnectionService {
  import ConnectionManager._

  private val connectedDpids = scala.collection.mutable.Set[ULong]()

  def onConnect(nodeId: ULong, ip: InetAddress, port: Int, nodeType: Option[String]): Future[ConnectionManagerResponse] = Future.successful(NodeNotFound)

  def onDisconnect(nodeId: ULong): Future[ConnectionManagerResponse] = {
    val result = if(connectedDpids.contains(nodeId)){
      publishMessage(SwitchConnector.ForceDisconnect(nodeId))
      Done
    }
    else NodeNotFound
    Future.successful(result)
  }

  def onGetNodes(controllerAddress: Option[InetAddress]): Future[ConnectionManagerResponse] = Future.successful(Nodes(Set(connectedDpids.map((_, "OF")).toSeq: _*)))

  override def started() = {
    super.started()
    subscribe("inventory") {
      testIncomingMessage {
        new IncomingMessagePredicate {
          def test(dpid: ULong, payload: Any) = payload.isInstanceOf[PortStatus]
        }
      } orElse {
        case _: SwitchConnector.SwitchHandshake | _: SwitchConnector.SwitchDisconnected => true
    }}
  }

  override def handleHandshake(api: DynamicStructureBuilder[_], dpid: ULong): Unit = connectedDpids += dpid

  override def handleDisconnected(api: DynamicStructureBuilder[_], dpid: ULong): Unit = connectedDpids -= dpid

  override def onPortStatus(dpid: ULong, msg: PortStatus): Array[BuilderInput] = super.onPortStatus(dpid, msg)
}