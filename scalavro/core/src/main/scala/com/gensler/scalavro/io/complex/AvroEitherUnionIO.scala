package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.io.primitive.AvroLongIO
import com.gensler.scalavro.types.{ AvroType, AvroComplexType, AvroNamedType, AvroPrimitiveType }
import com.gensler.scalavro.types.complex.AvroUnion
import com.gensler.scalavro.error.{ AvroSerializationException, AvroDeserializationException }
import com.gensler.scalavro.util.Union
import com.gensler.scalavro.util.Union._

import org.apache.avro.io.{ BinaryEncoder, BinaryDecoder }

import spray.json._

import scala.collection.mutable
import scala.util.{ Try, Success, Failure }
import scala.reflect.runtime.universe._

private[scalavro] case class AvroEitherUnionIO[U <: Union.not[_]: TypeTag, T <: Either[_, _]: TypeTag](
    avroType: AvroUnion[U, T]) extends AvroUnionIO[U, T] {

  val TypeRef(_, _, List(leftType, rightType)) = typeOf[T]
  val leftAvroType = avroType.memberAvroTypes.find { at => leftType <:< at.tag.tpe }.get
  val rightAvroType = avroType.memberAvroTypes.find { at => rightType <:< at.tag.tpe }.get

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write[X <: T: TypeTag](
    obj: X,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean): Unit = {

    AvroLongIO.write(if (obj.isLeft) 0L else 1L, encoder)
    writeHelper(obj, encoder, references, topLevel)(typeTag[X], leftAvroType.tag, rightAvroType.tag)
  }

  protected[this] def writeHelper[X <: T: TypeTag, A: TypeTag, B: TypeTag](
    obj: X,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean) = obj match {

    case Left(value) => leftAvroType.asInstanceOf[AvroType[A]].io.write(
      value.asInstanceOf[A],
      encoder,
      references,
      !useReferences
    )

    case Right(value) => rightAvroType.asInstanceOf[AvroType[B]].io.write(
      value.asInstanceOf[B],
      encoder,
      references,
      !useReferences
    )
  }

  protected[scalavro] def read(
    decoder: BinaryDecoder,
    references: mutable.ArrayBuffer[Any],
    topLevel: Boolean) = {

    readHelper(decoder, references, topLevel)(leftAvroType.tag, rightAvroType.tag).asInstanceOf[T]
  }

  def readHelper[A: TypeTag, B: TypeTag](
    decoder: BinaryDecoder,
    references: mutable.ArrayBuffer[Any],
    topLevel: Boolean) = {

    (AvroLongIO read decoder) match {
      case 0 => Left(leftAvroType.io.read(decoder, references, !useReferences).asInstanceOf[A])
      case 1 => Right(rightAvroType.io.read(decoder, references, !useReferences).asInstanceOf[B])
      case _ => throw new AvroDeserializationException[T]
    }
  }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writeJson[X <: T: TypeTag](obj: X) =
    try {
      if (obj.isLeft) writeJsonHelper(obj.left.get, leftAvroType)
      else writeJsonHelper(obj.right.get, rightAvroType)
    }
    catch {
      case cause: Throwable => throw new AvroSerializationException(obj, cause)
    }

  protected[this] def writeJsonHelper[A: TypeTag](obj: Any, argType: AvroType[A]) = {
    val value = obj.asInstanceOf[A]
    val valueJson = argType.io.writeJson(value)
    JsObject(simpleSchemaText(argType) -> valueJson)
  }

  def readJson(json: JsValue) = Try {
    val eitherInstance = json match {
      case JsNull => {
        if (leftAvroType.tag.tpe =:= typeOf[Unit]) Left(Unit)
        else if (rightAvroType.tag.tpe =:= typeOf[Unit]) Right(Unit)
        else throw new AvroDeserializationException[T]
      }
      case JsObject(fields) if fields.size == 1 => {
        val (compactSchema, valueJson) = fields.head
        if (simpleSchemaText(leftAvroType) == compactSchema)
          Left(readJsonHelper(valueJson, leftAvroType))
        else if (simpleSchemaText(rightAvroType) == compactSchema)
          Right(readJsonHelper(valueJson, rightAvroType))
        else throw new AvroDeserializationException[T]
      }
    }
    eitherInstance.asInstanceOf[T]
  }

  protected[this] def readJsonHelper[A: TypeTag](json: JsValue, argType: AvroType[A]) =
    argType.io.readJson(json).get

}