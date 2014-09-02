package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.types.primitive.AvroXml
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import org.apache.avro.generic.GenericData
import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.util.Try
import scala.xml.{ XML, Node }

object AvroXmlIO extends AvroXmlIO

trait AvroXmlIO extends AvroPrimitiveTypeIO[Node] {

  val avroType = AvroXml

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write(
    value: Node,
    encoder: BinaryEncoder): Unit = encoder writeString value.toString

  def read(decoder: BinaryDecoder) = XML loadString decoder.readString

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writePrimitiveJson(value: Node) = JsString(value.toString)

  def readJson(json: JsValue) = Try {
    json match {
      case JsString(value) => XML loadString value
      case _               => throw new AvroDeserializationException[Node]
    }
  }

}
