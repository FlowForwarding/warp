/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView
import org.flowforwarding.warp.controller.api.fixed.util._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.data._
import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.{Ofp13MessageInput, Ofp13Message, Ofp13MessageDescription}
import spire.math.{ULong, UShort}

trait MultipartRequestDataInput extends BuilderInput{
  def reqMore: Boolean // More replies to follow
}

case class MultipartRequestInput(data: MultipartRequestDataInput) extends Ofp13MessageInput

trait MultipartReplyData{
  def reqMore: Boolean // More replies to follow
}

trait MultipartReply extends Ofp13Message{
  def data: MultipartReplyData
}

trait MultipartReplyHandlers {
  handlers: SwitchDescriptionReplyHandler with PortDescriptionReplyHandler =>

  def onMultipartReply(dpid: ULong, msg: MultipartReply): Array[BuilderInput] = {
    msg.data match {
      case pd: PortDescriptionReplyData => handlers.onPortDescriptionReply(dpid, pd)
      case sd: SwitchDescriptionReplyData => handlers.onSwitchDescriptionReply(dpid, sd)
    }
  }
}

private[fixed] trait Ofp13MultipartDescription extends Ofp13MessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with MultipartReplyHandlers]
               with Ofp13HeaderDescription
               with Ofp13SwitchDescriptionDescription
               with Ofp13PortDescriptionDescription
               with Ofp13PortDescription =>

  protected[fixed] implicit object MultipartRequestDataInput extends ToDynamic[MultipartRequestDataInput] {
    val toDynamic: PartialFunction[MultipartRequestDataInput, DynamicBuilderInput] = {
      case sb: SwitchDescriptionRequestDataInput => new SwitchDescriptionRequestDataInputBuilder toDynamicInput sb
      case nb: PortDescriptionRequestDataInput => new PortDescriptionRequestDataInputBuilder toDynamicInput nb
    }
  }

  protected[fixed] implicit object MultipartResponseData extends FromDynamic[MultipartReplyData] {
    val fromDynamic: PartialFunction[DynamicStructure, MultipartReplyData] = {
      case s if s.ofType[SwitchDescriptionReplyData] => new OfpStructure[SwitchDescriptionReplyData](s) with SwitchDescriptionReplyData {
        val reqMore = primitiveField[UShort]("flags") == UShort(1)
        val body = structureField[SwitchDescription]("body")
      }
      case s if s.ofType[PortDescriptionReplyData] => new OfpStructure[PortDescriptionReplyData](s) with PortDescriptionReplyData {
        val reqMore = primitiveField[UShort]("flags") == UShort(1)
        val body = structuresSequence[Port]("body")
      }
    }
  }

  class MultipartRequestBuilder extends OfpMessageBuilder[MultipartRequestInput] {
    override protected def applyInput(input: MultipartRequestInput): Unit = {
      super.applyInput(input)
      setMember("data", input.data) // NOTE: broken protocol, undefined nested structure data
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): MultipartRequestInput = ???
  }

  class Ofp13MultipartReplyStructure(s: DynamicStructure) extends OfpMessage[MultipartReply](s) with MultipartReply {
    def data: MultipartReplyData = structureField[MultipartReplyData]("data") // NOTE: broken protocol, undefined nested structure data
  }

  protected abstract override def builderClasses = classOf[MultipartRequestBuilder] :: super.builderClasses
  protected abstract override def messageClasses = classOf[Ofp13MultipartReplyStructure] :: super.messageClasses
}