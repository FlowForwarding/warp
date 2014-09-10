/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed

import scala.reflect.ClassTag

import com.typesafe.config.Config

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed.util._
import org.flowforwarding.warp.controller.api.dynamic.DynamicStructure
import spire.math.{UByte, UShort, UInt, ULong}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

private[fixed] trait ConfigurableStructure{
  protected val namesConfig: Config

  protected val correspondingClass: Class[_]

  def mapFieldName(fieldKey: String) = {
    val StructureName(configSectionName) = correspondingClass
    namesConfig.getConfig(configSectionName).getString(fieldKey)
  }
}

private[fixed] trait FromDynamic[T]{
  def fromDynamic: PartialFunction[DynamicStructure, T]
}

private[fixed] trait ToDynamic[-Input <: BuilderInput]{
  def toDynamic: PartialFunction[Input, DynamicBuilderInput]
}

private[fixed] trait ConcreteStructure extends ConfigurableStructure{
  outer =>
  protected val underlyingStructure: DynamicStructure

  def enumField[E <: Enumeration](enum: E, name: String): E#Value = enum.values.find(v => ULong(v.id) == primitiveField[ULong](name)).get
  def bitmapField[T <: Bitmap: ClassTag](name: String): T = primitiveField[ULong](name).toBitmap.get
  def bitmapField[T <: Bitmap: ClassTag](name: String, bits: IndexedSeq[UInt]): T = primitiveField[ULong](name).toBitmap(bits).get
  def stringField(name: String) = new String(primitivesSequence[Char](name) takeWhile { _ != 0 }) // extract zero-terminated string

  def primitiveField[T: PrimitiveFromLong](name: String): T = {
    val data = underlyingStructure.primitiveField(mapFieldName(name))
    implicitly[PrimitiveFromLong[T]].fromLong(data)
  }

  def primitivesSequence[T: PrimitiveFromLong: ClassTag](name: String): Array[T] = {
    val data = underlyingStructure.primitivesSequence(mapFieldName(name))
    (data map implicitly[PrimitiveFromLong[T]].fromLong).toArray
  }

  def bytes(name: String): Array[Byte] = underlyingStructure.primitivesSequence(mapFieldName(name)) map { _.toByte }

  def structureField[T : FromDynamic : ClassTag](name: String): T = {
    val structure = underlyingStructure.structureField(mapFieldName(name))
    implicitly[FromDynamic[T]].fromDynamic(structure)
  }

  def structuresSequence[T : FromDynamic : ClassTag](name: String): Array[T] =
    underlyingStructure.structuresSequence(mapFieldName(name)) map { implicitly[FromDynamic[T]].fromDynamic }
}

private[fixed] abstract class ConcreteStructureBuilder[Input <: BuilderInput: ClassTag] extends ConfigurableStructure{

  protected val api: StructuresDescriptionHelper with DynamicStructureBuilder[_]

  protected val correspondingClass = implicitly[ClassTag[Input]].runtimeClass

  protected lazy val namesConfig = api.namesConfig
  protected lazy val structureName = firstGenericParameter(this.getClass) map { c => namesConfig.getTypeName(c)}
  protected lazy val dynamicBuilderInput: DynamicBuilderInput = api.newBuilderInput(structureName.get)

  protected def setMember(name: String, value: ULong)             = dynamicBuilderInput.setMember(mapFieldName(name), DynamicPrimitive(value.signed))
  protected def setMember(name: String, value: UInt)              = dynamicBuilderInput.setMember(mapFieldName(name), DynamicPrimitive(value.signed))
  protected def setMember(name: String, value: UShort)            = dynamicBuilderInput.setMember(mapFieldName(name), DynamicPrimitive(value.signed))
  protected def setMember(name: String, value: UByte)             = dynamicBuilderInput.setMember(mapFieldName(name), DynamicPrimitive(value.signed))
  protected def setMember(name: String, value: Enumeration#Value) = dynamicBuilderInput.setMember(mapFieldName(name), DynamicPrimitive(value.id))

  protected def setMember[I <: BuilderInput: ToDynamic](name: String, input: I) =
    dynamicBuilderInput.setMember(mapFieldName(name), DynamicStructureInput(implicitly[ToDynamic[I]].toDynamic(input)))

  protected def setMember[I <: BuilderInput: ToDynamic](name: String, input: Array[I]) =
    dynamicBuilderInput.setMember(mapFieldName(name), DynamicStructureInputs(input map { implicitly[ToDynamic[I]].toDynamic }))

  protected def setMember(name: String, value: Array[ULong])  = dynamicBuilderInput.setMember(mapFieldName(name), DynamicPrimitives(value map { _.toLong }))
  protected def setMember(name: String, value: Array[UInt])   = dynamicBuilderInput.setMember(mapFieldName(name), DynamicPrimitives(value map { _.toLong }))
  protected def setMember(name: String, value: Array[UShort]) = dynamicBuilderInput.setMember(mapFieldName(name), DynamicPrimitives(value map { _.toLong }))
  protected def setMember(name: String, value: Array[Byte])   = dynamicBuilderInput.setMember(mapFieldName(name), DynamicPrimitives(value map { _.toLong }))

  // Fills the underlying builder (of type MapBasedDynamicBuilderInput) with the specified input.
  protected def applyInput(input: Input): Unit

  private[fixed] def inputFromTextView(implicit input: BITextView): Input

  def toDynamicInput(input: Input): DynamicBuilderInput = {
    applyInput(input)
    dynamicBuilderInput
  }

  def toDynamicInput(input: BITextView): DynamicBuilderInput = {
    applyInput(inputFromTextView(input))
    dynamicBuilderInput
  }
}

trait BuilderInput


