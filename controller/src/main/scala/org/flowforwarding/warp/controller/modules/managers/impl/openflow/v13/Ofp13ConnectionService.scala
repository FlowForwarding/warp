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

import org.flowforwarding.warp.controller.modules.managers.AbstractService._

import org.flowforwarding.warp.controller.SwitchConnector
import org.flowforwarding.warp.controller.bus.ControllerBus
import org.flowforwarding.warp.controller.modules.managers._
import org.flowforwarding.warp.controller.api.fixed.v13.Ofp13MessageHandlers
import org.flowforwarding.warp.controller.api.dynamic.DynamicStructureBuilder


class Ofp13ConnectionService(controllerBus: ControllerBus) extends Ofp13MessageHandlers(controllerBus) with ConnectionService[OFNode, OFNodeConnector] with Ofp13Tag {
  import ConnectionManager._

  private val connectedDpids = scala.collection.mutable.Set[ULong]()

  def connect(node: OFNode, ip: InetAddress, port: Int): Future[ServiceResponse] = Future.successful(NodeNotFound)

  def disconnect(node: OFNode): Future[ServiceResponse] = {
    val result = if(connectedDpids.contains(node.id)){
      publishMessage(SwitchConnector.ForceDisconnect(node.id))
      Done
    }
    else NodeNotFound
    Future.successful(result)
  }

  def getNodes(controllerAddress: Option[InetAddress]): Future[ServiceResponse] =
    Future.successful(Nodes((connectedDpids map OFNode).toSeq))

  override def started() = {
    super.started()
    subscribe("connections") {
      case _: SwitchConnector.SwitchHandshake | _: SwitchConnector.SwitchDisconnected => true
    }
  }

  override def handleHandshake(api: DynamicStructureBuilder[_], dpid: ULong): Unit = connectedDpids += dpid

  override def handleDisconnected(api: DynamicStructureBuilder[_], dpid: ULong): Unit = connectedDpids -= dpid
}