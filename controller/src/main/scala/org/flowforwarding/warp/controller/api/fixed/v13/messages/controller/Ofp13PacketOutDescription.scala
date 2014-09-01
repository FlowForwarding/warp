/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller

import spire.math._

import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.{Ofp13HeaderDescription, PortNumber}
import org.flowforwarding.warp.controller.api.fixed.v13.messages.{Ofp13MessageInput, Ofp13MessageDescription}
import org.flowforwarding.warp.controller.api.fixed.v13.structures.actions.{Ofp13ActionsDescription, Action}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

case class ControllerMaxLength(v: UShort)
object Max extends ControllerMaxLength(UShort(0xffff))
object NoBuffer extends ControllerMaxLength(UShort(0xffff))

case class PacketOutInput(bufferId: ControllerMaxLength, inPort: PortNumber, actions: Array[Action], data: Array[Byte]) extends Ofp13MessageInput

private[fixed] trait Ofp13PacketOutDescription extends Ofp13MessageDescription{
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _]] with Ofp13HeaderDescription with Ofp13ActionsDescription =>

  private class PacketOutBuilder extends OfpMessageBuilder[PacketOutInput]{
    // Fills the underlying builder with the specified input.
    protected override def applyInput(input: PacketOutInput): Unit = {
      super.applyInput(input)
      setMember("buffer_id", input.bufferId.v)
      setMember("in_port", input.inPort.number)
      setMember("actions", input.actions)
      setMember("data", input.data)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): PacketOutInput =
      PacketOutInput(ControllerMaxLength("buffer_id"), PortNumber("in_port"), "actions", "data")
  }

  protected abstract override def builderClasses = classOf[PacketOutBuilder] :: super.builderClasses
}