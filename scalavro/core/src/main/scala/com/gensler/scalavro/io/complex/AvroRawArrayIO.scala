package com.gensler.scalavro.io.complex

import scala.collection.mutable
import scala.reflect.runtime.universe.TypeTag

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import com.gensler.scalavro.types.complex.AvroRawArray
import com.gensler.scalavro.error.{AvroDeserializationException, AvroSerializationException}
import com.gensler.scalavro.types.supply.RawSeq

class AvroRawArrayIO[T, S <: RawSeq[T]](t: AvroRawArray[T, S]) extends AvroArrayIO[T, S](t) {

  override protected[scalavro] def write[G <: Seq[T]: TypeTag](
                                                       items: G,
                                                       encoder: BinaryEncoder,
                                                       references: mutable.Map[Any, Long],
                                                       topLevel: Boolean): Unit = {
    try {
      for (item <- items)
        avroType.itemType.io.write(item, encoder, references, !useReferences)
    }
    catch {
      case cause: Throwable =>
        throw new AvroSerializationException(items, cause)
    }
  }

  override protected[scalavro] def read(
                                decoder: BinaryDecoder,
                                references: mutable.ArrayBuffer[Any],
                                topLevel: Boolean) = {
    val items = new scala.collection.mutable.ArrayBuffer[T]
    try {
      // This is not a correct way to check whether end of stream is reached.
      // Decoder.isEnd should be used, but it is not implemented for DirectBinaryDecoder.
      while(decoder.inputStream().available() != 0)
        items += avroType.itemType.io.read(decoder, references, !useReferences)
      originalTypeFactory(items) // a Seq is passed to varargs MethodMirror.apply
    }
    catch {
      case cause: Throwable =>
        throw new AvroDeserializationException[T](cause)
    }
  }
}