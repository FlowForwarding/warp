/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.async

import org.flowforwarding.warp.driver_api.fixed.SpecificVersionMessageHandlersSet
import spire.math._

import org.flowforwarding.warp.driver_api.dynamic._
import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.async.PacketInReason.PacketInReason
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.{Ofp13HeaderDescription, Ofp13MatchDescription, Match}
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.{Ofp13Message, Ofp13MessageDescription}

object PacketInReason extends Enumeration{
  type PacketInReason = Value
  val OFPR_NO_MATCH,           /* No matching flow (table-miss flow entry). */
      OFPR_ACTION,             /* Action explicitly output to controller. */
      OFPR_INVALID_TTL = Value /* Packet has invalid TTL */
}

trait PacketIn extends Ofp13Message{
  val bufferId: UInt           /* ID assigned by datapath. */
  val totalLen: UInt           /* Full length of frame. */
  val reason: PacketInReason  /* Reason packet is being sent (one of OFPR_*) */
  val tableId: UShort          /* ID of the table that was looked up */
  val cookie: ULong            /* Cookie of the flow entry that was looked up. */
  val m: Match                /* Packet metadata. Variable size. */
  val data: Array[Byte]       /* Ethernet frame */
}

private[fixed] trait PacketInHandler{
  def onPacketIn(dpid: ULong, msg: PacketIn): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13PacketInDescription extends Ofp13MessageDescription{
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _] with PacketInHandler] with Ofp13HeaderDescription with Ofp13MatchDescription =>

  private class PacketInStructure(s: DynamicStructure) extends OfpMessage[PacketIn](s) with PacketIn {
    val bufferId: UInt = primitiveField[UInt]("buffer_id")
    val totalLen: UInt = primitiveField[UInt]("total_len")
    val reason: PacketInReason = enumField(PacketInReason, "reason")
    val tableId: UShort = primitiveField[UShort]("table_id")
    val cookie: ULong = primitiveField[ULong]("cookie")
    val m: Match = structureField[Match]("match")

    val data: Array[Byte] = bytes("data")
  }

  protected abstract override def messageClasses = classOf[PacketInStructure] :: super.messageClasses
}