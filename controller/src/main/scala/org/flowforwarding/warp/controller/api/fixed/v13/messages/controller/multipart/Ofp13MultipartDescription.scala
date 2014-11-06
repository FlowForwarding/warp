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

import scala.reflect.ClassTag

trait MultipartRequestBodyInput extends BuilderInput

case class MultipartRequestInput(reqMore: Boolean, body: MultipartRequestBodyInput) extends Ofp13MessageInput

trait MultipartReplyBody[T]{ def value: T }

trait MultipartReply extends Ofp13Message{
  def reqMore: Boolean
  def body: MultipartReplyBody[_]
}

trait MultipartReplyHandlers {
  handlers: SwitchDescriptionReplyHandler with PortDescriptionReplyHandler =>

  def onMultipartReply(dpid: ULong, msg: MultipartReply): Array[BuilderInput] =
    msg.body match {
      case pd: PortDescriptionReplyBody => handlers.onPortDescriptionReply(dpid, pd.value)
      case sd: SwitchDescriptionReplyBody => handlers.onSwitchDescriptionReply(dpid, sd.value)
    }
}

private[fixed] trait Ofp13MultipartDescription extends Ofp13MessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with MultipartReplyHandlers]
               with Ofp13HeaderDescription
               with Ofp13SwitchDescriptionDescription
               with Ofp13PortDescriptionDescription
               with Ofp13PortDescription =>

  protected[fixed] implicit object MultipartRequestBodyInput extends ToDynamic[MultipartRequestBodyInput] {
    val toDynamic: PartialFunction[MultipartRequestBodyInput, DynamicBuilderInput] = {
      case sb: SwitchDescriptionRequestBodyInput => new SwitchDescriptionRequestBodyInputBuilder toDynamicInput sb
      case nb: PortDescriptionRequestBodyInput => new PortDescriptionRequestBodyInputBuilder toDynamicInput nb
    }
  }

  protected[fixed] implicit object MultipartReplyBody extends FromDynamic[MultipartReplyBody[_]] {
    val fromDynamic: PartialFunction[DynamicStructure, MultipartReplyBody[_]] = {
      case s if s.ofType[SwitchDescriptionReplyBody] =>
        new OfpMessage[SwitchDescriptionReplyBody](s) with SwitchDescriptionReplyBody {
          val value = structureField[SwitchDescription]("value")
        }
      case s if s.ofType[PortDescriptionReplyBody] =>
        new OfpMessage[PortDescriptionReplyBody](s) with PortDescriptionReplyBody {
          val value = structuresSequence[Port]("value")
        }
    }
  }

  class MultipartRequestBuilder extends OfpMessageBuilder[MultipartRequestInput] {
    override protected def applyInput(input: MultipartRequestInput): Unit = {
      super.applyInput(input)
      setMember("flags", if (input.reqMore) ULong(1) else ULong(0))
      setMember("body", input.body)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): MultipartRequestInput =
      MultipartRequestInput("flags", structure[MultipartRequestBodyInput]("body"))
  }

  class Ofp13MultipartReplyStructure(s: DynamicStructure) extends OfpMessage[MultipartReply](s) with MultipartReply {
    val reqMore: Boolean = primitiveField[ULong]("flags") == ULong(1)
    val body: MultipartReplyBody[_] = structureField[MultipartReplyBody[_]]("body")
  }

  protected abstract override def builderClasses = classOf[MultipartRequestBuilder] :: super.builderClasses
  protected abstract override def messageClasses = classOf[Ofp13MultipartReplyStructure] :: super.messageClasses
}