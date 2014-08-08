package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller

import spire.math._

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.util._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.Ofp13HeaderDescription
import org.flowforwarding.warp.controller.api.fixed.v13.messages.{Ofp13MessageInput, Ofp13Message, Ofp13MessageDescription}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

case class ConfigFlags(normal: Boolean, drop: Boolean, reasm: Boolean){
  def this(data: ULong) = this(data == ULong(0), (data & ULong(1)) == ULong(1), (data & ULong(2)) == ULong(1))
}

trait GetConfigReply extends Ofp13Message{
  def configFlags: ConfigFlags
  def missSendLength: UInt
}

case class SetConfigInput(missSendLength: UInt, configFlags: ConfigFlags) extends Ofp13MessageInput

case class GetConfigRequestInput() extends Ofp13MessageInput

private[fixed] trait GetConfigReplyHandler{
  def onGetConfigReply(dpid: ULong, msg: GetConfigReply): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13ConfigDescription extends Ofp13MessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with GetConfigReplyHandler] with Ofp13HeaderDescription =>

  private class GetConfigRequestBuilder extends OfpMessageBuilder[GetConfigRequestInput]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): GetConfigRequestInput = GetConfigRequestInput()
  }

  private class SetConfigBuilder extends OfpMessageBuilder[SetConfigInput] {
    // Fills the underlying builder with the specified input.
    override protected def applyInput(input: SetConfigInput): Unit = {
      super.applyInput(input)
      setMember("flags", UShort((if(input.configFlags.drop) 1 else 0) | (if(input.configFlags.reasm) 2 else 0)))
      setMember("miss_send_len", input.missSendLength)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): SetConfigInput = SetConfigInput("miss_send_len", new ConfigFlags("flags"))
  }

  private class GetConfigReplyStructure(s: DynamicStructure) extends OfpMessage[GetConfigReply](s) with GetConfigReply {
    def configFlags: ConfigFlags = new ConfigFlags(primitiveField[ULong]("flags"))
    def missSendLength: UInt = primitiveField[UInt]("miss_send_len")
  }

  protected abstract override def builderClasses = classOf[SetConfigBuilder] :: classOf[GetConfigRequestBuilder] :: super.builderClasses
  protected abstract override def messageClasses = classOf[GetConfigReplyStructure] :: super.messageClasses
}