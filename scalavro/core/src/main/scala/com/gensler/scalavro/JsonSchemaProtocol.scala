package com.gensler.scalavro

import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.complex.AvroRecord

import spray.json._

import scala.collection.immutable.ListMap
import scala.language.existentials

object JsonSchemaProtocol extends DefaultJsonProtocol {

  implicit object JsonSchemifiableWriter extends RootJsonFormat[JsonSchemifiable] {
    def write(objectWithSchema: JsonSchemifiable): JsValue = objectWithSchema.schema()
    def read(json: JsValue): JsonSchemifiable = ???
  }

  implicit object CanonicalFormWriter extends RootJsonFormat[CanonicalForm] {
    def write(objectWithCanonical: CanonicalForm): JsValue = objectWithCanonical.parsingCanonicalForm()
    def read(json: JsValue): CanonicalForm = ???
  }

  implicit object AvroTypeWriter extends RootJsonFormat[AvroType[_]] {
    def write(at: AvroType[_]): JsValue = at.asInstanceOf[JsonSchemifiable].toJson
    def read(json: JsValue): AvroType[_] = ???
  }

  implicit object AvroRecordFieldWriter extends RootJsonFormat[AvroRecord.Field[_]] {
    def write(record: AvroRecord.Field[_]): JsValue = record.asInstanceOf[JsonSchemifiable].toJson
    def read(json: JsValue): AvroRecord.Field[_] = ???
  }

  /**
    * Workaround for spray-json generic Map formatter.  Instances of ListMap
    * are sometimes preferable, for example when one wants to control the order
    * in which object keys are written to JSON.
    */
  implicit def listMapFormat[K: JsonFormat, V: JsonFormat] = new RootJsonFormat[ListMap[K, V]] {
    def write(m: ListMap[K, V]) = JsObject {
      m.map {
        case (k, v) =>
          k.toJson match {
            case JsString(x) => x -> v.toJson
            case x           => throw new SerializationException("Map key must be formatted as JsString, not '" + x + "'")
          }
      }
    }

    def read(value: JsValue): ListMap[K, V] = ???

  }

}