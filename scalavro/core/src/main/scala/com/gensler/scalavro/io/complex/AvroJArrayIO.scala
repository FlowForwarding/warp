package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.io.primitive.AvroLongIO
import com.gensler.scalavro.types.complex.AvroJArray
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }
import com.gensler.scalavro.util.ReflectionHelpers

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import org.apache.avro.generic.{ GenericData, GenericArray, GenericDatumWriter }
import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.collection.mutable
import scala.util.Try
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

case class AvroJArrayIO[T](avroType: AvroJArray[T]) extends AvroTypeIO[Array[T]]()(avroType.originalTypeTag) {

  implicit def itemTypeTag = avroType.itemType.tag
  implicit def originalTypeTag = avroType.originalTypeTag

  implicit val itemClassTag: ClassTag[T] =
    ReflectionHelpers.classTagForType(avroType.itemType.tag.tpe).asInstanceOf[ClassTag[T]]

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write[A <: Array[T]: TypeTag](
    items: A,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean): Unit = {

    try {
      encoder.writeArrayStart
      encoder.setItemCount(items.size)
      for (item <- items) {
        encoder.startItem
        avroType.itemType.io.write(item, encoder, references, !useReferences)
      }
      encoder.writeArrayEnd
    }
    catch {
      case cause: Throwable =>
        throw new AvroSerializationException(items, cause)
    }
  }

  protected[scalavro] def read(
    decoder: BinaryDecoder,
    references: mutable.ArrayBuffer[Any],
    topLevel: Boolean) = {

    val items = new scala.collection.mutable.ArrayBuffer[T]

    def readBlock(): Long = {
      val numItems = (AvroLongIO read decoder)
      val absNumItems = math abs numItems
      if (numItems < 0L) { val bytesInBlock = (AvroLongIO read decoder) }
      (0L until absNumItems) foreach { _ =>
        items += avroType.itemType.io.read(decoder, references, !useReferences)
      }
      absNumItems
    }

    var itemsRead = readBlock()
    while (itemsRead != 0L) { itemsRead = readBlock() }
    items.toArray
  }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writeJson[A <: Array[T]: TypeTag](items: A) = {
    val itemsAsJson = items.map { item =>
      avroType.itemType.io.writeJson(item)
    }
    JsArray(itemsAsJson: _*)
  }

  def readJson(json: JsValue) = Try {
    json match {
      case JsArray(itemsAsJson) => {
        val items = itemsAsJson.map { json =>
          avroType.itemType.io.readJson(json).get
        }
        items.toArray
      }
      case _ => throw new AvroDeserializationException[Array[T]]
    }
  }

}