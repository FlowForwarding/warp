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
import org.flowforwarding.warp.controller.bus.{ServiceRequest, ControllerBus}
import org.flowforwarding.warp.controller.modules.managers._, AbstractService._
import org.flowforwarding.warp.controller.modules.managers.sal._
import org.flowforwarding.warp.controller.api.fixed.{IncomingMessagePredicate, BuilderInput}
import org.flowforwarding.warp.controller.api.fixed.v13.Ofp13MessageHandlers
import org.flowforwarding.warp.controller.api.fixed.v13.messages.async.{PortReason, PortStatus}
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.FeaturesReply
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.Port
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.data._
import org.flowforwarding.warp.controller.api.dynamic.DynamicStructureBuilder

private [v13] object Ofp13InventoryMessages{
  trait Ofp13InventoryServiceRequest extends ServiceRequest with ProtocolInternal

  case class GetOfp13Nodes() extends Ofp13InventoryServiceRequest
  case class GetOfp13NodeConnectors(node: OFNode) extends Ofp13InventoryServiceRequest

  case class Ofp13Nodes(nodes: Map[OFNode, Set[Property[_]]]) extends ServiceResponse
  case class Ofp13Connectors(connectors: Map[OFNodeConnector, Set[Property[_]]]) extends ServiceResponse
}

class Ofp13InventoryService(controllerBus: ControllerBus) extends Ofp13MessageHandlers(controllerBus) with InventoryService[OFNode, OFNodeConnector] with Ofp13Tag {
  import InventoryMessages._
  import Ofp13InventoryMessages._

  override def started() = {
    super.started()
    subscribe("inventory") {
      val filter = new IncomingMessagePredicate {
        def test(dpid: ULong, payload: Any) = {
          payload match {
            case _: PortStatus | _: FeaturesReply => true
            case mp: MultipartReply =>
              mp.body.isInstanceOf[PortDescriptionReplyBody] ||
              mp.body.isInstanceOf[SwitchDescriptionReplyBody]
          }
        }
      }
      testIncomingMessage { filter } orElse { case _: SwitchConnector.SwitchDisconnected => true }
    }
  }

  private var nodeProps = Map[OFNode, Set[Property[_]]]()
  private var nodeConnectorsProps = Map[OFNodeConnector, Set[Property[_]]]()

  override def handleRequestImpl: PartialFunction[ServiceRequest, Future[Any]] = super.handleRequestImpl orElse {
    case GetOfp13Nodes() => Future.successful(Ofp13Nodes(nodeProps))
    case GetOfp13NodeConnectors(node) => Future.successful(Ofp13Connectors(nodeConnectorsProps.filter(_._1.node == node)))
  }

  override def handleDisconnected(api: DynamicStructureBuilder[_], dpid: ULong): Unit = {
    nodeProps = nodeProps - OFNode(dpid)
    nodeConnectorsProps = nodeConnectorsProps filter { case (OFNodeConnector(_, _, OFNode(id)), _) => id != dpid }
  }

  override def onFeaturesReply(dpid: ULong, msg: FeaturesReply): Array[BuilderInput] = {
    nodeProps = nodeProps.updated(OFNode(dpid), toProps(msg))
    Array(MultipartRequestInput(false, SwitchDescriptionRequestBodyInput()),
          MultipartRequestInput(false, PortDescriptionRequestBodyInput()))
  }

  override def onPortStatus(dpid: ULong, msg: PortStatus): Array[BuilderInput] = {
    val node = OFNode(dpid)
    val nodeConnector = OFNodeConnector(msg.port.number.number, "OF", node)
    msg.reason match {
      case PortReason.ADD =>
      case PortReason.MODIFY =>
        val props = toProps(msg.port)
        nodeConnectorsProps.updated(nodeConnector, props)
      case PortReason.DELETE =>
        nodeConnectorsProps = nodeConnectorsProps -nodeConnector
    }
    Array.empty
  }

  override def onSwitchDescriptionReply(dpid: ULong, desc: SwitchDescription): Array[BuilderInput] = {
    nodeProps.updated(OFNode(dpid), Description(desc.datapath)) // TODO: add another descriptions?
    Array.empty
  }

  override def onPortDescriptionReply(dpid: ULong, desc: Array[Port]): Array[BuilderInput] = {
    val node = OFNode(dpid)
    nodeConnectorsProps ++= desc map (port => (OFNodeConnector(port.number.number, "OF", node), toProps(port)))
    Array.empty
  }

  private def toProps(port: Port): Set[Property[_]] = Set(
    AdvertisedBandwidth(port.advertised.bitmap),
    SupportedBandwidth(port.supported.bitmap),
    PeerBandwidth(port.peer.bitmap),
    Config(port.config.bitmap),
    State(port.state.bitmap),
    MaxSpeed(port.maxSpeed),
    MAC(port.hwAddress.bytes),
    Name(port.name))

  private def toProps(features: FeaturesReply): Set[Property[_]] = Set(
    TimeStamp(System.currentTimeMillis()),
    MAC(deriveMacAddress(features.datapathId)),
    Tables(features.tablesCount),
    Capabilities(features.capabilities.bitmap),
    //SupportedFlowActions        send another multipart request?  Group Descriptions?
    Buffers(features.buffersCount))

  private def deriveMacAddress(dpid: ULong) = java.nio.ByteBuffer.allocate(8).putLong(dpid.toLong).array().drop(2)

  override def getNodes(): Future[ServiceResponse] = {
    Future.successful(Nodes(nodeProps))
  }

  override def getNodeConnectors(node: OFNode): Future[ServiceResponse] = {
    Future.successful(Connectors(nodeConnectorsProps.filter(_._1.node == node)))
  }

  //TODO: ensure all the properties are acceptable
  override def addNodeProperty(node: OFNode, property: Property[_]): Future[ServiceResponse] = {
    nodeProps.updated(node, nodeProps(node) + property)
    Future.successful[ServiceResponse](Done)
  }

  override def removeNodeProperty(node: OFNode, propertyName: String): Future[ServiceResponse] = {
    nodeProps.updated(node, nodeProps(node) filterNot (propertyName==))
    Future.successful[ServiceResponse](Done)
  }

  override def addNodeConnectorProperty(connector: OFNodeConnector, property: Property[_]) = {
    nodeConnectorsProps.updated(connector, nodeConnectorsProps(connector) + property)
    Future.successful[ServiceResponse](Done)
  }

  override def removeNodeConnectorProperty(connector: OFNodeConnector, propertyName: String) = {
    nodeConnectorsProps.updated(connector, nodeConnectorsProps(connector) filterNot (propertyName==))
    Future.successful[ServiceResponse](Done)
  }
}