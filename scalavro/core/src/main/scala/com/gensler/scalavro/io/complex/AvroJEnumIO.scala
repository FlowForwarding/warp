package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.complex.AvroJEnum
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }
import com.gensler.scalavro.util.ReflectionHelpers

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{ GenericData, GenericEnumSymbol, GenericDatumWriter, GenericDatumReader }
import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

case class AvroJEnumIO[E](avroType: AvroJEnum[E]) extends AvroTypeIO[E]()(avroType.tag) {

  protected lazy val avroSchema: Schema = (new Parser) parse avroType.selfContainedSchema().toString

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write[T <: E: TypeTag](
    obj: T,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean): Unit = {

    try {
      val datumWriter = new GenericDatumWriter[GenericEnumSymbol](avroSchema)
      datumWriter.write(
        new GenericData.EnumSymbol(avroSchema, obj.toString),
        encoder
      )
    }
    catch {
      case cause: Throwable =>
        throw new AvroSerializationException(obj, cause)
    }
  }

  protected[scalavro] def read(
    decoder: BinaryDecoder,
    references: mutable.ArrayBuffer[Any],
    topLevel: Boolean) = {

    val datumReader = new GenericDatumReader[GenericEnumSymbol](avroSchema)
    datumReader.read(null, decoder) match {
      case genericEnumSymbol: GenericEnumSymbol => avroType.symbolMap(genericEnumSymbol.toString)
      case _                                    => throw new AvroDeserializationException[E]()(avroType.tag)
    }
  }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writeJson[T <: E: TypeTag](obj: T) = JsString(obj.toString)

  def readJson(json: JsValue) = Try {
    json match {
      case JsString(valueName) => avroType.symbolMap(valueName)
      case _                   => throw new AvroDeserializationException[E]()(avroType.tag)
    }
  }
}