package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{ AvroType, AvroNamedType }
import com.gensler.scalavro.JsonSchemaProtocol._
import com.gensler.scalavro.util.ReflectionHelpers

import spray.json._

import scala.reflect.runtime.universe._
import scala.collection.immutable.ListMap
import scala.collection.mutable

/**
  * Represents a mapping from a Java enum type to a corresponding Avro type.
  */
class AvroJEnum[E: TypeTag](
    val name: String,
    val symbols: Seq[String],
    val namespace: Option[String] = None) extends AvroNamedType[E] {

  val typeName = "enum"

  val enumClass = ReflectionHelpers.classLoaderMirror.runtimeClass(tag.tpe.typeSymbol.asClass)

  val symbolMap: Map[String, E] = enumClass.getEnumConstants.toSeq.map {
    symbol => symbol.toString -> symbol
  }.toMap.asInstanceOf[Map[String, E]]

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

object AvroJEnum {

  private[types] def fromType[T: TypeTag](processedTypes: Set[Type]): AvroType[T] = {
    val tt = typeTag[T]
    val enumClass = ReflectionHelpers.classLoaderMirror.runtimeClass(tt.tpe.typeSymbol.asClass)
    new AvroJEnum[T](
      name = enumClass.getSimpleName,
      symbols = enumClass.getEnumConstants.map(_.toString),
      namespace = Some(enumClass.getPackage.getName)
    )
  }

}