package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.util._
import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.{Ofp13MessageInput, Ofp13Message, Ofp13MessageDescription}
import scala.reflect.ClassTag
import spire.math.{ULong, UShort, UInt}

trait Ofp13MultipartMultiValueMessage[T] extends Ofp13Message{
  def reqMore: Boolean // More replies to follow
  def data: Array[T]
  // def mpType Is it necessary?
}

trait Ofp13MultipartSingleValueMessage[T] extends Ofp13Message{
  def reqMore: Boolean // More replies to follow
  def data: T
  // def mpType Is it necessary?
}

trait MultipartMessageRequestInput extends Ofp13MessageInput{
  val reqMore: Boolean
  // def mpType Is it necessary?
}

trait MultipartMessageEmptyBodyRequestInput extends MultipartMessageRequestInput

trait MultipartMessageWithBodyRequestInput[Body <: BuilderInput] extends MultipartMessageRequestInput{
  val body: Body
}

private[fixed] trait Ofp13MultipartMessageDescription extends Ofp13MessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _]] with Ofp13HeaderDescription =>

  abstract class Ofp13MultipartMessageEmptyBodyRequestBuilder[Input <: MultipartMessageRequestInput: ClassTag] extends OfpMessageBuilder[Input] {
    override protected def applyInput(input: Input): Unit = {
      super.applyInput(input)
      setMember("flags", UShort(if(input.reqMore) 1 else 0))
    }
  }

  abstract class Ofp13MultipartMessageWithBodyRequestBuilder[Body <: BuilderInput: ToDynamic,
                                                             Input <: MultipartMessageWithBodyRequestInput[Body]: ClassTag] extends OfpMessageBuilder[Input] {
    override protected def applyInput(input: Input): Unit = {
      super.applyInput(input)
      setMember("flags", UShort(if(input.reqMore) 1 else 0))
      setMember("body", input.body)
    }
  }

  abstract class OfpMultipartMultiValueMessage[T: ClassTag, Data :FromDynamic :ClassTag](s: DynamicStructure) extends OfpMessage(s)(implicitly[ClassTag[T]]) {
    apiToImplement: T =>  // Forces to implement necessary methods. I'm not sure is it really good idea.
    def reqMore: Boolean = primitiveField[UShort]("flags") == UShort(1)
    def data: Array[Data] = structuresSequence[Data]("data")
  }

  abstract class OfpMultipartSingleValueMessage[T: ClassTag, Data :FromDynamic :ClassTag](s: DynamicStructure) extends OfpMessage(s)(implicitly[ClassTag[T]]) {
    apiToImplement: T =>  // Forces to implement necessary methods. I'm not sure is it really good idea.
    def reqMore: Boolean = primitiveField[UShort]("flags") == UShort(1)
    def data: Data = structureField[Data]("data")
  }
  //abstract override def builderClasses = classOf[TableStatisticsRequestBuilder] :: super.builderClasses
  //abstract override def messageClasses = classOf[TableStatisticsReplyStructure] :: super.messageClasses
}