package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.types.primitive.AvroFloat
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.util.Try

object AvroFloatIO extends AvroFloatIO

trait AvroFloatIO extends AvroPrimitiveTypeIO[Float] {

  val avroType = AvroFloat

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write(
    value: Float,
    encoder: BinaryEncoder): Unit = encoder writeFloat value

  def read(decoder: BinaryDecoder) = decoder.readFloat

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writePrimitiveJson(value: Float) = JsNumber(BigDecimal(value.toDouble))

  def readJson(json: JsValue) = Try {
    json match {
      case JsNumber(bigDecimal) => bigDecimal.toFloat
      case _                    => throw new AvroDeserializationException[Float]
    }
  }

}
