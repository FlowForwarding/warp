package com.gensler.scalavro.types.supply

import com.gensler.scalavro.util.FixedData
import java.nio.{ByteBuffer, ByteOrder}

@FixedData.Length(1)
case class UInt8(b1: Byte = 0)
  extends FixedData(List(b1)){
  private[scalavro] def this(bytes: Bytes) = this(bytes(0))
}

@FixedData.Length(2)
case class UInt16(b1: Byte = 0, b2: Byte = 0)
  extends FixedData(List(b1, b2)){
  private[scalavro] def this(bytes: Bytes) = this(bytes(0), bytes(1))
}

@FixedData.Length(3)
case class UInt24(b1: Byte = 0, b2: Byte = 0, b3: Byte = 0)
  extends FixedData(List(b1, b2, b3)){
  private[scalavro] def this(bytes: Bytes) = this(bytes(0), bytes(1), bytes(2))
}

@FixedData.Length(4)
case class UInt32(b1: Byte = 0, b2: Byte = 0, b3: Byte = 0, b4: Byte = 0)
  extends FixedData(List(b1, b2, b3, b4)){
  private[scalavro] def this(bytes: Bytes) = this(bytes(0), bytes(1), bytes(2), bytes(3))
}

@FixedData.Length(6)
case class UInt48(b1: Byte = 0, b2: Byte = 0, b3: Byte = 0, b4: Byte = 0, b5: Byte = 0, b6: Byte = 0)
  extends FixedData(List(b1, b2, b3, b4, b5, b6)){
  private[scalavro] def this(bytes: Bytes) = this(bytes(0), bytes(1), bytes(2), bytes(3), bytes(4), bytes(5))
}

@FixedData.Length(8)
case class UInt64(b1: Byte = 0, b2: Byte = 0, b3: Byte = 0, b4: Byte = 0, b5: Byte = 0, b6: Byte = 0, b7: Byte = 0, b8: Byte = 0)
  extends FixedData(List(b1, b2, b3, b4, b5, b6, b7, b8)){
  private[scalavro] def this(bytes: Bytes) = this(bytes(0), bytes(1), bytes(2), bytes(3), bytes(4), bytes(5), bytes(6), bytes(7))
}

@FixedData.Length(16)
case class UInt128(b1: Byte = 0, b2: Byte = 0, b3: Byte = 0, b4: Byte = 0, b5: Byte = 0, b6: Byte = 0, b7: Byte = 0, b8: Byte = 0,
                   b9: Byte = 0, b10: Byte = 0, b11: Byte = 0, b12: Byte = 0, b13: Byte = 0, b14: Byte = 0, b15: Byte = 0, b16: Byte = 0)
  extends FixedData(List(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16)){
  private[scalavro] def this(bytes: Bytes) = this(bytes(0), bytes(1), bytes(2), bytes(3), bytes(4), bytes(5), bytes(6), bytes(7),
                                                  bytes(8), bytes(9), bytes(10), bytes(11), bytes(12), bytes(13), bytes(14), bytes(15))
}

import java.nio.ByteBuffer.{allocate => allocateBuffer}

object UInt8{
  def toByte(value: UInt8): Byte  = value.b1
  def fromByte(data: Byte): UInt8 = UInt8(data)
}

object UInt16{
  def toShort(value: UInt16)(implicit bo: ByteOrder = ByteOrder.BIG_ENDIAN): Short  = fixedToBuffer(value, bo).getShort
  def fromShort(data: Short)(implicit bo: ByteOrder = ByteOrder.BIG_ENDIAN): UInt16 = new UInt16(allocateBuffer(2).order(bo).putShort(data).array().toList)
}

object UInt32{
  def toInt(value: UInt32)(implicit bo: ByteOrder = ByteOrder.BIG_ENDIAN): Int  = fixedToBuffer(value, bo).getInt
  def fromInt(data: Int)(implicit bo: ByteOrder = ByteOrder.BIG_ENDIAN): UInt32 = new UInt32(allocateBuffer(4).order(bo).putInt(data).array().toList)
}

object UInt48{
  def toLong(value: UInt48)(implicit bo: ByteOrder = ByteOrder.BIG_ENDIAN): Long  = {
    val zeros = Seq[Byte](0, 0)
    val bytes = if (bo == ByteOrder.BIG_ENDIAN) zeros ++ value.bytes else value.bytes ++ zeros
    ByteBuffer.wrap(bytes.toArray).order(bo).getLong
  }
  def fromLong(data: Long)(implicit bo: ByteOrder = ByteOrder.BIG_ENDIAN): UInt48 = {
    val bytes = allocateBuffer(8).order(bo).putLong(data).array().toList
    val cropBytes = if(bo == ByteOrder.BIG_ENDIAN) bytes.splitAt(2)._2 else bytes.splitAt(6)._1
    new UInt48(cropBytes)
  }
}

object UInt64{
  def toLong(value: UInt64)(implicit bo: ByteOrder = ByteOrder.BIG_ENDIAN): Long  = fixedToBuffer(value, bo).getLong
  def fromLong(data: Long)(implicit bo: ByteOrder = ByteOrder.BIG_ENDIAN): UInt64 = new UInt64(allocateBuffer(8).order(bo).putLong(data).array().toList)
}