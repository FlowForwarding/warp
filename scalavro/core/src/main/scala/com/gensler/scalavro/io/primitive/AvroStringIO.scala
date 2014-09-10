package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.types.primitive.AvroString
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.generic.GenericData
import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.util.Try

object AvroStringIO extends AvroStringIO

trait AvroStringIO extends AvroPrimitiveTypeIO[String] {

  val avroType = AvroString

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write(
    value: String,
    encoder: BinaryEncoder): Unit = encoder writeString value

  def read(decoder: BinaryDecoder) = decoder.readString

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writePrimitiveJson(value: String) = JsString(value)

  def readJson(json: JsValue) = Try {
    json match {
      case JsString(value) => value
      case _               => throw new AvroDeserializationException[String]
    }
  }

}
