/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.dynamic

import org.flowforwarding.warp.driver_api.MessageType

import scala.util.{Failure, Success, Try}

import org.flowforwarding.warp.sdriver.OfpMsg
//import org.flowforwarding.warp.driver_api.fixed.v13.Ofp13DriverApi // TODO: remove this dependency !!!
import org.flowforwarding.warp.driver_api.dynamic.{DynamicDriver, DynamicBuilderInput}
import spire.math.{ULong, UByte}

case class MatchingClassNotFoundException(structureName: String) extends Exception(s"Class matching structure of type $structureName not found.")

class ReflectiveMessageDriver(underlyingDriver: DriverWithReflectionSupport[_ <: OfpMsg[_, _]],
                              classes: Iterable[Class[_]]) extends DynamicDriver[ReflectiveStructure]{

  private val buildersCache = scala.collection.mutable.Map[String, Try[ReflectiveStructureBuilder]]()

  private def getBuilder(msgType: String): Try[ReflectiveStructureBuilder] = {
    if(!buildersCache.contains(msgType)){
      buildersCache(msgType) = classes.find(_.getSimpleName == msgType) match {
        case Some(c) => Success(new ReflectiveStructureBuilder(c, this))
        case None => Failure(MatchingClassNotFoundException(msgType))
      }
    }
    buildersCache(msgType)
  }

  def getDPID(in: Array[Byte]): Try[ULong] = underlyingDriver.getDPID(in)

  def getXid(msg: ReflectiveStructure) = getXidResolvingExistential(underlyingDriver, msg).get

  def getIncomingMessageType(msg: ReflectiveStructure): MessageType = getIncomingMessageTypeResolvingExistential(underlyingDriver, msg).get

  val versionCode: UByte = underlyingDriver.versionCode

  def decodeMessage(in: Array[Byte]): (Try[ReflectiveStructure], Array[Byte]) = {
    val (message, rest) = underlyingDriver.decodeMessage(in)
    (message flatMap { m => Try { new ReflectiveStructure(m.structure) }}, rest)
  }

  def encodeMessage(msg: ReflectiveStructure): Try[Array[Byte]] =
    encodeResolvingExistential(underlyingDriver, msg)

  private def encodeResolvingExistential[T <: OfpMsg[_, _]](u: DriverWithReflectionSupport[T], msg: ReflectiveStructure) =
    u.dynamic2Static(msg).flatMap(u.encodeMessage)

  private def getXidResolvingExistential[T <: OfpMsg[_, _]](u: DriverWithReflectionSupport[T], msg: ReflectiveStructure) =
    u.dynamic2Static(msg).map(u.getXid)

  private def getIncomingMessageTypeResolvingExistential[T <: OfpMsg[_, _]](u: DriverWithReflectionSupport[T], msg: ReflectiveStructure) =
    u.dynamic2Static(msg).map(u.getIncomingMessageType)

  def getHelloMessage(supportedVersions: Array[UByte]): Array[Byte] = underlyingDriver.getHelloMessage(supportedVersions)

  def rejectVersionError(reason: String): Array[Byte] = underlyingDriver.rejectVersionError(reason)

  def getFeaturesRequest: Array[Byte] =  underlyingDriver.getFeaturesRequest

  override def build(input: DynamicBuilderInput): Try[ReflectiveStructure] =
    getBuilder(input.structureName) flatMap { _.build(input) }
}

