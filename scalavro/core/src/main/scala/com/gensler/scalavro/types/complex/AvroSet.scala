package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{ AvroType, AvroComplexType, AvroNamedType }
import com.gensler.scalavro.JsonSchemaProtocol._

import spray.json._

import scala.reflect.runtime.universe._
import scala.collection.immutable.ListMap
import scala.collection.mutable

/**
  * Represents a mapping from a Scala type (a subclass of Set[_]) to a
  * corresponding Avro type.
  */
class AvroSet[T, S <: Set[T]](
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

object AvroSet {

  import com.gensler.scalavro.util.ReflectionHelpers

  private[types] def fromType[T <: Set[_]: TypeTag](processedTypes: Set[Type]): AvroType[T] = {

    val tt = typeTag[T]

    // Traverse up to the Seq supertype and get the type of items
    val setSuperSymbol = tt.tpe.baseClasses.map(_.asType).find { bcSymbol =>
      bcSymbol == typeOf[Set[_]].typeSymbol
    }

    val itemType = setSuperSymbol.map(_.typeParams(0).asType.toTypeIn(tt.tpe)).get

    if (processedTypes.exists { _ =:= itemType }) AvroType.cyclicTypeDependencyException[T]

    def makeSet[I](itemTag: TypeTag[I]) = new AvroSet()(itemTag, tt.asInstanceOf[TypeTag[Set[I]]])

    ReflectionHelpers.varargsFactory[T].get // throws an exception if one can't be derived

    makeSet(ReflectionHelpers tagForType itemType).asInstanceOf[AvroType[T]]
  }

}