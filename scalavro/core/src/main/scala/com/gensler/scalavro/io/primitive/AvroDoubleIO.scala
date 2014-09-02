package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.types.primitive.AvroDouble
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.util.Try

object AvroDoubleIO extends AvroDoubleIO

trait AvroDoubleIO extends AvroPrimitiveTypeIO[Double] {

  val avroType = AvroDouble

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write(
    value: Double,
    encoder: BinaryEncoder): Unit = encoder writeDouble value

  def read(decoder: BinaryDecoder) = decoder.readDouble

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writePrimitiveJson(value: Double) = JsNumber(BigDecimal(value))

  def readJson(json: JsValue) = Try {
    json match {
      case JsNumber(bigDecimal) => bigDecimal.toDouble
      case _                    => throw new AvroDeserializationException[Double]
    }
  }

}