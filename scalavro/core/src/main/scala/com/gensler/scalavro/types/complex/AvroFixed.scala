package com.gensler.scalavro.types.complex

import com.gensler.scalavro.types.{ AvroType, AvroNamedType }
import com.gensler.scalavro.JsonSchemaProtocol._

import com.gensler.scalavro.util.FixedData

import spray.json._

import scala.reflect.runtime.universe._
import scala.collection.immutable.ListMap
import scala.collection.mutable

/**
  * Represents a mapping from a Scala type (a subclass of
  * com.gensler.scalavro.util.FixedData) to a corresponding Avro type.
  */
class AvroFixed[T <: FixedData: TypeTag](
    val name: String,
    val size: Int,
    val namespace: Option[String] = None,
    val aliases: Seq[String] = Seq()) extends AvroNamedType[T] {

  val typeName = "fixed"

  def selfContainedSchema(
    resolvedSymbols: mutable.Set[String] = mutable.Set[String]()) = {

    val requiredParams = ListMap(
      "name" -> name.toJson,
      "type" -> typeName.toJson,
      "size" -> size.toJson
    )

    val namespaceParam = ListMap(
      "namespace" -> namespace
    ).collect { case (k, Some(v)) => (k, v.toJson) }

    val aliasesParam = ListMap(
      "aliases" -> aliases
    ).collect { case (k, v) => (k, v.toJson) }

    new JsObject(requiredParams ++ namespaceParam ++ aliasesParam)
  }

}

object AvroFixed {

  import com.gensler.scalavro.util.ReflectionHelpers
  import scala.collection.immutable

  private[types] def fromType[T <: FixedData: TypeTag](processedTypes: Set[Type] = Set[Type]()): AvroType[T] = {
    val tt = typeTag[T]
    FixedData.lengthAnnotationInstance(tt.tpe.typeSymbol.asClass) match {
      case Some(FixedData.Length(dataLength)) => {
        val TypeRef(prefix, symbol, _) = tt.tpe

        if (tt.tpe.typeSymbol.asClass.typeParams.nonEmpty)
          throw new IllegalArgumentException(
            "FixedData classes with type parameters are not supported"
          )

        if (!ReflectionHelpers.singleArgumentConstructor[T, immutable.Seq[Byte]].isDefined)
          throw new IllegalArgumentException(
            "FixedData classes must define a public single-argument constructor taking a Seq[Byte]"
          )

        new AvroFixed(
          name = symbol.name.toString,
          size = dataLength,
          namespace = Some(prefix.toString stripSuffix ".type")
        )
      }
      case None => throw new IllegalArgumentException(
        "FixedData classes must be decorated with a FixedData.Length annotation"
      )
    }
  }

}