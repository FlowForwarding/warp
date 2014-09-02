package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.types.complex.AvroEnum
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

case class AvroEnumIO[E <: Enumeration](avroType: AvroEnum[E]) extends AvroTypeIO[E#Value]()(avroType.tag) {

  // AvroEnum exposes two TypeTags:
  //   `AvroEnum.tag` is the TypeTag for the enum values
  //   `AvroEnum.enumTag` is the TypeTag of the enum itself

  protected lazy val avroSchema: Schema = (new Parser) parse avroType.selfContainedSchema().toString

  val moduleMirror = ReflectionHelpers.classLoaderMirror.reflectModule {
    avroType.enumTag.tpe.typeSymbol.asClass.module.asModule
  }

  val enumeration = moduleMirror.instance.asInstanceOf[E]

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write[T <: E#Value: TypeTag](
    obj: T,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean): Unit = {

    try {
      val datumWriter = new GenericDatumWriter[GenericEnumSymbol](avroSchema)
      obj match {
        case value: E#Value => datumWriter.write(
          new GenericData.EnumSymbol(avroSchema, value.toString),
          encoder
        )
        case _ => throw new AvroSerializationException(obj)
      }
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
      case genericEnumSymbol: GenericEnumSymbol => enumeration withName genericEnumSymbol.toString
      case _                                    => throw new AvroDeserializationException[E#Value]()(avroType.tag)
    }
  }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writeJson[T <: E#Value: TypeTag](obj: T) = JsString(obj.toString)

  def readJson(json: JsValue) = Try {
    json match {
      case JsString(valueName) => enumeration withName valueName
      case _                   => throw new AvroDeserializationException[E#Value]()(avroType.tag)
    }
  }

}