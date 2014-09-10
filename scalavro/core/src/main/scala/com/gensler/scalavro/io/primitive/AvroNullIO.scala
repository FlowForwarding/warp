package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.types.primitive.AvroNull
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.util.Try

object AvroNullIO extends AvroNullIO

trait AvroNullIO extends AvroPrimitiveTypeIO[Unit] {

  val avroType = AvroNull

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  // null is written as zero bytes.
  protected[scalavro] def write(
    value: Unit,
    encoder: BinaryEncoder): Unit = {}

  def read(decoder: BinaryDecoder) = ()

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writePrimitiveJson(value: Unit): JsValue = JsNull

  def readJson(json: JsValue) = Try {
    json match {
      case JsNull => Unit
      case _      => throw new AvroDeserializationException[Unit]
    }
  }
}