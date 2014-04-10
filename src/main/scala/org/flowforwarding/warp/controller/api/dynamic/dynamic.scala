package org.flowforwarding.warp.controller.api.dynamic

import org.flowforwarding.warp.controller.session.OFMessage

trait DynamicStructure[SelfType <: DynamicStructure[SelfType]] extends OFMessage{
  def primitiveField(name: String): Long
  def structureField(name: String): SelfType
  def primitivesSequence(name: String): Array[Long]
  def structuresSequence(name: String): Array[SelfType]
  def isTypeOf(typeName: String): Boolean
}

trait DynamicStructureBuilder[SelfType <: DynamicStructureBuilder[SelfType, StructureType],
                              StructureType <: DynamicStructure[StructureType]]{
  def setMember(memberName: String, value: Long): SelfType
  def setMember[T](memberName: String, values: Array[T]): SelfType
  def setMember(memberName: String, value: StructureType): SelfType
  def build: StructureType
}





