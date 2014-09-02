package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.io.primitive.AvroLongIO

import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.AvroPrimitiveType
import com.gensler.scalavro.types.complex.AvroRecord
import com.gensler.scalavro.types.supply.RawSeqFieldsInfo
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }
import com.gensler.scalavro.util.ReflectionHelpers

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{ GenericRecord, GenericData, GenericDatumWriter }
import org.apache.avro.io.{DecoderFactory, EncoderFactory, BinaryEncoder, BinaryDecoder}


import spray.json._

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.{ TypeTag, typeTag }

import java.io.ByteArrayInputStream

case class AvroRecordIO[T](avroType: AvroRecord[T]) extends AvroTypeIO[T]()(avroType.tag) {

  implicit val tt: TypeTag[T] = avroType.tag

  import ReflectionHelpers.{ ProductElementExtractor, CaseClassFactory }

  protected[this] lazy val extractors: Map[String, ProductElementExtractor[T, _]] = {
    avroType.fields.map { field => field.name -> extractorFor(field) }.toMap
  }

  private def extractorFor[F](field: AvroRecord.Field[F]): ProductElementExtractor[T, F] = {
    implicit val ft: TypeTag[F] = field.fieldType.tag
    new ProductElementExtractor[T, F](field.name)
  }

  protected[this] lazy val factory = new CaseClassFactory[T]

  protected[this] lazy val fieldReaders: Seq[AvroTypeIO[_]] = avroType.fields.map { _.fieldType.io }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  val UNION_INDEX_RECORD: Long = 0
  val UNION_INDEX_REFERENCE: Long = 1

  protected[scalavro] def write[R <: T: TypeTag](
    obj: R,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean): Unit = {

    if (topLevel) {
      // write the object
      writeFieldValues(obj, encoder, references)
    }
    else {
      references.get(obj) match {
        case Some(id: Long) => { // the object has already been written
          // encode the union index
          AvroLongIO.write(UNION_INDEX_REFERENCE, encoder)
          // encode the reference id
          AvroLongIO.write(id, encoder, references, false)
        }

        case None => { // the object has not been written to the stream yet
          // encode the union index
          AvroLongIO.write(UNION_INDEX_RECORD, encoder)

          // write the object
          writeFieldValues(obj, encoder, references)

          // add the object to the reference map
          references += obj -> references.size

        }
      }
    }
  }

  protected[this] def writeFieldValues[R <: T: TypeTag](
    obj: R,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long]): Unit = {

    // encode the object as field values
    for (field <- avroType.fields) {
      try {
        val value = extractors(field.name).extractFrom(obj).asInstanceOf[Any]
        val fieldTag = field.fieldType.tag.asInstanceOf[TypeTag[Any]]
        field.fieldType.io.asInstanceOf[AvroTypeIO[Any]].write(value, encoder, references, !useReferences)(fieldTag)
      }
      catch {
        case cause: Throwable => throw new AvroSerializationException(
          obj,
          cause,
          "Could not extract a value for field [%s]" format field.name
        )
      }
    }
  }

  protected[scalavro] def read(
    decoder: BinaryDecoder,
    references: mutable.ArrayBuffer[Any],
    topLevel: Boolean): T = {

    if (topLevel) readObject(decoder, references)
    else {
      (AvroLongIO read decoder) match {
        case UNION_INDEX_REFERENCE => {
          val index = AvroLongIO read decoder
          references(index.toInt).asInstanceOf[T]
        }
        case UNION_INDEX_RECORD => readObject(decoder, references)
        case _                  => throw new AvroDeserializationException
      }
    }
  }

  protected[this] def fillArgs(args: scala.collection.mutable.ArrayBuffer[Any], decoder: BinaryDecoder, references: mutable.ArrayBuffer[Any], rsfInfo: RawSeqFieldsInfo) = {
    for ((reader, i) <- fieldReaders.zipWithIndex) {
      val field = reader match {
        case r: AvroRawArrayIO[_, _] =>
          val rawBytes = new Array[Byte](rsfInfo.rawFieldsLengthCalculator(i)(args))
          val readBytes = decoder.inputStream().read(rawBytes)
          require(readBytes == -1 && rawBytes.length == 0 ||
                  readBytes == rawBytes.length, s"Actual amount of raw data ($readBytes bytes) doesn't match amount supposed by RawSeqFieldsInfo (${rawBytes.length} bytes)")
          // TODO: choose type of new decoder based on actual type of argument "decoder"
          val rawSeqDecoder = DecoderFactory.get.directBinaryDecoder(new ByteArrayInputStream(rawBytes), null)
          r.read(rawSeqDecoder, references, !useReferences)
        case r => r.read(decoder, references, !useReferences)
      }
      args += field
    }
  }

  protected[this] def fillArgs(args: scala.collection.mutable.ArrayBuffer[Any], decoder: BinaryDecoder, references: mutable.ArrayBuffer[Any]) = {
    for (reader <- fieldReaders) {
      args += reader.read(decoder, references, !useReferences)
    }
  }

  protected[this] def readObject(decoder: BinaryDecoder, references: mutable.ArrayBuffer[Any]): T = {
    val args = new scala.collection.mutable.ArrayBuffer[Any](initialSize = avroType.fields.size)
    try {
      avroType.rsfInfo match{
        case Some(info) => fillArgs(args, decoder, references, info)
        case None => fillArgs(args, decoder, references)
      }
      val result = factory buildWith args
      references append result
      result
    }
    catch {
      case cause: Throwable => throw new AvroDeserializationException[T](
        cause,
        "The object's arguments were: [%s]" format args.mkString(", ")
      )
    }
  }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writeJson[R <: T: TypeTag](obj: R) = {

    val fields = avroType.fields.map { field =>
      try {
        val value = extractors(field.name).extractFrom(obj).asInstanceOf[Any]
        val fieldTag = field.fieldType.tag.asInstanceOf[TypeTag[Any]]
        field.name -> field.fieldType.io.asInstanceOf[AvroTypeIO[Any]].writeJson(value)(fieldTag)
      }
      catch {
        case cause: Throwable => throw new AvroSerializationException(
          obj,
          cause,
          "Could not extract a value for field [%s]" format field.name
        )
      }
    }

    JsObject(fields.toSeq: _*)
  }

  def readJson(json: JsValue) = Try {
    json match {
      case JsObject(fields) => {

        val args = (fields.values zip fieldReaders).map {
          case (fieldJson, reader) => reader.readJson(fieldJson).get
        }

        factory buildWith args.toSeq
      }
      case _ => throw new AvroDeserializationException[T]
    }
  }

}