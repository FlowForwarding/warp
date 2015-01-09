/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.text_view

import scala.reflect.ClassTag

import org.flowforwarding.warp.driver_api.fixed.util._
import spire.math.{UByte, ULong, UInt, UShort}
import org.flowforwarding.warp.driver_api.fixed.{StructuresDescriptionHelper, ConcreteStructureBuilder, BuilderInput}

sealed trait BITextViewItem
case class Str(s: String) extends BITextViewItem
case class Num(n: BigInt) extends BITextViewItem
case class BITextView(structureName: String, data: Map[String, BITextViewItem]) extends BITextViewItem
case class BITextViewItems[I <: BITextViewItem](is: Seq[I]) extends BITextViewItem

/* Grants a builder ability to build structures from text representations.
   Provides convenient way of text representation parsing via set of implicit conversions.
 */
private[fixed] trait BITextViewSupport[Input <: BuilderInput] {
  builder: ConcreteStructureBuilder[Input] =>

  private implicit class BITextViewExt(m: BITextView){
    val BITextView(_, data) = m
    def string(field: String) = data get field collect { case Str(s) => s }
    def byte(field: String)   = data get field collect { case Num(b) => UByte(b.toByte) }
    def short(field: String)  = data get field collect { case Num(b) => UShort(b.toInt) }
    def int(field: String)    = data get field collect { case Num(b) => UInt(b.toInt) }
    def long(field: String)   = data get field collect { case Num(b) => ULong.fromBigInt(b) }

    def enumIndex(field: String) = data get field collect { case Num(b) => b.toInt }

    def isid(field: String) = int(field) map ISID.apply
    def bool(field: String) = string(field) map { _.toBoolean }
    def mac(field: String)  = string(field) flatMap MacAddress.parse
    def ipv4(field: String) = string(field) flatMap IPv4Address.parse
    def ipv6(field: String) = string(field) flatMap IPv6Address.parse

    def bitmapOpt[B <: Bitmap: ClassTag](field: String) = long(field) flatMap { _.toBitmap[B] }

    def bytes(field: String)  = data get field collect { case BITextViewItems(ns) => ns map { case Num(b) => b.toByte } }
    def shorts(field: String) = data get field collect { case BITextViewItems(ns) => ns map { case Num(b) => UShort(b.toInt) } }
    def ints(field: String)   = data get field collect { case BITextViewItems(ns) => ns map { case Num(b) => UInt(b.toInt) } }
    def longs(field: String)  = data get field collect { case BITextViewItems(ns) => ns map { case Num(b) => ULong.fromBigInt(b) } }

    private def buildFixed[T <: BuilderInput: ClassTag](input: BITextView): T =
      implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]].cast(api.parseTextView(input).get)

    def structure[T <: BuilderInput: ClassTag](field: String): Option[T] =
      data get field collect { case v: BITextView => buildFixed[T](v) }

    def structures[T <: BuilderInput: ClassTag](field: String): Option[Seq[T]] =
      data get field collect { case BITextViewItems(is) => is map { case v: BITextView => buildFixed[T](v) } }
  }

  final protected implicit def string(field: String)(implicit m: BITextView) = m.string(field).get
  final protected implicit def bool(field: String)(implicit m: BITextView)   = m.bool(field).get
  final protected implicit def byte(field: String)(implicit m: BITextView)   = m.byte(field).get
  final protected implicit def short(field: String)(implicit m: BITextView)  = m.short(field).get
  final protected implicit def int(field: String)(implicit m: BITextView)    = m.int(field).get
  final protected implicit def long(field: String)(implicit m: BITextView)   = m.long(field).get

  final protected implicit def enumIndex(field: String)(implicit m: BITextView) = m.enumIndex(field).get

  final protected implicit def isid(field: String)(implicit m: BITextView) = m.isid(field).get
  final protected implicit def mac(field: String)(implicit m: BITextView)  = m.mac(field).get
  final protected implicit def ipv4(field: String)(implicit m: BITextView) = m.ipv4(field).get
  final protected implicit def ipv6(field: String)(implicit m: BITextView) = m.ipv6(field).get

  final protected implicit def boolOpt(field: String)(implicit m: BITextView)  = m.bool(field)
  final protected implicit def byteOpt(field: String)(implicit m: BITextView)  = m.byte(field)
  final protected implicit def shortOpt(field: String)(implicit m: BITextView) = m.short(field)
  final protected implicit def intOpt(field: String)(implicit m: BITextView)   = m.int(field)
  final protected implicit def longOpt(field: String)(implicit m: BITextView)  = m.long(field)

  final protected implicit def isidOpt(field: String)(implicit m: BITextView) = m.isid(field)
  final protected implicit def macOpt(field: String)(implicit m: BITextView)  = m.mac(field)
  final protected implicit def ipv4Opt(field: String)(implicit m: BITextView) = m.ipv4(field)
  final protected implicit def ipv6Opt(field: String)(implicit m: BITextView) = m.ipv6(field)

  // TODO: try to make implicits
  final protected def bitmap[B <: Bitmap](field: String)(implicit m: BITextView, c: ClassTag[B]): B = m.bitmapOpt[B](field)(c).get

  final protected def structure[T <: BuilderInput](field: String)(implicit m: BITextView, ev: T <:< BuilderInput, c: ClassTag[T]): T = m.structure[T](field).get

  final protected implicit def structures[T <: BuilderInput](field: String)(implicit m: BITextView, ev: T <:< BuilderInput, c: ClassTag[T]): Array[T] =
    m.structures[T](field).map(_.toArray).get

  final protected implicit def bytes(field: String)(implicit m: BITextView)  = m.bytes(field).map(_.toArray).get
  final protected implicit def shorts(field: String)(implicit m: BITextView) = m.shorts(field).map(_.toArray).get
  final protected implicit def ints(field: String)(implicit m: BITextView)   = m.ints(field).map(_.toArray).get
  final protected implicit def longs(field: String)(implicit m: BITextView)  = m.longs(field).map(_.toArray).get

}

private[fixed] object StructureTextView{
  def reflect(structureInstance: Any, structureTrait: Class[_]): BITextView = {
    val name = structureTrait.getSimpleName

    val members: Map[String, BITextViewItem] =
      structureTrait.getMethods.collect {
        case m if m.getParameterTypes.length == 0 =>

          val getString: PartialFunction[Any, String]  = {
            case obj @ (_: String | _: MacAddress | _: IPv4Address | _: IPv6Address | _: ISID | true | false) => obj.toString
          }

          val getNumber: PartialFunction[Any, BigInt] = {
            case n: Byte   => BigInt(n)
            case n: UByte  => BigInt(n.signed)
            case n: Char   => BigInt(n)
            case n: UShort => BigInt(n.signed)
            case n: Int    => BigInt(n)
            case n: UInt   => BigInt(n.signed)
            case n: Long   => BigInt(n)
            case n: ULong  => BigInt(n.signed)
            case e: Enumeration#Value => BigInt(e.id)
          }

          val getStructure: PartialFunction[Any, BITextView] = {
            case s: StructureWithTextView => s.textView
          }

          val getArray: PartialFunction[Any, BITextViewItems[_]] = {
            case array: Array[_] =>
              val textViews = array collect getStructure
              val nums      = array collect getNumber map Num
              val strings   = array collect getString map Str
              if(textViews.length == array.length)
                BITextViewItems(textViews.toList)
              else if(nums.length == array.length)
                BITextViewItems(nums.toList)
              else //if(strings.length == array.length)
                BITextViewItems(strings.toList)
          }

          val getTextView = (getString andThen Str) orElse
                            (getNumber andThen Num) orElse
                             getStructure orElse
                             getArray

          (m.getName, getTextView(m.invoke(structureInstance)))
      }.toMap

    BITextView(name, members)
  }
}

private[fixed] trait StructureWithTextView{
  def textView: BITextView
}

