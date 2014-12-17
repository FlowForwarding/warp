/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api

import scala.util.Try
import spire.math.{UByte, UInt, ULong}

trait OFMessage // Marker trait

trait OfpVersionSupport{
  val versionCode: UByte
}

trait OfpHandshakeSupport extends OfpVersionSupport{
  def suitesSwitch(hello: Array[Byte]): Boolean = UByte(hello(0)) == versionCode // implementation depends on ofp version

  def getHelloMessage(supportedVersions: Array[UByte]): Array[Byte]
  def rejectVersionError(reason: String): Array[Byte]
  def getFeaturesRequest: Array[Byte]
}

trait MessageType
case object Async extends MessageType
case object Error extends MessageType
case object Request extends MessageType
case object Command extends MessageType
case object SingleMessageResponse extends MessageType
case class MultipartResponse(reqMore: Boolean) extends MessageType

trait OfpFeaturesExtractor[-T <: OFMessage] extends OfpVersionSupport{
  def getDPID(in: Array[Byte]): Try[ULong]
  def getXid(msg: T): UInt
  def getIncomingMessageType(msg: T): MessageType
}

trait OfpMessageEncoder[-T <: OFMessage] extends OfpVersionSupport{
  def encodeMessage(msg: T): Try[Array[Byte]]
}

trait OfpMessageDecoder[+T <: OFMessage] extends OfpVersionSupport{
  def decodeMessage(in: Array[Byte]): (Try[T], Array[Byte])
}

trait MessageDriver[T <: OFMessage] extends OfpHandshakeSupport with OfpFeaturesExtractor[T] with OfpMessageEncoder[T] with OfpMessageDecoder[T]

import spire.algebra.Order._

trait MessageDriverFactory[T <: OFMessage]{
  def get(versionCode: UByte): MessageDriver[T]
  def supportedVersions: Array[UByte]
  // accepted version or rejected versions + error data
  def highestCommonVersion(helloMsg: Array[Byte]): Either[UByte, (Array[UByte], Array[Byte])] = {
    supportedVersions.map(get)
                     .sortBy(_.versionCode)
                     .reverse
                     .collectFirst { case d if d.suitesSwitch(helloMsg) => d.versionCode } match {
      case Some(version) => Left(version)
      case None => Right(Array(UByte(helloMsg(0))),
                         get(supportedVersions.head).rejectVersionError("No message driver."))  // TODO: improve implementation
    }
  }
}

