package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.complex.AvroFixedSizeEnum
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }
import com.gensler.scalavro.types.supply._
import com.gensler.scalavro.util.FixedData

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic._
import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

import java.nio.ByteOrder

/* Elem should be Byte, Short or Int */
/* Unfortunately, there are problems with usage of union type to prove it (implicit parameters required) */
case class AvroFixedSizeEnumIO[Elem, U <: FixedData, E <: EnumWithUnsignedValues[Elem, U]](
    avroType: AvroFixedSizeEnum[Elem, U, E]) extends AvroTypeIO[E#Value]()(avroType.tag) {

  // AvroEnum exposes two TypeTags:
  //   `AvroEnum.tag` is the TypeTag for the enum values
  //   `AvroEnum.enumTag` is the TypeTag of the enum itself

  protected lazy val avroSchema: Schema =
    (new Parser) parse (avroType.valuesType.schema().toString // TODO: Think about place of item schema definition.
                      + avroType.selfContainedSchema().toString)

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write[T <: E#Value: TypeTag](
    obj: T,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean): Unit = {

    try {
      val datumWriter = new GenericDatumWriter[GenericFixed](avroSchema)
      val fixedData = obj.data match {
        case b: Byte  => UInt8.fromByte(b)
        case s: Short => UInt16.fromShort(s)
        case i: Int   => UInt32.fromInt(i)
      }
      datumWriter.write(
        new GenericData.Fixed(avroSchema, fixedData.bytes.toArray),
        encoder)
    }
    catch {
      case cause: Throwable =>
        throw new AvroSerializationException(obj, cause)
    }
  }

  protected[scalavro] def read(
    decoder: BinaryDecoder,
    references: mutable.ArrayBuffer[Any],
    topLevel: Boolean): E#Value = {

    val datumReader = new GenericDatumReader[GenericFixed](avroSchema)
    datumReader.read(null, decoder) match {
      case genericFixed: GenericFixed =>
        val bytes = genericFixed.bytes
        avroType.enum.valueFromBytes(bytes).asInstanceOf[E#Value]
      case _ => throw new AvroDeserializationException[E#Value]()(avroType.tag)
    }
  }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writeJson[T <: E#Value: TypeTag](obj: T) = JsString(obj.toString)

  def readJson(json: JsValue): Try[E#Value]= Try {
    json match {
      case JsString(valueName) => avroType.enum.values(valueName).asInstanceOf[E#Value]
      case _                   => throw new AvroDeserializationException[E#Value]()(avroType.tag)
    }
  }

}