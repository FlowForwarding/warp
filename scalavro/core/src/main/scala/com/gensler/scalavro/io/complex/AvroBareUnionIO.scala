package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.io.primitive.AvroLongIO
import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.complex.{AvroTaggedUnion, AvroFixedSizeEnum, AvroUnion}
import com.gensler.scalavro.types.primitive.AvroNull
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }

import com.gensler.scalavro.util.ReflectionHelpers

import com.gensler.scalavro.util.Union
import com.gensler.scalavro.util.Union._

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._
import com.gensler.scalavro.types.supply.{EnumWithDefaultValues, UInt8}

class AvroBareUnionIO[U <: Union.not[_]: TypeTag, T: TypeTag](val avroType: AvroUnion[U, T]) extends AvroUnionIO[U, T] {

  import com.gensler.scalavro.types.supply._

  private[this] val byteIO = AvroType[UInt8].io

  protected[scalavro] def writeTagByIndex(index: Int,
                                          encoder: BinaryEncoder,
                                          references: mutable.Map[Any, Long]) = {
    // Use this hack because of lack non-encoding encoder in avro.
    // TODO: Implement non-encoding encoder ASAP
    byteIO.write(UInt8.fromByte(index.toByte), encoder, references, !useReferences) // AvroLongIO.write(index.toLong, encoder)
  }

  protected[scalavro] def getIndexByTag(decoder: BinaryDecoder,
                                        references: mutable.ArrayBuffer[Any]) = {
    // Use this hack because of lack non-encoding decoder in avro.
    // TODO: Implement non-encoding decoder ASAP
    byteIO.read(decoder, references, !useReferences).b1.toInt
  }

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  private def writeImpl[X: TypeTag](
    obj: X,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean) = {

    val typeOfObj = ReflectionHelpers.classLoaderMirror.staticClass(obj.getClass.getName).toType
    val staticTypeOfObj = typeOf[X]
    avroType.memberAvroTypes.indexWhere { at => staticTypeOfObj <:< at.tag.tpe || typeOfObj <:< at.tag.tpe } match {
      case -1 => throw new AvroSerializationException(obj)
      case index: Int => {
        writeTagByIndex(index, encoder, references)
        val memberType = avroType.memberAvroTypes(index).asInstanceOf[AvroType[T]]
        memberType.io.write(obj.asInstanceOf[T], encoder, references, !useReferences)
      }
    }
  }

  // Works for classes extends the union trait. They must be members of the union as well.
  protected[scalavro] def write[X <: T: TypeTag](
    obj: X,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean): Unit = {

    writeImpl(obj, encoder, references, topLevel)
  }

  def writeBare[X: prove[T]#containsType: TypeTag](
    obj: X,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean) = {

    writeImpl(obj, encoder, references, topLevel)
  }

  protected[scalavro] def read(
    decoder: BinaryDecoder,
    references: mutable.ArrayBuffer[Any],
    topLevel: Boolean) = {
    val index = getIndexByTag(decoder, references)

    val memberType = avroType.memberAvroTypes(index).asInstanceOf[AvroType[T]]
    memberType.io.read(decoder, references, !useReferences)
  }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writeJson[X <: T: TypeTag](obj: X) = {

    ??? // Not Implemented!
  }

  def readJson(json: JsValue) = Try {
    val memberInstance = json match {
      case JsNull => {
        resolveMemberTypeFromCompactSchema(AvroNull.compactSchema) match {
          case None           => throw new AvroDeserializationException[T]
          case Some(nullType) => Unit
        }
      }
      case JsObject(fields) if fields.size == 1 => {
        val (compactSchema, valueJson) = fields.head

        resolveMemberTypeFromCompactSchema(AvroNull.compactSchema) match {
          case None             => throw new AvroDeserializationException[T]
          case Some(memberType) => readJsonHelper(valueJson, memberType)
        }
      }
    }
    memberInstance.asInstanceOf[T]
  }

  protected[this] def resolveMemberTypeFromCompactSchema(schema: JsValue): Option[AvroType[_]] =
    avroType.memberAvroTypes.find { _.compactSchema == schema }

  protected[this] def readJsonHelper[A: TypeTag](json: JsValue, argType: AvroType[A]) =
    argType.io.readJson(json).get

}

class AvroTaggedBareUnionIO[U <: Union.not[_]: TypeTag, E <: EnumWithDefaultValues[_], T: TypeTag](
  override val avroType: AvroTaggedUnion[U, E, T]) extends AvroBareUnionIO[U, T](avroType){

  implicit val tt = avroType.enumTypeTag

  val tagIO = AvroType[E#Value].io

  val sortedTags = avroType.tagsEnum.values.values.toSeq.sortBy(_.id)

  protected[scalavro] override def writeTagByIndex(index: Int,
                                          encoder: BinaryEncoder,
                                          references: mutable.Map[Any, Long]) = {
    tagIO.write(sortedTags(index), encoder, references, false)
  }

  protected[scalavro] override def getIndexByTag(decoder: BinaryDecoder,
                                                 references: mutable.ArrayBuffer[Any]) = {
    val readValue = tagIO.read(decoder, references, false)
    val res = sortedTags.zipWithIndex collectFirst { case (v, i) if v == readValue => i }
    res match {
      case Some(i) => i
      case None => throw new RuntimeException("Invalid tag read")
    }
  }
}