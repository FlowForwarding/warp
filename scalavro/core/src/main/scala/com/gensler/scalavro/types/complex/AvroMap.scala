package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{ AvroType, AvroComplexType }
import com.gensler.scalavro.JsonSchemaProtocol._

import spray.json._

import scala.reflect.runtime.universe._
import scala.collection.immutable.ListMap
import scala.collection.mutable

/**
  * Represents a mapping from a Scala type (a subclass of Map[String, _]) to a
  * corresponding Avro type.
  */
class AvroMap[T, M <: Map[String, T]](
  implicit val itemTypeTag: TypeTag[T],
  implicit val originalTypeTag: TypeTag[M])
    extends AvroComplexType[M] {

  val itemType = AvroType[T]

  val typeName = "map"

  def selfContainedSchema(
    resolvedSymbols: mutable.Set[String] = mutable.Set[String]()) = new JsObject(ListMap(
    "type" -> typeName.toJson,
    "values" -> selfContainedSchemaOrFullyQualifiedName(itemType, resolvedSymbols)
  ))

}

object AvroMap {

  import com.gensler.scalavro.util.ReflectionHelpers

  private[types] def fromType[T <: Map[String, _]: TypeTag](processedTypes: Set[Type]): AvroType[T] = {

    val tt = typeTag[T]

    // Traverse up to the Map supertype and get the type of items
    val mapSuperSymbol = tt.tpe.baseClasses.map(_.asType).find { bcSymbol =>
      bcSymbol == typeOf[Map[_, _]].typeSymbol
    }

    val itemType = mapSuperSymbol.map(_.typeParams(1).asType.toTypeIn(tt.tpe)).get

    if (processedTypes.exists { _ =:= itemType }) AvroType.cyclicTypeDependencyException[T]

    def makeMap[I](itemTag: TypeTag[I]) = new AvroMap()(itemTag, tt.asInstanceOf[TypeTag[Map[String, I]]])

    ReflectionHelpers.varargsFactory[T].get // throws an exception if one can't be derived

    makeMap(ReflectionHelpers tagForType itemType).asInstanceOf[AvroType[T]]
  }

}