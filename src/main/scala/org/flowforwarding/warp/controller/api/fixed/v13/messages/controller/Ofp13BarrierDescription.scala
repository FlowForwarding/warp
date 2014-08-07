package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller

import spire.math._

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.Ofp13HeaderDescription
import org.flowforwarding.warp.controller.api.fixed.v13.messages.{Ofp13MessageInput, Ofp13Message, Ofp13MessageDescription}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

trait BarrierReply extends Ofp13Message

case class BarrierRequestInput() extends Ofp13MessageInput

private[fixed] trait BarrierHandler{
  /* Barrier message has no speciefic data */
  def onBarrier(dpid: ULong, msg: BarrierReply): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13BarrierDescription extends Ofp13MessageDescription{
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with BarrierHandler] with Ofp13HeaderDescription =>

  private class BarrierBuilder extends OfpMessageBuilder[BarrierRequestInput]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): BarrierRequestInput = BarrierRequestInput()
  }

  private class BarrierStructure(s: DynamicStructure) extends OfpMessage[BarrierReply](s) with BarrierReply

  protected abstract override def builderClasses = classOf[BarrierBuilder] :: super.builderClasses
  protected abstract override def messageClasses = classOf[BarrierStructure] :: super.messageClasses
}