package com.gensler.scalavro.types.complex

import java.nio.ByteOrder

import scala.collection.{immutable, mutable}
import scala.collection.immutable.ListMap
import scala.reflect.runtime.universe._

import spray.json._

import com.gensler.scalavro.types.{AvroType, AvroNamedType}
import com.gensler.scalavro.types.supply._

import com.gensler.scalavro.util._
import com.gensler.scalavro.util.Union._
import com.gensler.scalavro.JsonSchemaProtocol._


/* Enumeration with predefined size and default values */
class AvroFixedSizeEnum[Elem,
                        Unsigned <: FixedData :prove [AvroFixedSizeEnum.EnumData] #containsType: TypeTag,
                        E <: EnumWithUnsignedValues[Elem, Unsigned]: TypeTag](
    val enum: E,
    val namespace: Option[String] = None) extends AvroNamedType[E#Value] {

  val name = enum.getClass.getSimpleName stripSuffix "$"

  val enumTag = typeTag[E]

  val valuesType = AvroType[Unsigned]

  val typeName = "enum"

  def selfContainedSchema(resolvedSymbols: mutable.Set[String] = mutable.Set[String]()) = {
    val requiredParams = ListMap(
      "name" -> name.toJson,
      "type" -> typeName.toJson,
      "items" -> implicitly[TypeTag[Unsigned]].tpe.typeSymbol.name.toString.toJson,
      "list" -> new JsArray(
        enum.values
          .map { case (n, elem) => new JsObject(ListMap("name" -> n.toJson, "default" -> elem.data.toString.toJson)) }
          .toVector)
    )

    val optionalParams = ListMap(
      "namespace" -> namespace
    ).collect { case (k, Some(v)) => (k, v.toJson) }

    new JsObject(requiredParams ++ optionalParams)
  }
}

object AvroFixedSizeEnum {

  type EnumData = union [UInt8] #or [UInt16] #or [UInt32]

  import scala.reflect.runtime.universe._

  def enumObjectForValueType[T](implicit tt: TypeTag[T]) = {
    val TypeRef(TypeRef(enum, _, _), _, _) = typeOf[T]
    ReflectionHelpers.getCompanionObject(enum)
  }

  private[scalavro] def fromType[E <: EnumWithUnsignedValues[_, _]: TypeTag](processedTypes: Set[Type]): AvroType[EnumWithUnsignedValues[_, _]] = {
    fromObject(enumObjectForValueType[E#Value])
  }

  private[scalavro] def fromObject(obj: Any): AvroType[EnumWithUnsignedValues[_, _]] = {
    val res = obj match{
      case u8:  ByteEnum  => new AvroFixedSizeEnum[Byte,  UInt8,  ByteEnum](u8)//fromEnum(UInt8.fromByte)(u8)
      case u16: WordEnum  => new AvroFixedSizeEnum[Short, UInt16, WordEnum](u16)   // TODO: parametrize order of bytes
      case u32: DWordEnum => new AvroFixedSizeEnum[Int,   UInt32, DWordEnum](u32)
      case _ => throw new RuntimeException("Only 8, 16 and 32 bits fixed-size enumeration allowed.")
    }
    res.asInstanceOf[AvroType[EnumWithUnsignedValues[_, _]]]
  }
}
