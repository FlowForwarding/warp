package org.flowforwarding.warp.controller.api.fixed.v13

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.Utils._

trait HelloElem { val mType: Short }

case class HelloElemVersionBitmap(bitmaps: Array[Int]) extends HelloElem { val mType: Short = 1 }

trait Hello extends Message{
  def elems: Array[HelloElem]
}

case class HelloInput(xid: Int, elems: Array[HelloElem]) extends MessageInput{
  def length = ???
}

trait Ofp13HelloDescription[BuilderType <: DynamicStructureBuilder[BuilderType, StructureType],
                            StructureType <: DynamicStructure[StructureType]] extends BasicStructuresDescription[BuilderType, StructureType]{
  apiProvider: DriverApiHelper[BuilderType, StructureType] =>

  class HelloBuilder extends OfpMessageBuilder[HelloInput]{
    // Fills the underlying builder with the specified input.
    protected override def applyInput(input: HelloInput): Unit = ???
  }

  case class HelloStructure(underlyingStructure: StructureType) extends Hello with OfpMessage{
    def elems: Array[HelloElem] = underlyingStructure.structuresSequence(getFieldName[Hello]("elements")) collect {
      case s if s.isTypeOf(namesConfig.getTypeName[HelloElemVersionBitmap]) =>
        HelloElemVersionBitmap(s.primitivesSequence(getFieldName[HelloElemVersionBitmap]("bitmaps")) map { _.toInt })
    }
  }

  abstract override def builderClasses = classOf[HelloBuilder] :: super.builderClasses
  abstract override def structureClasses = classOf[HelloStructure] :: super.structureClasses
}