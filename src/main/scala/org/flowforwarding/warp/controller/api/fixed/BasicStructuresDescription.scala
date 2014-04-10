package org.flowforwarding.warp.controller.api.fixed

import _root_.scala.reflect.ClassTag

import org.flowforwarding.warp.controller.api.dynamic._

trait Message{
  def header: Header
}

case class Header(length: Int, xid: Int) extends BuilderInput

trait MessageInput extends BuilderInput{
  def header: Header = new Header(length, xid)
  def length: Int
  def xid: Int
}

trait BasicStructuresDescription[BuilderType <: DynamicStructureBuilder[BuilderType, StructureType],
                                 StructureType <: DynamicStructure[StructureType]] {
   apiProvider: DriverApiHelper[BuilderType, StructureType] =>

  trait OfpStructure extends ConcreteStructure{
    val namesConfig = apiProvider.namesConfig
  }

  trait OfpMessage extends OfpStructure {
    def header: Header = {
      val h = underlyingStructure.structureField(getFieldName("header")(ClassTag(this.getClass)))
      val xid = h.primitiveField(getFieldName[Header]("xid")).toInt
      val length = h.primitiveField(getFieldName[Header]("length")).toInt
      Header(length, xid)
    }
  }

  trait OfpStructureBuilder[Input <: BuilderInput] extends ConcreteStructureBuilder[Input, BuilderType, StructureType] {
    protected val dynamicBuilder: BuilderType = apiProvider.getDynamicBuilder(this.getClass).get
    protected val namesConfig = apiProvider.namesConfig
  }

  trait OfpMessageBuilder[Input <: MessageInput] extends OfpStructureBuilder[Input]{
    // Fills the underlying builder with the specified input.
    protected def applyInput(input: Input): Unit = {
      // builder already must contain message type and version !!!
      dynamicBuilder.setMember(getFieldName("header")(ClassTag(this.getClass)), new HeaderBuilder().build(input.header))
    }
  }

  class HeaderBuilder extends OfpStructureBuilder[Header]{
    protected def applyInput(input: Header): Unit = {
      dynamicBuilder.setMember(getFieldName[Header]("xid"), input.xid)
      dynamicBuilder.setMember(getFieldName[Header]("length"), input.length)
    }
  }

  case class HeaderStructure(underlyingStructure: StructureType) extends OfpStructure

  // These lists are provided using stackable trait pattern
  override def builderClasses: List[Class[_ <: OfpStructureBuilder[_]]] = List()
  override def structureClasses: List[Class[_ <: OfpStructure]] = List()
}






