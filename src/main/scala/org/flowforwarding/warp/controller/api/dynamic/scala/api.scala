package org.flowforwarding.warp.controller.api.dynamic.scala

import scala.language.dynamics

import org.flowforwarding.warp.controller.api.dynamic.{DynamicDriver, DynamicStructure, DynamicStructureBuilder}

trait DynamicStructureScalaAPI[SelfType <: DynamicStructureScalaAPI[SelfType]] extends DynamicStructure[SelfType] with Dynamic{
  def selectDynamic(name: String): SelfType = structureField(name)

  object primitives extends Dynamic{
    def selectDynamic(name: String): Long = primitiveField(name)
  }

  object sequencesOfPrimitives extends Dynamic{
    def selectDynamic(name: String): Seq[Long] = primitivesSequence(name)
  }

  object sequencesOfStructures extends Dynamic{
    def selectDynamic(name: String): Seq[SelfType] = structuresSequence(name)
  }

  object typeOf extends Dynamic{
    def selectDynamic(name: String): Boolean = isTypeOf(name)
  }
}

trait DynamicStructureBuilderScalaAPI[SelfType <: DynamicStructureBuilderScalaAPI[SelfType, StructureType],
                                      StructureType <: DynamicStructureScalaAPI[StructureType]] extends
  DynamicStructureBuilder[SelfType, StructureType] with Dynamic{

  def applyDynamic(name: String) = new MemberSetter(name)

  class MemberSetter(memberName: String){
    def apply(value: StructureType) = setMember(memberName, value)
    def apply(value: Long)          = setMember(memberName, value)
    def apply[T](values: Array[T])  = setMember(memberName, values)
  }
}

trait DynamicDriverScalaAPI[BuilderType <: DynamicStructureBuilderScalaAPI[BuilderType, StructureType],
                            StructureType <: DynamicStructureScalaAPI[StructureType]] extends
  DynamicDriver[BuilderType, StructureType] with Dynamic{

  def selectDynamic(msgType: String): BuilderType = getBuilder(msgType)
}
