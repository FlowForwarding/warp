package org.flowforwarding.warp.controller.session

import scala.util.Try

trait OFMessage // Marker trait

trait MessageDriver[T <: OFMessage]{
  def getDPID(in: Array[Byte]): Try[Long]
  def decodeMessage(in: Array[Byte]): Try[T]
  def encodeMessage(msg: T): Try[Array[Byte]]
  val versionCode: Short

  def getHelloMessage(supportedVersions: Array[Short]): Array[Byte]
  def rejectVersionError(reason: String): Array[Byte]
  def suitesSwitch(hello: Array[Byte]): Boolean = (hello(0).toShort & 0xff).toShort == versionCode // implementation depends on ofp version
  def getFeaturesRequest: Array[Byte]
}

trait MessageDriverFactory[T <: OFMessage, +DriverType <: MessageDriver[T]]{
  def get(versionCode: Short): DriverType
  def supportedVersions: Array[Short]
  // accepted version or rejected versions + error data
  def highestCommonVersion(helloMsg: Array[Byte]): Either[Short, (Array[Short], Array[Byte])] = {
    supportedVersions.map(get)
                     .sortBy(_.versionCode)
                     .reverse
                     .collectFirst { case d if d.suitesSwitch(helloMsg) => d.versionCode } match {
      case Some(version) => Left(version)
      case None => Right(Array((helloMsg(0).toShort & 0xff).toShort), get(supportedVersions.head).rejectVersionError("No message driver."))  // TODO: improve implementation
    }
  }
}