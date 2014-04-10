package org.flowforwarding.warp.controller.api.fixed.v13

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._

trait EchoReply extends Message{
  def elements: Array[Byte]
}

case class EchoReplyInput(xid: Int, elements: Array[Byte]) extends MessageInput{
  def length: Int = 8 + elements.length
}

trait Ofp13EchoReplyDescription[BuilderType <: DynamicStructureBuilder[BuilderType, StructureType],
                                StructureType <: DynamicStructure[StructureType]] extends BasicStructuresDescription[BuilderType, StructureType] {
  apiProvider: DriverApiHelper[BuilderType, StructureType] =>

  class EchoReplyBuilder extends OfpMessageBuilder[EchoReplyInput]{
    // Fills the underlying builder with the specified input.
    override protected def applyInput(input: EchoReplyInput): Unit = {
      super.applyInput(input)
      dynamicBuilder.setMember(getFieldName[EchoReplyBuilder]("elements"), input.elements)
    }
  }

  case class EchoReplyStructure(underlyingStructure: StructureType) extends OfpMessage with EchoReply{
    def elements: Array[Byte] = underlyingStructure.primitivesSequence(getFieldName[EchoReply]("elements")) map { _.toByte }
  }

  abstract override def builderClasses = classOf[EchoReplyBuilder] :: super.builderClasses
  abstract override def structureClasses = classOf[EchoReplyStructure] :: super.structureClasses
}

