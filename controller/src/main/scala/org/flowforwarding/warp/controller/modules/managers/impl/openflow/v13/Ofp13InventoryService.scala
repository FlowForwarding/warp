/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.managers.impl.openflow.v13

import scala.concurrent.Future

import spire.math.ULong

import org.flowforwarding.warp.controller.SwitchConnector
import org.flowforwarding.warp.controller.bus.ControllerBus
import org.flowforwarding.warp.controller.modules.managers._, AbstractService._
import org.flowforwarding.warp.controller.api.fixed.{IncomingMessagePredicate, BuilderInput}
import org.flowforwarding.warp.controller.api.fixed.v13.Ofp13MessageHandlers
import org.flowforwarding.warp.controller.api.fixed.v13.messages.async.{PortReason, PortStatus}
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.FeaturesReply
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.Port
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.data._
import org.flowforwarding.warp.controller.api.dynamic.DynamicStructureBuilder

class Ofp13InventoryService(controllerBus: ControllerBus) extends Ofp13MessageHandlers(controllerBus) with InventoryService[OFNode, OFNodeConnector] with Ofp13Tag {
  import InventoryManager._
  import scala.collection.mutable.{Map => MMap}

  val nodeConnectorsProps = MMap[OFNodeConnector, Set[Property[_]]]()
  val nodeProps = MMap[OFNode, Set[Property[_]]]()

  override def started() = {
    super.started()
    subscribe("inventory") {
      testIncomingMessage {
        new IncomingMessagePredicate {
          def test(dpid: ULong, payload: Any) = {
            payload match {
              case _: PortStatus | _: FeaturesReply => true
              case mp: MultipartReply =>
                mp.data.isInstanceOf[PortDescriptionReplyData] ||
                mp.data.isInstanceOf[SwitchDescriptionReplyData]
            }
          }
        }
      } orElse {
        case _: SwitchConnector.SwitchDisconnected => true
      }}
  }

  override def handleDisconnected(api: DynamicStructureBuilder[_], dpid: ULong): Unit = {
    nodeProps remove OFNode(dpid)
    nodeConnectorsProps retain { case (OFNodeConnector(_, _, OFNode(id)), _) => id != dpid }
  }

  override def onFeaturesReply(dpid: ULong, msg: FeaturesReply): Array[BuilderInput] = {
    nodeProps(OFNode(dpid)) = toProps(msg)
    Array(MultipartRequestInput(PortDescriptionRequestDataInput(false)),
          MultipartRequestInput(SwitchDescriptionRequestDataInput(false)))
  }

  override def onPortStatus(dpid: ULong, msg: PortStatus): Array[BuilderInput] = {
    val node = OFNode(dpid)
    val nodeConnector = OFNodeConnector(msg.port.number.number, "OF", node)
    msg.reason match {
      case PortReason.ADD =>
      case PortReason.MODIFY =>
        val props = toProps(msg.port)
        nodeConnectorsProps(nodeConnector) = props
      case PortReason.DELETE =>
        nodeConnectorsProps.remove(nodeConnector)
    }
    Array.empty
  }

  override def onSwitchDescriptionReply(dpid: ULong, msg: SwitchDescriptionReplyData): Array[BuilderInput] = {
    nodeProps(OFNode(dpid)) += Description(msg.body.datapath) // TODO: add another descriptions?
    Array.empty
  }

  override def onPortDescriptionReply(dpid: ULong, msg: PortDescriptionReplyData): Array[BuilderInput] = {
    val node = OFNode(dpid)
    nodeConnectorsProps ++= msg.body map (port => (OFNodeConnector(port.number.number, "OF", node), toProps(port)))
    Array.empty
  }

  private def toProps(port: Port): Set[Property[_]] = Set(
    AdvertisedBandwidth(port.advertised.bitmap),
    SupportedBandwidth(port.supported.bitmap),
    PeerBandwidth(port.peer.bitmap),
    Config(port.config.bitmap),
    State(port.state.bitmap),
    MaxSpeed(port.maxSpeed),
    MacAddress(port.hwAddress.bytes),
    Name(port.name))

  private def toProps(features: FeaturesReply): Set[Property[_]] = Set(
    TimeStamp(System.currentTimeMillis()),
    MacAddress(deriveMacAddress(features.datapathId)),
    Tables(features.tablesCount),
    Capabilities(features.capabilities.bitmap),
    //SupportedFlowActions        send another multipart request?  Group Descriptions?
    Buffers(features.buffersCount))

  private def deriveMacAddress(dpid: ULong) = java.nio.ByteBuffer.allocate(8).putLong(dpid.toLong).array().drop(2)

  override def getNodes(): Future[ServiceResponse] = {
    Future.successful(Nodes(nodeProps.toMap))
  }

  override def getNodeConnectors(node: OFNode): Future[ServiceResponse] = {
    Future.successful(Connectors(nodeConnectorsProps.filter(_._1.node == node).toMap))
  }

  override def getNodeProperty(node: OFNode, propertyName: String): Future[ServiceResponse] = {
   nodeProps.collectFirst { case (`node`, p) => p }
            .flatMap { props => props collectFirst { case p if p.name == propertyName => p } }
            .fold(Future.successful[ServiceResponse](NodeNotFound)) { p => Future.successful(PropertyValue(p)) }
  }

  //TODO: find out acceptable properties and implement methods
  override def addNodeProperty(node: OFNode, property: Property[_]): Future[ServiceResponse] = Future.successful[ServiceResponse](NotAcceptable)

  override def removeNodeProperty(node: OFNode, propertyName: String): Future[ServiceResponse] = Future.successful[ServiceResponse](NotAcceptable)

  override def addNodeConnectorProperty(connector: OFNodeConnector, property: Property[_]) = Future.successful[ServiceResponse](NotAcceptable)

  override def removeNodeConnectorProperty(connector: OFNodeConnector, propertyName: String) = Future.successful[ServiceResponse](NotAcceptable)
}