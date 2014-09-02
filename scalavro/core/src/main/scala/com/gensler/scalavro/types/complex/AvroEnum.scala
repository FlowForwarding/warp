package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{ AvroType, AvroNamedType }
import com.gensler.scalavro.JsonSchemaProtocol._

import spray.json._

import scala.reflect.runtime.universe._
import scala.collection.immutable.ListMap
import scala.collection.mutable

/**
  * Represents a mapping from a Scala type (a subclass of scala.Enumeration)
  * to a corresponding Avro type.
  */
class AvroEnum[E <: Enumeration: TypeTag](
    val name: String,
    val symbols: Seq[String],
    val namespace: Option[String] = None) extends AvroNamedType[E#Value] {

  val enumTag = typeTag[E]

  val typeName = "enum"

  def selfContainedSchema(
    resolvedSymbols: mutable.Set[String] = mutable.Set[String]()) = {

    val requiredParams = ListMap(
      "name" -> name.toJson,
      "type" -> typeName.toJson,
      "symbols" -> symbols.toJson
    )

    val optionalParams = ListMap(
      "namespace" -> namespace
    ).collect { case (k, Some(v)) => (k, v.toJson) }

    new JsObject(requiredParams ++ optionalParams)
  }

}

object AvroEnum {

  import com.gensler.scalavro.util.ReflectionHelpers

  private[types] def fromType[T <: Enumeration: TypeTag](processedTypes: Set[Type]): AvroType[T] = {
    val tt = typeTag[T]
    tt.tpe match {
      case TypeRef(prefix, symbol, _) =>

        val enumTypeTag = ReflectionHelpers.enumForValue(tt.asInstanceOf[TypeTag[_ <: Enumeration#Value]])

        new AvroEnum(
          name = symbol.name.toString,
          symbols = ReflectionHelpers.symbolsOf(enumTypeTag),
          namespace = Some(prefix.toString stripSuffix ".type")
        )(enumTypeTag).asInstanceOf[AvroType[T]]
    }
  }

}