/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.symmetric

import org.flowforwarding.warp.driver_api.fixed.SpecificVersionMessageHandlersSet
import spire.math._

import org.flowforwarding.warp.driver_api.dynamic.{DynamicStructure, DynamicStructureBuilder}
import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.Ofp13HeaderDescription

import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.{Ofp13MessageInput, Ofp13Message, Ofp13MessageDescription}
import org.flowforwarding.warp.driver_api.fixed.text_view.BITextView

trait EchoRequest extends Ofp13Message{
  def elements: Array[Byte]
}

case class EchoRequestInput(elements: Array[Byte]) extends Ofp13MessageInput

trait EchoReply extends Ofp13Message{
  def elements: Array[Byte]
}

case class EchoReplyInput(elements: Array[Byte]) extends Ofp13MessageInput

private[fixed] trait EchoHandler{
  def onEchoRequest(dpid: ULong, msg: EchoRequest): Array[BuilderInput] = Array.empty[BuilderInput]
  def onEchoReply(dpid: ULong, msg: EchoReply): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13EchoDescription extends Ofp13MessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _] with EchoHandler] with Ofp13HeaderDescription =>

  private class EchoRequestBuilder extends OfpMessageBuilder[EchoRequestInput]{
    // Fills the underlying builder with the specified input.
    override protected def applyInput(input: EchoRequestInput): Unit = {
      super.applyInput(input)
      setMember("elements", input.elements)
    }

    // TODO: Implement in all subclasses
    override private[fixed] def inputFromTextView(implicit input: BITextView): EchoRequestInput = EchoRequestInput("elements")
  }

  private class EchoRequestStructure(s: DynamicStructure) extends OfpMessage[EchoRequest](s) with EchoRequest{
    def elements: Array[Byte] = bytes("elements")
  }

  private class EchoReplyBuilder extends OfpMessageBuilder[EchoReplyInput]{
    // Fills the underlying builder with the specified input.
    override protected def applyInput(input: EchoReplyInput): Unit = {
      super.applyInput(input)
      setMember("elements", input.elements)
    }

    // TODO: Implement in all subclasses
    override private[fixed] def inputFromTextView(implicit input: BITextView): EchoReplyInput = EchoReplyInput("elements")
  }

  private class EchoReplyStructure(s: DynamicStructure) extends OfpMessage[EchoReply](s) with EchoReply{
    def elements: Array[Byte] = bytes("elements")
  }

  protected abstract override def builderClasses = classOf[EchoReplyBuilder] :: classOf[EchoRequestBuilder] :: super.builderClasses
  protected abstract override def messageClasses = classOf[EchoReplyStructure] :: classOf[EchoRequestStructure] :: super.messageClasses
}


