package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{ AvroType, AvroComplexType }
import com.gensler.scalavro.JsonSchemaProtocol._

import spray.json._

import scala.reflect.runtime.universe._
import scala.collection.immutable.ListMap
import scala.collection.mutable
import com.gensler.scalavro.types.supply.{RawData, RawSeq}

/**
  * Represents a mapping from a Scala type (a subclass of Seq[_]) to a
  * corresponding Avro type.
  */
class AvroArray[T, S <: Seq[T]](
  implicit val itemTypeTag: TypeTag[T],
  implicit val originalTypeTag: TypeTag[S])
    extends AvroComplexType[S] {

  val itemType = AvroType[T]

  val typeName = "array"

  def selfContainedSchema(
    resolvedSymbols: mutable.Set[String] = mutable.Set[String]()) = new JsObject(ListMap(
    "type" -> typeName.toJson,
    "items" -> selfContainedSchemaOrFullyQualifiedName(itemType, resolvedSymbols)
  ))

}

/**
 * Represents a sequence of items, which are serialized without any metadata.
 * When deserialized, it is considered that the whole stream contains items of type T.
 */
class AvroRawArray[T, S <: RawSeq[T]](implicit itemTypeTag: TypeTag[T], originalTypeTag: TypeTag[S]) extends AvroArray[T, S] with RawData

object AvroArray {

  import com.gensler.scalavro.util.ReflectionHelpers

  private[types] def fromType[T: TypeTag](processedTypes: Set[Type]): AvroType[T] = {
    val tt = typeTag[T]
    if (tt.tpe <:< typeOf[Seq[_]])
      fromSeqType[T](processedTypes, false)
    else if (tt.tpe <:< typeOf[Array[_]])
      fromArrayType[T](processedTypes)
    else throw new IllegalArgumentException(
      "The supplied type must be a subtype of either Seq[_] or Array[_]"
    )
  }

  private[types] def raw[T: TypeTag](processedTypes: Set[Type]): AvroType[T] = {
    val tt = typeTag[T]
    if (tt.tpe <:< typeOf[RawSeq[_]])
      fromSeqType[T](processedTypes, true)
    else throw new IllegalArgumentException(
      "The supplied type must be a subtype of RawSeq[_]"
    )
  }

  private[types] def fromSeqType[T: TypeTag](processedTypes: Set[Type], raw: Boolean): AvroType[T] = {
    val tt = typeTag[T]

    // Traverse up to the Seq supertype and get the type of items
    val seqSuperSymbol = tt.tpe.baseClasses.map(_.asType).find { bcSymbol =>
      bcSymbol == typeOf[Seq[_]].typeSymbol
    }

    val itemType = seqSuperSymbol.map(_.typeParams(0).asType.toTypeIn(tt.tpe)).get

    if (processedTypes.exists { _ =:= itemType }) AvroType.cyclicTypeDependencyException[T]

    def makeArray[I](itemTag: TypeTag[I], raw: Boolean) =
      if (raw) new AvroRawArray()(itemTag, tt.asInstanceOf[TypeTag[RawSeq[I]]])
      else new AvroArray()(itemTag, tt.asInstanceOf[TypeTag[Seq[I]]])

    ReflectionHelpers.varargsFactory[T].get // throws an exception if one can't be derived

    makeArray(ReflectionHelpers tagForType itemType, raw).asInstanceOf[AvroType[T]]
  }

  private[types] def fromArrayType[T: TypeTag](processedTypes: Set[Type]): AvroType[T] = {

    val tt = typeTag[T]

    val itemType = tt.tpe.typeSymbol.asType.typeParams(0).asType.toTypeIn(tt.tpe)

    if (processedTypes.exists { _ =:= itemType }) AvroType.cyclicTypeDependencyException[T]

    def makeJArray[I](itemTag: TypeTag[I]) = new AvroJArray()(itemTag, tt.asInstanceOf[TypeTag[Array[I]]])

    makeJArray(ReflectionHelpers tagForType itemType).asInstanceOf[AvroType[T]]
  }

}