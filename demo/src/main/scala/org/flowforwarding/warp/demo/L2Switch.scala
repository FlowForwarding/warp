/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.demo

import spire.math.{UShort, ULong, UInt}
import spire.syntax.literals._

import org.flowforwarding.warp.controller.bus.ControllerBus
import org.flowforwarding.warp.driver_api.fixed.BuilderInput
import org.flowforwarding.warp.controller.message_handlers.fixed.IncomingMessagePredicate
import org.flowforwarding.warp.driver_api.fixed.util.MacAddress

import org.flowforwarding.warp.driver_api.fixed.openflow.v13.Ofp13MessageHandlers
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.async.PacketIn
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.{PacketOutInput, Max, FeaturesReply}
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.mod.{FlowModFlags, FlowModInput, FlowModCommand}
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.PortNumber
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.PortNumber._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.GroupId._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.instructions._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.actions.Action
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.oxm_tlv._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.MatchInput

// Simple layer-2 learning switch logic using OpenFlow Protocol v1.3.
class L2Switch(controllerBus: ControllerBus) extends Ofp13MessageHandlers(controllerBus){

  override protected def started(): Unit = {
    subscribe("l2messages") { testIncomingMessage {
      new IncomingMessagePredicate {
        def test(dpid: ULong, payload: Any) = payload.isInstanceOf[PacketIn] || payload.isInstanceOf[FeaturesReply]
      }
    }}
  }

  // Handle switch features reply to install table miss flow entries.
  override def onFeaturesReply(dpid: ULong, msg: FeaturesReply): Array[BuilderInput] =
    Array(installTableMiss(uh"0"), installTableMiss(uh"1"))

  private def create_flow_mod(_priority: UInt, _tableId: UShort, _match: MatchInput, _instructions: Instruction*) =
    FlowModInput(
      cookie = ul"0",
      cookieMask = ul"0",
      tableId = _tableId,
      command = FlowModCommand.Add,
      idleTimeout = ui"0",
      hardTimeout = ui"0",
      priority = _priority,
      bufferId = Max,
      outPort = AnyPort,
      outGroup = AnyGroup,
      flags = FlowModFlags(false, false, false, false, false),
      fmMatch = _match,
      instructions = _instructions.toArray)

  // Create table miss flow entries.
  private def installTableMiss(missTableId: UShort): BuilderInput = {
    val output = Action.output(ControllerPort, Max)
    val write = new InstructionWriteActions(Array(output))
    val emptyMatch = new MatchInput(true, Array.empty)
    create_flow_mod(ui"0", missTableId, emptyMatch, write)
  }

  override def onPacketIn(dpid: ULong, packetIn: PacketIn) = {
    val oxmFields = packetIn.m.fields
    val response =
      for{inPort <- oxmFields collectFirst { case in_port(v)    => v }
          ethSrc <- oxmFields collectFirst { case eth_src(v, _) => v }
          ethDst <- oxmFields collectFirst { case eth_dst(v, _) => v }} yield {
        if(packetIn.tableId == uh"0")
          println("Installing new source mac received from port " + inPort)
        Array(
          // Install flow entries
          install_src_entry(inPort, ethSrc),
          install_dst_entry(inPort, ethSrc),
          // Flood
          flood(packetIn.data))
    }
    response getOrElse Array.empty[BuilderInput]
  }

  // Send a packet_out with output to all ports.
  private def flood(data: Array[Byte]): BuilderInput = {
    val outputAll = Action.output(AllPorts)
    PacketOutInput(Max, ControllerPort, Array(outputAll), data)
  }

  // Install flow entry matching on eth_src in table 0.
  private def install_src_entry(inPort: UInt, ethSrc: MacAddress): BuilderInput = {
    val m = MatchInput(true, Array(in_port(inPort), eth_src(ethSrc)))
    val goto = InstructionGotoTable(ub"1")
    create_flow_mod(ui"123", uh"0", m, goto)
  }

  // Install flow entry matching on eth_dst in table 1.
  private def install_dst_entry(inPort: UInt, ethSrc: MacAddress): BuilderInput = {
    val m =  MatchInput(true, Array(eth_dst(ethSrc)))
    val output = Action.output(PortNumber(inPort))
    val write = InstructionWriteActions(Array(output))
    create_flow_mod(ui"123", uh"1", m, write)
  }
}
