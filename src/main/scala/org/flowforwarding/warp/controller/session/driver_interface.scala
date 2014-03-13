package org.flowforwarding.warp.controller.session

import scala.util.Try

trait OFMessage // Marker trait

trait MessageDriver[T <: OFMessage]{
  def getDPID(in: Array[Byte]): Try[Long]
  def decodeMessage(in: Array[Byte]): Try[T]
  def encodeMessage(dict: T): Try[Array[Byte]]
  val versionCode: Short
}

trait MessageDriverFactory[T <: OFMessage]{
  def get(versionCode: Short): Option[MessageDriver[T]]

  def getVersion(msg: Array[Byte]): Option[Short] = Try((msg(0).toShort & 0xff).toShort).toOption
  def get(msg: Array[Byte]): Option[MessageDriver[T]] = getVersion(msg).flatMap(get)
  //def get(version: String): MessageDriver[T]
}