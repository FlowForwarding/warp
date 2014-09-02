package com.gensler.scalavro.io.primitive

import com.gensler.scalavro.io.AvroTypeIO

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.collection.mutable
import scala.reflect.runtime.universe.TypeTag

trait AvroPrimitiveTypeIO[T] extends AvroTypeIO[T] {

  final protected[scalavro] def write[V <: T: TypeTag](
    value: V,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean): Unit = write(value, encoder)

  protected[scalavro] def write(value: T, encoder: BinaryEncoder): Unit

  final protected[scalavro] def read(
    decoder: BinaryDecoder,
    references: mutable.ArrayBuffer[Any],
    topLevel: Boolean) = read(decoder)

  protected[scalavro] def read(decoder: BinaryDecoder): T

  final def writeJson[V <: T: TypeTag](value: V) =
    writePrimitiveJson(value.asInstanceOf[T])

  protected[scalavro] def writePrimitiveJson(value: T): JsValue

}
