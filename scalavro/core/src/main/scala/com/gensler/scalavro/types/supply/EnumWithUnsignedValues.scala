package com.gensler.scalavro.types.supply

import java.nio.ByteBuffer

import scala.util.Try

import com.gensler.scalavro.util.FixedData

trait EnumWithUnsignedValues[Elem, Unsigned <: FixedData] extends EnumWithDefaultValues[Elem]{
  protected def longToElem(l: Long): Elem
  protected def bytesToElem(bytes: Array[Byte]): Elem

  protected def elemToValue(elem: Elem) = {
    val values = this.values.values.filter { _.data == elem }
    values.size match {
      case 0 => Try { ValueFactory.set(elem) } getOrElse ValueFactory.unspecified(elem)
      case 1 => values.head
      case _ => ValueFactory.unresolved(values.head.data)
    }
  }

  def valueFromBytes(bytes: Array[Byte]) = elemToValue(bytesToElem(bytes))
  def valueFromLong(l: Long) = elemToValue(longToElem(l))
}

trait ByteEnum extends EnumWithUnsignedValues[Byte, UInt8]{
  protected def ##(data: Long): Predefined = {
    if(data < 0 || data > Byte.MaxValue.toLong - Byte.MinValue.toLong)
      throw new IllegalArgumentException("Value must be positive and fit to 8 bits")
    else if (data > Byte.MaxValue)
      super.value((data - 2 * Byte.MaxValue).toByte)
    else
      super.value(data.toByte)
  }

  protected def longToElem(l: Long): Byte = l.toByte
  protected def bytesToElem(bytes: Array[Byte]): Byte = {
    assert(bytes.length == 1)
    bytes(0)
  }
}

trait WordEnum extends EnumWithUnsignedValues[Short, UInt16]{
  protected def ##(data: Long): Predefined = {
    if(data < 0 || data > Short.MaxValue.toLong - Short.MinValue.toLong)
      throw new IllegalArgumentException("Value must be positive and fit to 16 bits")
    else if (data > Short.MaxValue)
      super.value((data - 2 * Short.MaxValue).toShort)
    else
      super.value(data.toShort)
  }

  protected def bytesToElem(bytes: Array[Byte]): Short = {
    assert(bytes.length == 2)
    ByteBuffer.wrap(bytes).getShort
  }
  protected def longToElem(l: Long): Short = l.toShort
}

trait DWordEnum extends EnumWithUnsignedValues[Int, UInt32]{
  protected def ##(data: Long): Predefined = {
    if(data < 0 || data > Int.MaxValue.toLong - Int.MinValue.toLong)
      throw new IllegalArgumentException("Value must be positive and fit to 32 bits")
    else if (data > Int.MaxValue)
      super.value((data - 2 * Int.MaxValue).toInt)
    else
      super.value(data.toInt)
  }

  protected def bytesToElem(bytes: Array[Byte]): Int = {
    assert(bytes.length == 4)
    ByteBuffer.wrap(bytes).getInt
  }
  protected def longToElem(l: Long): Int = l.toInt
}

trait Bitmap[T] extends AllowValuesSet[T]{ _: EnumWithDefaultValues[T] =>
  protected val size: Int
  protected def toResult(i: Int): T
  protected def toInt(t: T): Int

  def joinValues(values: Set[T]): T = toResult(values.map(toInt).fold(0) { _ | _ })
  def divideValue(value: T): Set[T] = (0 until size).toSet map { (s: Int) => 1 << s } collect { case mask if (toInt(value) & mask) != 0 => toResult(mask) }
}

trait ByteBitmap extends ByteEnum with Bitmap[Byte] {
  protected val size = 8
  protected def toResult(i: Int) = i.toByte
  protected def toInt(b: Byte) = b.toInt
}

trait WordBitmap extends WordEnum with Bitmap[Short] {
  protected val size = 16
  protected def toResult(i: Int) = i.toShort
  protected def toInt(s: Short) = s.toInt
}

trait DWordBitmap extends DWordEnum with Bitmap[Int] {
  protected val size = 32
  protected def toResult(i: Int) = i
  protected def toInt(i: Int) = i
}