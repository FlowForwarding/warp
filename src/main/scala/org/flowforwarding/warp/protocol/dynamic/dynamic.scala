package org.flowforwarding.warp.protocol

import scala.util.Try
import scala.language.dynamics
import scala.reflect.runtime.universe._

import com.gensler.scalavro.types.supply._

import com.gensler.scalavro.util.ReflectionHelpers
import org.flowforwarding.warp.controller.driver_interface.MessageDriver

package object dynamic{

  trait DriverWithReflectionSupport[T <: OfpMsg[_, _]] extends MessageDriver[T] {
      def dynamic2Static(msg: ReflectiveStructure): Try[T]
  }

  def methodByName(im: InstanceMirror, methodName: String) = {
    val at = TermName(methodName)
    val ts = im.symbol.typeSignature
    (ts member at).asMethod
  }

  def defaultArgsOfMethod(im: InstanceMirror, method: MethodSymbol): Map[Int, Any] = {
    val argsSeq = for{i <- 0 until method.paramLists.flatten.length
                      defarg = im.symbol.typeSignature member TermName(s"${method.name}$$default$$${i+1}") if defarg != NoSymbol
                      value = (im reflectMethod defarg.asMethod)()}
                  yield (i, value)

    argsSeq.toMap
  }

  def getDefaultConstructor(constructorSymbol: Symbol) =
    if (constructorSymbol.isMethod) constructorSymbol.asMethod
    else {
      val ctors = constructorSymbol.asTerm.alternatives
      ctors.map { _.asMethod }.find { _.isPrimaryConstructor }.get
    }

  private[dynamic] val toLong: PartialFunction[Any, Long] = {
    case l: Long            => l
    case i: Int             => i.toLong
    case s: Short           => s.toLong
    case b: Byte            => b.toLong

    case ui64: UInt64          => UInt64.toLong(ui64)
    case ui32: UInt32          => UInt32.toInt(ui32).toLong
    case ui16: UInt16          => UInt16.toShort(ui16).toLong
    case ui8:  UInt8           => UInt8.toByte(ui8).toLong

    case enumVal: EnumWithDefaultValues[_]#Value => enumVal.data match {
      case b: Byte  => b.toLong
      case s: Short => s.toLong
      case i: Int   => i.toLong
      case l: Long  => l
    }
  }

  private[dynamic] def fromLong(t: Type, value: Long): Any =
    if(t == typeOf[UInt64])
      UInt64.fromLong(value)
    else if(t == typeOf[UInt48])
      UInt48.fromLong(value)
    else if(t == typeOf[UInt32])
      UInt32.fromInt(value.toInt)
    else if(t == typeOf[UInt16])
      UInt16.fromShort(value.toShort)
    else if(t == typeOf[UInt8])
      UInt8.fromByte(value.toByte)
    else if (t.baseClasses.head.owner == typeOf[EnumWithDefaultValues[_]].typeSymbol){
      val TypeRef(enumType, _, _) = t
      val enum = ReflectionHelpers.getCompanionObject(enumType).asInstanceOf[EnumWithUnsignedValues[_, _]]
      enum.valueFromLong(value)
    }
}