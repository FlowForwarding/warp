/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed

import java.nio.ByteBuffer
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl

import spire.math._

package object util{
  import com.typesafe.config.Config

  private[fixed] implicit class ConfigExt(namesConfig: Config){
    def getTypeName[T: Class]: String = {
      val StructureName(name) = implicitly[Class[T]]
      namesConfig.getConfig(name).getString("type_name")
    }
  }

  import scala.reflect.{ClassTag, classTag}

  // TODO: generalize methods
  private[fixed] implicit class BitmapFromLong(l: ULong) {

    private def testBit(bit: Int): Boolean = (l & (ULong(1) << bit)) != ULong(0)

    def toBitmap[T <: Bitmap: ClassTag]: Option[T] = {
      classTag[T].runtimeClass.getConstructors.collectFirst {
        case c if c.getParameterTypes.forall(_ == classOf[Boolean]) =>
          val optionsCount = c.getParameterTypes.length
          val args = (0 until optionsCount) map { idx => testBit(idx).asInstanceOf[AnyRef] }
          c.newInstance(args: _*).asInstanceOf[T]
      }
    }

    def toBitmap[T <: Bitmap: ClassTag](bits: IndexedSeq[UInt]): Option[T] = {
      classTag[T].runtimeClass.getConstructors.collectFirst {
        case c if c.getParameterTypes.forall(_ == classOf[Boolean]) &&
                  c.getParameterTypes.length == bits.size =>
          val args = (0 until bits.size) map { idx => testBit(idx).asInstanceOf[AnyRef] }
          c.newInstance(args: _*).asInstanceOf[T]
      }
    }
  }

  private[fixed] implicit object UByteSize extends HasSize[UByte]{
    def size = UByte(1)
    def bytes(instance: UByte): Array[Byte] = Array(instance.toByte)
  }
  private[fixed] implicit object UShortSize extends HasSize[UShort]{
    def size = UByte(2)
    def bytes(instance: UShort): Array[Byte] =  ByteBuffer.allocate(size.toInt).putShort(instance.toShort).array()
  }
  private[fixed] implicit object UIntSize extends HasSize[UInt]{
    def size = UByte(4)
    def bytes(instance: UInt): Array[Byte] = ByteBuffer.allocate(size.toInt).putInt(instance.toInt).array()
  }
  private[fixed] implicit object ULongSize extends HasSize[ULong]{
    def size = UByte(8)
    def bytes(instance: ULong): Array[Byte] = ByteBuffer.allocate(size.toInt).putLong(instance.toLong).array()
  }
  private[fixed] implicit object ISIDSize extends HasSize[ISID]{
    def size = UByte(3)
    def bytes(instance: ISID): Array[Byte] = instance.bytes
  }
  private[fixed] implicit object MacSize extends HasSize[MacAddress]{
    def size = UByte(6)
    def bytes(instance: MacAddress): Array[Byte] = instance.bytes
  }
  private[fixed] implicit object IPv4Size extends HasSize[IPv4Address]{
    spire.syntax.signed
    def size = UByte(4)
    def bytes(instance: IPv4Address): Array[Byte] = instance.bytes
  }
  private[fixed] implicit object IPv6Size extends HasSize[IPv6Address]{
    def size = UByte(16)
    def bytes(instance: IPv6Address): Array[Byte] = instance.bytes
  }

  private[fixed] implicit object ULongFromLong  extends PrimitiveFromLong[ULong]  { def fromLong(l: Long) = ULong(l) }
  private[fixed] implicit object UIntFromLong   extends PrimitiveFromLong[UInt]   { def fromLong(l: Long) = UInt(l.toInt) }
  private[fixed] implicit object UShortFromLong extends PrimitiveFromLong[UShort] { def fromLong(l: Long) = UShort(l.toShort) }
  private[fixed] implicit object UByteFromLong  extends PrimitiveFromLong[UByte]  { def fromLong(l: Long) = UByte(l.toByte) }
  private[fixed] implicit object CharFromLong   extends PrimitiveFromLong[Char]   { def fromLong(l: Long) = l.toChar }

  private[fixed] implicit class ByteBufferExt(bb: ByteBuffer){
    def getUByte = UByte(bb.get())
    def getUByte(index: Int) = UInt(bb.get(index))

    def getUShort = UShort(bb.getShort)
    def getUShort(index: Int) = UInt(bb.getShort(index))

    def getUInt = UInt(bb.getInt)
    def getUInt(index: Int) = UInt(bb.getInt(index))

    def getULong = ULong(bb.getLong)
    def getULong(index: Int) = UInt(bb.getLong(index))

    def getMacAddress = {
      val bytes = Array.ofDim[Byte](6)
      bb.get(bytes)
      MacAddress(bytes)
    }

    def getISID = {
      val bytes = Array.ofDim[Byte](3)
      bb.get(bytes)
      ISID(bytes)
    }

    def getIPv4Address = {
      val bytes = Array.ofDim[Byte](4)
      bb.get(bytes)
      IPv4Address(bytes)
    }

    def getIPv6Address = {
      val bytes = Array.ofDim[Byte](16)
      bb.get(bytes)
      IPv6Address(bytes)
    }
  }

  private[fixed] def firstGenericParameter(c: Class[_]): Option[Class[_]] = scala.util.Try {
    c.getGenericSuperclass
      .asInstanceOf[java.lang.reflect.ParameterizedType]
      .getActualTypeArguments
      .head match {
        case pt: ParameterizedTypeImpl => pt.getRawType
        case c: Class[_] => c
      }
    }.toOption
}

package util{

  import java.nio.{ByteOrder, ByteBuffer}
  import java.net.{Inet4Address, Inet6Address, InetAddress}
  import scala.util.Try

  // Mix this trait in to convert product of booleans to ULong
  private[fixed] trait Bitmap {
    p: Product =>
    def bits: IndexedSeq[Int] = 0 to (p.productArity - 1)
    def bitmap = ULong(productIterator.zipWithIndex map { case (v: Boolean, idx) => if (v) 1L << bits(idx) else 0L } reduce { _ | _ })
  }

  private[fixed] object StructureName {
    def unapply(c: Class[_]): Option[String] = Some(c.getSimpleName.stripSuffix("Input"))
  }

  private[fixed] trait HasSize[T]{
    def size: UByte
    def bytes(instance: T): Array[Byte]
  }

  private[fixed] trait PrimitiveFromLong[T]{
    def fromLong(l: Long): T
  }

  private[fixed] abstract class ULongFitData[T : HasSize] protected (initialData: Either[Array[Byte], ULong]){
    protected[util] def length = implicitly[HasSize[T]].size
    protected[util] def mask: ULong = (length.toShort until 8).foldLeft(ULong(0)) { case (acc, pos) => acc | ULong(0xFF) << (8 * pos) }

    def bytes: Array[Byte] = initialData match {
      case Left(data) => data
      case Right(long) => ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putLong(long.signed).array().drop(8 - length.toShort)
    }
    def toLong: ULong = initialData match {
      case Left(data) => ULong(ByteBuffer.wrap(Seq.fill[Byte](8 - length.toShort)(0) ++: data).getLong)
      case Right(long) => long
    }

    require((toLong & mask) == ULong(0))
  }

  sealed class ISID private (initialData: Either[Array[Byte], ULong]) extends ULongFitData[ISID](initialData)

  object ISID{
    def apply(bytes: Array[Byte]) = new ISID(Left(bytes))
    def apply(value: UInt) = new ISID(Right(ULong(value.toLong)))
  }

  sealed class MacAddress private (initialData: Either[Array[Byte], ULong]) extends ULongFitData[MacAddress](initialData){
    override def toString = bytes.map(x => f"$x%02x").mkString(":")
  }

  object MacAddress{
    def apply(bytes: Array[Byte]) = new MacAddress(Left(bytes))
    def apply(value: ULong) = new MacAddress(Right(value))

    def parse(s: String): Option[MacAddress] = Try { MacAddress(s.split(':').map(_.toByte)) } toOption
  }

  sealed class IPv4Address private (initialData: Either[Array[Byte], ULong]) extends ULongFitData[IPv4Address](initialData){
    override def toString = InetAddress.getByAddress(bytes).toString.substring(1)
  }

  object IPv4Address{
    def apply(bytes: Array[Byte]) = new IPv4Address(Left(bytes))
    def apply(value: UInt) = new IPv4Address(Right(ULong(value.toLong)))

    def parse(s: String): Option[IPv4Address] =
      Try { IPv4Address(InetAddress.getByName(s).asInstanceOf[Inet4Address].getAddress) } toOption
  }

  sealed case class IPv6Address(bytes: Array[Byte]){
    require(bytes.length == 16, "IPv6 Address should consists of exactly 16 bytes.")
    override def toString = InetAddress.getByAddress(bytes).toString.substring(1)
  }

  object IPv6Address{
    def parse(s: String): Option[IPv6Address] =
      Try { IPv6Address(InetAddress.getByName(s).asInstanceOf[Inet6Address].getAddress) } toOption
  }
}