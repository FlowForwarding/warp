/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import com.typesafe.scalalogging.StrictLogging

import scala.util.{Failure, Try}
import scala.reflect.runtime.universe._

import org.apache.avro.io.EncoderFactory

import spire.math.{UInt, ULong, UByte}

import com.gensler.scalavro.util.{U16, U32, Union}
import com.gensler.scalavro.util.Union._
import com.gensler.scalavro.io.complex.AvroBareUnionIO
import com.gensler.scalavro.types.supply.{UInt16, UInt32, UInt64}

import org.flowforwarding.warp.sdriver.dynamic.{DriverWithReflectionSupport, ReflectiveStructure}
import org.flowforwarding.warp.driver_api.{MessageType, MessageDriverFactory, OFMessage}

case class OfpMsg[U <: Union.not[_], T: TypeTag: prove [U]#containsType](structure: T) extends OFMessage

trait UnionMemberTransform[U <: Union.not[_], R]{
  def apply[T: TypeTag: prove [U]#containsType](m: T): R
}

abstract class StaticDriver[U <: Union.not[_]: TypeTag, SelfType <: StaticDriver[U, SelfType]] extends DriverWithReflectionSupport[OfpMsg[U, _]]
                                                                                               with MessageDriverFactory[OfpMsg[U, _]]
                                                                                               with StrictLogging {
  protected val msgTypeIO: AvroBareUnionIO[U, U]
  protected val getDPIDField: Any => UInt64
  protected val getXidField: Any => UInt32
  protected val getIncomingMessageType: Any => MessageType

  protected def transformValue[R](transform: UnionMemberTransform[U, R])(value: Any): Try[R]

  private val mkMsg = transformValue { new UnionMemberTransform[U, OfpMsg[U, _]]{
    def apply[T: TypeTag: prove [U]#containsType](m: T): OfpMsg[U, _] = OfpMsg[U, T](m)
  }} _

  private val encodeMsg = transformValue { new UnionMemberTransform[U, Try[Array[Byte]]]{
    def apply[T: TypeTag: prove [U]#containsType](m: T) = Try(encodeUnionMember(m))
  }} _

  private val getDPID =  transformValue { new UnionMemberTransform[U, ULong]{
    def apply[T: TypeTag: prove [U]#containsType](m: T) = ULong(UInt64.toLong(getDPIDField(m)))
  }} _

  private val getXID =  transformValue { new UnionMemberTransform[U, UInt]{
    def apply[T: TypeTag: prove [U]#containsType](m: T) = UInt(UInt32.toInt(getXidField(m)))
  }} _

  private val incomingMessageType = transformValue { new UnionMemberTransform[U, MessageType]{
    def apply[T: TypeTag: prove [U]#containsType](m: T) = getIncomingMessageType(m)
  }} _

  protected def encodeUnionMember[T: TypeTag: prove [U]#containsType](msg: T) = {
    val ostream = new ByteArrayOutputStream
    val encoder = EncoderFactory.get.directBinaryEncoder(ostream, null)
    msgTypeIO.writeBare(msg, encoder, scala.collection.mutable.Map[Any, Long](), true)
    val res = versionCode.toByte +: ostream.toByteArray // add version
    logger.debug("Encode " + msg)
    logger.debug(s"Result (${res.length} bytes): ${res.mkString("[", ", ", "]")}")
    res
  }

  protected def decodeUnionMember(in: Array[Byte]): (Try[U], Array[Byte]) = {
    if(in.length < 4)
      (Failure(new ArrayIndexOutOfBoundsException()), in)
    else {
      val firstMemberLength = U16.f(UInt16.toShort(UInt16(in(2), in(3))))
      if(firstMemberLength <= in.length){
        val (firstMessageData, rest) = in.splitAt(firstMemberLength)
        val istream = new ByteArrayInputStream(firstMessageData)
        istream.skip(1) //skip version
        val res = msgTypeIO read istream
        logger.debug("Decode " + firstMessageData.mkString("[", ", ", "]"))
        logger.debug("Result: " + res.toString)
        (res, rest)
      }
      else (Failure(new ArrayIndexOutOfBoundsException()), in)
    }
  }

  def dynamic2Static(msg: ReflectiveStructure): Try[OfpMsg[U, _]] = mkMsg(msg.underlying)

  def getDPID(in: Array[Byte]): Try[ULong] = decodeMessage(in)._1 flatMap (msg => getDPID(msg.structure))

  def getXid(msg: OfpMsg[U, _]): UInt = getXID(msg.structure).get

  def getIncomingMessageType(msg: OfpMsg[U, _]): MessageType = incomingMessageType(msg.structure).get

  def decodeMessage(in: Array[Byte]): (Try[OfpMsg[U, _]], Array[Byte]) = {
    val (message, rest) = decodeUnionMember(in)
    (message flatMap mkMsg, rest)
  }

  def encodeMessage(in: OfpMsg[U, _]): Try[Array[Byte]] = encodeMsg(in.structure).flatten

  def get(version: UByte): this.type = {
    if(versionCode == version) this
    else throw new IllegalArgumentException("Unsupported version.")
  }

  def supportedVersions: Array[UByte] = Array(versionCode)
}