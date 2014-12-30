/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.opendaylight_rest.impl.openflow.v13

import java.net.InetAddress

import org.flowforwarding.warp.controller.SwitchConnector
import org.flowforwarding.warp.controller.bus.ControllerBus
import org.flowforwarding.warp.controller.modules.opendaylight_rest.managers.AbstractService._
import org.flowforwarding.warp.controller.modules.opendaylight_rest.managers._
import org.flowforwarding.warp.controller.modules.opendaylight_rest.sal.{OFNode, OFNodeConnector}
import org.flowforwarding.warp.driver_api.dynamic.DynamicStructureBuilder
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.Ofp13MessageHandlers
import spire.math.ULong

import scala.concurrent.Future

class Ofp13ConnectionService(controllerBus: ControllerBus) extends Ofp13MessageHandlers(controllerBus)
                                                              with ConnectionService[OFNode, OFNodeConnector]
                                                              with Ofp13Tag  {
  import org.flowforwarding.warp.controller.modules.opendaylight_rest.managers.ConnectionMessages._

  override def started() = {
    super.started()
    subscribe("connections") {
      case _: SwitchConnector.SwitchHandshake[_] | _: SwitchConnector.SwitchDisconnected[_] => true
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