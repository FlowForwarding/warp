package org.flowforwarding.warp.controller.api.fixed.v13

import org.flowforwarding.warp.controller.api.dynamic.{DynamicStructure, DynamicStructureBuilder}
import org.flowforwarding.warp.controller.api.fixed._

trait EchoRequest extends Message{
  def elements: Array[Byte]
}

case class EchoRequestInput(xid: Int, elements: Array[Byte]) extends MessageInput{
  def length: Int = 8 + elements.length
}

trait Ofp13EchoRequestDescription[BuilderType <: DynamicStructureBuilder[BuilderType, StructureType],
                                  StructureType <: DynamicStructure[StructureType]] extends BasicStructuresDescription[BuilderType, StructureType] {
  apiProvider: DriverApiHelper[BuilderType, StructureType] =>

  class EchoRequestBuilder extends OfpMessageBuilder[EchoRequestInput]{
    // Fills the underlying builder with the specified input.
    override protected def applyInput(input: EchoRequestInput): Unit = {
      super.applyInput(input)
      dynamicBuilder.setMember(getFieldName[EchoRequestBuilder]("elements"), input.elements)
    }
  }

  case class EchoRequestStructure(underlyingStructure: StructureType) extends OfpMessage with EchoRequest{
    def elements: Array[Byte] = underlyingStructure.primitivesSequence(getFieldName[EchoRequest]("elements")) map { _.toByte }
  }

  abstract override val builderClasses = classOf[EchoRequestBuilder] :: super.builderClasses
  abstract override val structureClasses = classOf[EchoRequestStructure] :: super.structureClasses
}

