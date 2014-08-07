package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import spire.math.ULong
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

trait PortDescriptionReply extends Ofp13MultipartMultiValueMessage[Port]

case class PortDescriptionRequestInput(reqMore: Boolean) extends MultipartMessageEmptyBodyRequestInput

trait PortDescriptionReplyHandler{
  def onPortDescriptionReply(dpid: ULong, msg: PortDescriptionReply): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13DPortDescriptionDescription extends Ofp13MultipartMessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with PortDescriptionReplyHandler] with Ofp13HeaderDescription with Ofp13PortDescription =>

  class PortDescriptionRequestBuilder extends Ofp13MultipartMessageEmptyBodyRequestBuilder[PortDescriptionRequestInput]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): PortDescriptionRequestInput = PortDescriptionRequestInput("flags")
  }

  case class PortDescriptionReplyStructure(s: DynamicStructure)
    extends OfpMultipartMultiValueMessage[PortDescriptionReply, Port](s) with PortDescriptionReply

  abstract override def builderClasses = classOf[PortDescriptionRequestBuilder] :: super.builderClasses
  abstract override def messageClasses = classOf[PortDescriptionReplyStructure] :: super.messageClasses
}