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
import org.flowforwarding.warp.controller.modules.managers._
import org.flowforwarding.warp.controller.api.fixed.v13.Ofp13MessageHandlers
import org.flowforwarding.warp.controller.api.dynamic.DynamicStructureBuilder
import org.flowforwarding.warp.controller.modules.managers.AbstractService._
import org.flowforwarding.warp.controller.modules.managers.sal.{OFNodeConnector, OFNode}


class Ofp13ConnectionService(controllerBus: ControllerBus) extends Ofp13MessageHandlers(controllerBus) with ConnectionService[OFNode, OFNodeConnector] with Ofp13Tag {
  import ConnectionMessages._

  override def started() = {
    super.started()
    subscribe("connections") {
      case _: SwitchConnector.SwitchHandshake | _: SwitchConnector.SwitchDisconnected => true
    }
  }

  private var connectedDpids = Set[ULong]()

  def connect(node: OFNode, ip: InetAddress, port: Int): Future[ServiceResponse] = Future.successful(NotFound)

  def disconnect(node: OFNode): Future[ServiceResponse] = {
    val result = if(connectedDpids.contains(node.id)){
      askFirst(SwitchConnector.ForceDisconnect(node.id))
      Done
    }
    else NotFound
    Future.successful(result)
  }

  def getNodes(controllerAddress: Option[InetAddress]): Future[ServiceResponse] =
    Future.successful(Nodes((connectedDpids map OFNode).toSeq))

  override def handleHandshake(api: DynamicStructureBuilder[_], dpid: ULong): Unit = connectedDpids = connectedDpids + dpid

  override def handleDisconnected(api: DynamicStructureBuilder[_], dpid: ULong): Unit = connectedDpids = connectedDpids - dpid
}