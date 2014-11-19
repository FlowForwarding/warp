/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.managers.impl.openflow.v13

import java.net.{Inet6Address, Inet4Address}

import scala.concurrent.Future

import spire.math.{UByte, UInt, UShort, ULong}

import org.flowforwarding.warp.controller.bus.ControllerBus
import org.flowforwarding.warp.controller.modules.managers._
import org.flowforwarding.warp.controller.modules.managers.AbstractService._
import org.flowforwarding.warp.controller.modules.managers.sal._

import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.util.{MacAddress, IPv6Address, IPv4Address}
import org.flowforwarding.warp.controller.api.fixed.v13.Ofp13MessageHandlers
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.NoBuffer
import org.flowforwarding.warp.controller.api.fixed.v13.messages.async.{Error, FlowRem}
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod.{FlowModFlags, FlowModCommand, FlowModInput}, FlowModCommand._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.{GroupId, PortNumber, MatchInput}
import org.flowforwarding.warp.controller.api.fixed.v13.structures.oxm_tlv._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.actions.Action
import org.flowforwarding.warp.controller.api.fixed.v13.structures.instructions.InstructionApplyActions

class Ofp13FlowsService(controllerBus: ControllerBus) extends Ofp13MessageHandlers(controllerBus)
                                                         with FlowsService[OFNode, OFNodeConnector]
                                                         with Ofp13Tag
                                                         with FixedStructuresSender {
  import FlowsMessages._

  override def started() = {
    super.started()
    subscribe("connections") {
      val filter = new IncomingMessagePredicate {
        def test(dpid: ULong, payload: Any) = {
          payload match { case _: FlowRem | _: Error  => true }
        }
      }
      testIncomingMessage { filter }
    }
  }

  private var flows = Map[OFNode, Seq[Flow]]()

  private val hasName = (name: String) => (flow: Flow) => flow.name contains name
  private def nodeFlow(node: OFNode, name: String) = flows.get(node) flatMap { _ find hasName(name) }

  override def onError(dpid: ULong, msg: Error): Array[BuilderInput] = {
    // TODO: check xid to ensure that it is really flow-related error
    log.debug(s"Flow error (${msg.message}}: dpid = $dpid, type = ${msg.errorType}}, code = ${msg.errorCode} ")
    Array.empty
  }

  override def onFlowRem(dpid: ULong, msg: FlowRem): Array[BuilderInput] = {
    val node = OFNode(dpid)
    // if there is a flow and for some reason it is still marked as installed, remove the mark
    flows(node) find {
      f => f.priority == Some(msg.priority) && f.matchFields == msg.m.fields.toSet && f.installInHw
    } foreach {
      f => flows = flows.updated(node, (flows(node) filterNot (f==)) :+ f.copy(installInHw = !f.installInHw))
    }
    log.debug(s"Flow removed: dpid = $dpid, reason = ${msg.reason}")
    Array.empty
  }

  override def getContainerFlows(): Future[ServiceResponse] = Future.successful(ContainerFlows(flows.toMap))

  override def getNodeFlows(node: OFNode): Future[ServiceResponse] = {
    flows.get(node) match {
      case Some(fs) => Future.successful(NodeFlows(fs))
      case None => Future.successful(NotFound)
    }
  }

  private def withValidation(node: OFNode, flow: Flow, afterValidation: (OFNode, Flow) => ServiceResponse) = {
    val ethertypeSet = flow.matchFields exists { case _: EtherType        => true; case _ => false }
    val protocolSet  = flow.matchFields exists { case _: Protocol         => true; case _ => false }
    val nwAddressSet = flow.matchFields exists { case _: NwSrc | _: NwDst => true; case _ => false }
    val portsSet     = flow.matchFields exists { case _: TpSrc | _: TpDst => true; case _ => false }

    if (nwAddressSet && !ethertypeSet) { // network address check
      InvalidParams("The match on network source or destination address cannot be accepted if the match on proper ethertype is missing")
    }
    else if (protocolSet && !ethertypeSet) { // transport protocol check
      InvalidParams("The match on network protocol cannot be accepted if the match on proper ethertype is missing")
    }
    else if (portsSet && (!ethertypeSet || !protocolSet)) { // transport ports check
      InvalidParams("The match on transport source or destination port cannot be accepted if the match on network protocol and match on IP ethertype are missing")
    }
    else afterValidation(node, flow)
  }

  override def addFlow(node: OFNode, flow: Flow): Future[ServiceResponse] = Future.successful {
    withValidation(node, flow, (n, f) => {
      def nameConflict(f1: Flow)(f2: Flow) = f1.name == f2.name
      def matchConflict(f1: Flow)(f2: Flow) = f1.priority == f2.priority && f1.matchFields == f2.matchFields
      val (res, updatedFlows) = flows.get(n) match {
        case Some(fs) if fs exists nameConflict(f) =>
          (Conflict("Entry with this name on specified switch already exists"), fs)
        case Some(fs) if fs exists matchConflict(f) =>
          (Conflict("A flow with same match and priority exists on the target node"), fs)
        case Some(fs) =>
          (Done, fs :+ flow)
        case None =>
          (Done, Seq(f))
      }
      flows = flows.updated(n, updatedFlows)
      if(res == Done && f.installInHw)
        installFlow(n, f)
      res
    })
  }

  override def getFlow(node: OFNode, flowName: String): Future[ServiceResponse] =
    nodeFlow(node, flowName) match {
      case Some(f) => Future.successful(NodeFlow(f))
      case None => Future.successful(NotFound)
    }

  override def removeFlow(node: OFNode, flowName: String): Future[ServiceResponse] = {
    nodeFlow(node, flowName) match {
      case Some(f) =>
        uninstallFlow(node, f)
        flows = flows.updated(node, flows(node) filterNot hasName(flowName))
        Future.successful(Done)
      case _ =>
        Future.successful(NotFound)
    }
  }

  private def convertAction: PartialFunction[org.flowforwarding.warp.controller.modules.managers.sal.Action,
                                             org.flowforwarding.warp.controller.api.fixed.v13.structures.actions.Action] = {
    case Output(nc: OFNodeConnector)
                         => Action.output(PortNumber(nc.id), NoBuffer)
    //case Drop          () => do nothing
    case Loopback()      => Action.output(PortNumber.InPort)
    case Flood()         => Action.output(PortNumber.FloodPort)
    case FloodAll()      => Action.output(PortNumber.AllPorts)
    case Controller()    => Action.output(PortNumber.ControllerPort)
    //case Interface     () =>
    case SoftwarePath()  => Action.output(PortNumber.LocalPort)
    case HarwarePath()   => Action.output(PortNumber.NormalPort)
    case Enqueue(qid)    => Action.setQueue(qid)

    case SetDlType(t)    => Action.setField(eth_type(t))
    case SetDlSrc(data)  => Action.setField(eth_src(MacAddress(data)))
    case SetDlDst(data)  => Action.setField(eth_dst(MacAddress(data)))

    case SetVlan(vid)    => Action.setField(vlan_vid(vid))
    case SetVlanPcp(pcp) => Action.setField(vlan_pcp(pcp))
    //case SetVlanCif   () => how to handle in ofp 1.3?

    case PopVlan()       => Action.popVlan
    case PushVlan(t)     => Action.pushVlan(t)

    case SetNwSrc(addr)  =>
      val oxm = addr match {
        case a: Inet4Address => ipv4_src(IPv4Address(a.getAddress))
        case a: Inet6Address => ipv6_src(IPv6Address(a.getAddress))
      }
      Action.setField(oxm)
    case SetNwDst(addr)  =>
      val oxm = addr match {
        case a: Inet4Address => ipv4_dst(IPv4Address(a.getAddress))
        case a: Inet6Address => ipv6_dst(IPv6Address(a.getAddress))
      }
      Action.setField(oxm)
    case SetNwTos(tos)   => Action.setField(ip_dscp(tos))

    case SetTpSrc(port)  => Action.setField(tcp_src(port))
    case SetTpDst(port)  => Action.setField(tcp_dst(port))

    //case SetNextHop   () => not supported by OF?
  }

  private def convertMatchField: PartialFunction[MatchField, OxmTlv[_]] = {
    case IngressPort(nc: OFNodeConnector) => in_port(nc.id)

    case VlanId(value)       => vlan_vid(value)
    case VlanPriority(value) => vlan_pcp(value)

    case EtherType(value)    => eth_type(value)
    case DlSrc(value)        => eth_src(MacAddress(value))
    case DlDst(value)        => eth_dst(MacAddress(value))

    case Protocol(value)     => ip_proto(value)
    case TosBits(value)      => ip_dscp(value)

    case TpSrc(value)        => tcp_src(value)
    case TpDst(value)        => tcp_dst(value)

    case NwSrc(value) => value match {
      case a: Inet4Address => ipv4_src(IPv4Address(a.getAddress))
      case a: Inet6Address => ipv6_src(IPv6Address(a.getAddress))
    }
    case NwDst(value) => value match {
      case a: Inet4Address => ipv4_dst(IPv4Address(a.getAddress))
      case a: Inet6Address => ipv6_dst(IPv6Address(a.getAddress))
    }
  }

  private def convertFlow(f: Flow, command: FlowModCommand, port: PortNumber, groupId: GroupId) = {
    val Flow(id, _, _,  priority, idleTimeout, hardTimeout, matchFields, actions) = f
    FlowModInput(
      id.fold(ULong(0)) { ULong.apply },
      ULong(0),
      UShort(0),
      command,
      idleTimeout getOrElse UInt(0),
      hardTimeout getOrElse UInt(0),
      priority.fold(UInt(0)) { p => UInt(p.signed) },
      NoBuffer,
      port,
      groupId,
      FlowModFlags(true, false, false, false, false),  // Instruct switch to let controller know when flow is removed
      MatchInput(true, (matchFields collect convertMatchField).toArray),
      Array(InstructionApplyActions((actions collect convertAction).toArray))
    )
  }

  private def sendToSwitch(n: OFNode, msg: BuilderInput) =
    sendBuilderInput(UByte(4), n.id, msg, false)

  private def installFlow(n: OFNode, f: Flow) = {
    val msg = convertFlow(f, Add, PortNumber.AllPorts, GroupId.AllGroups)
    sendToSwitch(n, msg)
  }

  private def uninstallFlow(n: OFNode, f: Flow) = {
    val msg = convertFlow(f, Delete, PortNumber.AnyPort, GroupId.AnyGroup)  // are port and group correct?
    sendToSwitch(n, msg)
  }

  override def toggleFlow(node: OFNode, flowName: String): Future[ServiceResponse] = {
    nodeFlow(node, flowName) match {
      case Some(f) =>
        if(f.installInHw)
          uninstallFlow(node, f)
        else
          installFlow(node, f)
        flows = flows.updated(node, (flows(node) filterNot hasName(flowName)) :+ f.copy(installInHw = !f.installInHw))
        Future.successful(Done)
      case _ =>
        Future.successful(NotFound)
    }
  }
}