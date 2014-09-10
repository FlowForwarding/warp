package com.gensler.scalavro.io.complex

import com.gensler.scalavro.io.AvroTypeIO
import com.gensler.scalavro.io.primitive.AvroLongIO
import com.gensler.scalavro.types.AvroType
import com.gensler.scalavro.types.complex.AvroUnion
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

private[scalavro] case class AvroClassUnionIO[U <: Union.not[_]: TypeTag, T: TypeTag](
    avroType: AvroUnion[U, T]) extends AvroUnionIO[U, T] {

  ////////////////////////////////////////////////////////////////////////////
  // BINARY ENCODING
  ////////////////////////////////////////////////////////////////////////////

  protected[scalavro] def write[X <: T: TypeTag](
    obj: X,
    encoder: BinaryEncoder,
    references: mutable.Map[Any, Long],
    topLevel: Boolean): Unit = {

    val staticTypeOfObj = typeOf[X]
    val runtimeTypeOfObj = ReflectionHelpers.classLoaderMirror.staticClass(obj.getClass.getName).toType
    val objTypeTag = ReflectionHelpers.tagForType(runtimeTypeOfObj)

    avroType.memberAvroTypes.indexWhere {
      at => staticTypeOfObj <:< at.tag.tpe || runtimeTypeOfObj <:< at.tag.tpe
    } match {
      case -1 => throw new AvroSerializationException(obj)
      case index: Int => {
        AvroLongIO.write(index.toLong, encoder)
        val memberType = avroType.memberAvroTypes(index).asInstanceOf[AvroType[X]]
        memberType.io.write(obj, encoder, references, !useReferences)(objTypeTag.asInstanceOf[TypeTag[X]])
      }
    }
  }

  protected[scalavro] def read(
    decoder: BinaryDecoder,
    references: mutable.ArrayBuffer[Any],
    topLevel: Boolean) = {

    val index = AvroLongIO.read(decoder)
    val memberType = avroType.memberAvroTypes(index.toInt).asInstanceOf[AvroType[T]]
    memberType.io.read(decoder, references, !useReferences)
  }

  ////////////////////////////////////////////////////////////////////////////
  // JSON ENCODING
  ////////////////////////////////////////////////////////////////////////////

  def writeJson[X <: T: TypeTag](obj: X) = {
    val staticTypeOfObj = typeOf[X]
    val runtimeTypeOfObj = ReflectionHelpers.classLoaderMirror.staticClass(obj.getClass.getName).toType
    val objTypeTag = ReflectionHelpers.tagForType(runtimeTypeOfObj)

    avroType.memberAvroTypes.find {
      at => staticTypeOfObj <:< at.tag.tpe || runtimeTypeOfObj <:< at.tag.tpe
    } match {
      case None => throw new AvroSerializationException(obj)
      case Some(memberType) => {
        val argType = memberType.asInstanceOf[AvroType[X]]
        val valueJson = argType.io.writeJson(obj)(objTypeTag.asInstanceOf[TypeTag[X]])
        JsObject(simpleSchemaText(argType) -> valueJson)
      }
    }
  }

  def readJson(json: JsValue) = Try {
    val subclassInstance = json match {
      case JsNull => {
        resolveMemberTypeFromCompactSchema(simpleSchemaText(AvroNull)) match {
          case None           => throw new AvroDeserializationException[T]
          case Some(nullType) => Unit
        }
      }
      case JsObject(fields) if fields.size == 1 => {
        val (compactSchema, valueJson) = fields.head

        resolveMemberTypeFromCompactSchema(compactSchema) match {
          case None             => throw new AvroDeserializationException[T]
          case Some(memberType) => readJsonHelper(valueJson, memberType)
        }
      }
    }
    subclassInstance.asInstanceOf[T]
  }

  protected[this] def readJsonHelper[A: TypeTag](json: JsValue, argType: AvroType[A]) =
    argType.io.readJson(json).get

  protected[this] def resolveMemberTypeFromCompactSchema(schema: String): Option[AvroType[_]] =
    avroType.memberAvroTypes.find { simpleSchemaText(_) == schema }

}