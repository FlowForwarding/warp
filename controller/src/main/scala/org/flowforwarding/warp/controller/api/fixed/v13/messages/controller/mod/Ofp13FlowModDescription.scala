/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod

import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.ControllerMaxLength
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod.FlowModCommand.FlowModCommand
import org.flowforwarding.warp.controller.api.fixed.v13.messages.{Ofp13MessageInput, Ofp13MessageDescription}
import org.flowforwarding.warp.controller.api.fixed.util.Bitmap
import spire.math.{ULong, UShort, UInt}
import org.flowforwarding.warp.controller.api.fixed.v13.structures.instructions.{Instruction, Ofp13InstructionsDescription}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

object FlowModCommand extends Enumeration{
  type FlowModCommand = Value
  val Add, Modify, ModifyStrict, Delete, DeleteStrict = Value
}

case class FlowModFlags(sendFlowModRem: Boolean, checkOverlap: Boolean, resetCounts: Boolean, noPktCounts: Boolean, boBytCounts: Boolean) extends Bitmap

case class FlowModInput(cookie: ULong,             /* Opaque controller-issued identifier. */
                        cookieMask: ULong,         /* Mask used to restrict the cookie bits that must match when the command is
                                                     OFPFC_MODIFY* or OFPFC_DELETE*. A value of 0 indicates no restriction. */
                        tableId: UShort,           /* ID of the table to put the flow in. For OFPFC_DELETE_* commands, OFPTT_ALL
                                                     can also be used to delete matching flows from all tables. */
                        command: FlowModCommand,  /* One of OFPFC_*. */
                        idleTimeout: UInt,         /* Idle time before discarding (seconds). */
                        hardTimeout: UInt,         /* Max time before discarding (seconds). */
                        priority: UInt,            /* Priority level of flow entry. */
                        bufferId: ControllerMaxLength,       /* Buffered packet to apply to, or OFP_NO_BUFFER. Not meaningful for OFPFC_DELETE*. */
                        outPort: PortNumber,      /* For OFPFC_DELETE* commands, require matching entries to include this as an
                                                     output port. A value of OFPP_ANY indicates no restriction. */
                        outGroup: GroupId,        /* For OFPFC_DELETE* commands, require matching entries to include this as an output group.
                                                     A value of OFPG_ANY indicates no restriction. */
                        flags: FlowModFlags,      /* Bitmap of OFPFF_* flags. */
                        fmMatch: MatchInput,      /* Fields to match. Variable size. */
                        /* Instruction set - 0 or more. The length of the instruction  set is inferred from the length field in the header. */
                        instructions: Array[Instruction]) extends Ofp13MessageInput

private[fixed] trait Ofp13FlowModDescription extends Ofp13MessageDescription{
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _]] with Ofp13HeaderDescription with Ofp13MatchDescription with Ofp13InstructionsDescription =>

  private class FlowModBuilder extends OfpMessageBuilder[FlowModInput]{
    // Fills the underlying builder with the specified input.
    protected override def applyInput(input: FlowModInput): Unit = {
      super.applyInput(input)
      setMember("cookie", input.cookie)
      setMember("cookie_mask", input.cookieMask)
      setMember("table_id", input.tableId)
      setMember("command", input.command)
      setMember("idle_timeout", input.idleTimeout)
      setMember("hard_timeout", input.hardTimeout)
      setMember("priority", input.priority)
      setMember("buffer_id", input.bufferId.v)
      setMember("out_port", input.outPort.number)
      setMember("out_group", input.outGroup.id)
      setMember("flags", input.flags.bitmap)
      setMember("match", input.fmMatch)
      setMember("instructions", input.instructions)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): FlowModInput =
      FlowModInput("cookie",
                   "cookie_mask",
                   "table_id",
                   FlowModCommand("command"),
                   "idle_timeout",
                   "hard_timeout",
                   "priority",
                   ControllerMaxLength("buffer_id"),
                   PortNumber("out_port"),
                   GroupId("out_group"),
                   bitmap[FlowModFlags]("flags"),
                   structure[MatchInput]("match"),
                   "instructions")
  }

  protected abstract override def builderClasses = classOf[FlowModBuilder] :: super.builderClasses
}