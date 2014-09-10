package com.gensler.scalavro.types

import com.gensler.scalavro.types.complex.AvroRecord
import com.gensler.scalavro.JsonSchemaProtocol._

import scala.reflect.runtime.universe._

import spray.json._

import scala.collection.immutable.ListMap
import scala.collection.mutable

/**
  * Parent class of all composite and parameterized Avro types.
  */
abstract class AvroComplexType[T: TypeTag]
    extends AvroType[T]
    with SelfDescribingSchemaHelpers {

  final val isPrimitive = false

  override def toString(): String = {
    "%s[%s]".format(getClass.getSimpleName, typeOf[T])
  }

  def schema(): JsValue = selfContainedSchema()

  protected def withoutDocOrAliases(json: JsValue): JsValue = json match {
    case JsObject(fields)      => new JsObject(fields -- Seq("Doc", "aliases"))
    case otherJsValue: JsValue => otherJsValue
  }

  /**
    * Returns true if this type depends upon the supplied type.
    */
  final def dependsOn(thatType: AvroType[_]): Boolean = {
    dependentNamedTypes contains thatType
  }

  final override def parsingCanonicalForm(): JsValue =
    schemaToParsingCanonicalForm(this.schema)

}

trait SelfDescribingSchemaHelpers {

  protected[scalavro] def selfContainedSchemaOrFullyQualifiedName(
    avroType: AvroType[_],
    resolvedSymbols: mutable.Set[String] = mutable.Set[String]()): JsValue = {

    avroType match {

      case recordType: AvroRecord[_] => recordType.referencedSchema(resolvedSymbols)

      case namedType: AvroNamedType[_] => {
        if (resolvedSymbols contains namedType.fullyQualifiedName)
          namedType.fullyQualifiedName.toJson
        else {
          val itemSchema = namedType selfContainedSchema resolvedSymbols
          resolvedSymbols += namedType.fullyQualifiedName
          itemSchema
        }
      }

      case _ => avroType selfContainedSchema resolvedSymbols
    }
  }

  protected[scalavro] def schemaToParsingCanonicalForm(schema: JsValue): JsValue = schema match {
    case JsArray(elements) => JsArray(elements map schemaToParsingCanonicalForm)

    // replace short names with full names, using applicable namespaces
    // retain only: type, name, fields, symbols, items, values, size (in that order)
    case JsObject(fields) => {
      val keysToRetain = List("type", "name", "fields", "symbols", "items", "values", "size")
      val canonicalFields: List[(String, JsValue)] = keysToRetain.flatMap { key =>
        fields.get(key).map { value =>
          val canonicalValue = (key, value) match {
            case ("name", JsString(name)) => fields.get("namespace") match {
              case Some(JsString(ns)) => "%s.%s".format(ns, name).toJson
              case _                  => name.toJson
            }
            case ("type", _)   => schemaToParsingCanonicalForm(value)
            case ("fields", _) => schemaToParsingCanonicalForm(value)
            case _             => value
          }
          key -> canonicalValue
        }
      }

      ListMap(canonicalFields: _*).toJson
    }

    case otherValue: JsValue => otherValue
  }

}

object SelfDescribingSchemaHelpers extends SelfDescribingSchemaHelpers