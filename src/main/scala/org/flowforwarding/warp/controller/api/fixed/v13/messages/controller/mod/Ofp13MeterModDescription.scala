package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod

import spire.math._

import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod.MeterModCommand.MeterModCommand
import org.flowforwarding.warp.controller.api.fixed.v13.messages.{Ofp13MessageInput, Ofp13MessageDescription}
import org.flowforwarding.warp.controller.api.fixed.util.Bitmap
import org.flowforwarding.warp.controller.api.fixed.v13.structures.meter_bands.{MeterBand, Ofp13MeterBandsDescription}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

object MeterModCommand extends Enumeration{
  type MeterModCommand = Value
  val Add, Modify, Delete = Value
}

case class MeterModFlags(
  KBPS: Boolean,  /* Rate value in kb/s (kilo-bit per second). */
  PKTPS: Boolean, /* Rate value in packet/sec. */
  Burst: Boolean, /* Do burst size. */
  Stats: Boolean  /* Collect statistics. */
) extends Bitmap

case class MeterModInput(command: MeterModCommand, flags: MeterModFlags, meterId: MeterId, bands: Array[MeterBand]) extends Ofp13MessageInput

private[fixed] trait Ofp13MeterModDescription extends Ofp13MessageDescription{
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _]] with Ofp13HeaderDescription with Ofp13MeterBandsDescription =>

  private class MeterModBuilder extends OfpMessageBuilder[MeterModInput]{
    // Fills the underlying builder with the specified input.
    protected override def applyInput(input: MeterModInput): Unit = {
      super.applyInput(input)
      setMember("command", input.command)
      setMember("flags", input.flags.bitmap)
      setMember("meter_id", input.meterId.id)
      setMember("bands", input.bands)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): MeterModInput =
      MeterModInput(
        MeterModCommand("command"),
        bitmap[MeterModFlags]("flags"),
        MeterId("meter_id"),
        "bands")
  }

  protected abstract override def builderClasses = classOf[MeterModBuilder] :: super.builderClasses
}