package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.io.primitive.{ AvroLongIO, AvroStringIO }
import com.gensler.scalavro.types.complex.AvroMap
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }
import com.gensler.scalavro.util.ReflectionHelpers

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{ GenericData, GenericDatumWriter }
import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }
import org.apache.avro.util.Utf8

import spray.json._

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe.TypeTag

case class AvroMapIO[T, M <: Map[String, T]](avroType: AvroMap[T, M]) extends AvroTypeIO[M]()(avroType.originalTypeTag) {

  implicit def itemTypeTag = avroType.itemType.tag
  implicit def originalTypeTag = avroType.originalTypeTag

  val originalTypeFactory = ReflectionHelpers.varargsFactory[M].get

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write[M <: Map[String, T]: TypeTag](
    map: M,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean): Unit = {

    try {
      encoder.writeMapStart
      encoder.setItemCount(map.size)
      for ((key, value) <- map) {
        encoder.startItem
        encoder writeString key
        avroType.itemType.io.write(value, encoder, references, !useReferences)
      }
      encoder.writeMapEnd
    }
    catch {
      case cause: Throwable =>
        throw new AvroSerializationException(map, cause)
    }
  }

  protected[scalavro] def read(
    decoder: BinaryDecoder,
    references: mutable.ArrayBuffer[Any],
    topLevel: Boolean) = {

    val items = new scala.collection.mutable.ArrayBuffer[(String, T)]

    def readBlock(): Long = {
      val numItems = (AvroLongIO read decoder)
      val absNumItems = math abs numItems
      if (numItems < 0L) { val bytesInBlock = AvroLongIO read decoder }
      (0L until absNumItems) foreach { _ =>
        val key = AvroStringIO read decoder
        val value = avroType.itemType.io.read(decoder, references, !useReferences)
        items += key -> value
      }
      absNumItems
    }

    var itemsRead = readBlock()
    while (itemsRead != 0L) { itemsRead = readBlock() }
    originalTypeFactory(items)
  }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writeJson[G <: Map[String, T]: TypeTag](map: G) = {
    val fields = map.map {
      case (key, value) =>
        key -> avroType.itemType.io.writeJson(value)
    }
    JsObject(fields.toSeq: _*)
  }

  def readJson(json: JsValue) = Try {
    json match {
      case JsObject(fields) => {
        val items: Seq[(String, T)] = fields.map {
          case (key, valueAsJson) =>
            key -> avroType.itemType.io.readJson(valueAsJson).get
        }.toSeq
        originalTypeFactory(items)
      }
      case _ => throw new AvroDeserializationException[M]
    }
  }

}