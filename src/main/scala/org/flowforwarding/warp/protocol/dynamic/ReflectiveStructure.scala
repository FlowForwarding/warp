package org.flowforwarding.warp.protocol.dynamic

import org.flowforwarding.warp.controller.api.dynamic.DynamicStructure


class ReflectiveStructure(val underlying: Any) extends DynamicStructure{

  override def toString = "ReflectiveStructure(" + underlying.toString + ")"

  private def valueByName(name: String): Any = {
    val accessor = underlying.getClass.getDeclaredMethod(name)
    accessor.invoke(underlying)
  }

  def primitiveField(name: String): Long = {
    val value = valueByName(name)
    if(toLong.isDefinedAt(value))
      toLong(value)
    else if (value.isInstanceOf[Seq[_]])
      throw new IllegalArgumentException(s"Value of field $name is not a primitive, but sequence of values. Use method 'primitivesSequence' or 'structuresSequence' instead.")
    else
      throw new IllegalArgumentException(s"Value of field $name is not a primitive, but structure. Use method 'structureField' instead.")
  }

  def structureField(name: String): ReflectiveStructure = {
    val value = valueByName(name)
    if(toLong.isDefinedAt(value))
      throw new IllegalArgumentException(s"Value of field $name is not a structure, but attribute. Use method 'primitiveField' instead.")
    else if (value.isInstanceOf[Seq[_]])
      throw new IllegalArgumentException(s"Value of field $name is not a structure, but sequence of values. Use method 'primitivesSequence' or 'structuresSequence' instead.")
    else
      new ReflectiveStructure(value)
  }

  def primitivesSequence(name: String): Array[Long] = valueByName(name) match {
    case seq: Seq[_] if seq.isEmpty || toLong.isDefinedAt(seq.head) =>
      (seq map toLong).toArray
    case seq: Seq[_] =>
      throw new IllegalArgumentException(s"Value of field $name is a sequence of structures, not primitives. Use method 'structuresSequence' instead.")
    case v if toLong.isDefinedAt(v) =>
      throw new IllegalArgumentException(s"Value of field $name is not a sequence, but attribute. Use method 'primitiveField' instead.")
    case _ =>
      throw new IllegalArgumentException(s"Value of field $name is not a sequence, but structure. Use method 'structureField' instead.")
  }

  def structuresSequence(name: String): Array[DynamicStructure] = valueByName(name) match {
    case seq: Seq[_] if seq.isEmpty || !toLong.isDefinedAt(seq.head) =>
      seq map { new ReflectiveStructure(_) } toArray
    case seq: Seq[_] =>
      throw new IllegalArgumentException(s"Value of field $name is a sequence of primitives, not structures. Use method 'primitivesSequence' instead.")
    case v if toLong.isDefinedAt(v) =>
      throw new IllegalArgumentException(s"Value of field $name is not a sequence, but attribute. Use method 'primitiveField' instead.")
    case _ =>
      throw new IllegalArgumentException(s"Value of field $name is not a sequence, but structure. Use method 'structureField' instead.")
  }

  def isTypeOf(typeName: String) = underlying.getClass.getSimpleName == typeName
}