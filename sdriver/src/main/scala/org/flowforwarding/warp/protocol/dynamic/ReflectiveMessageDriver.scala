/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.dynamic

import scala.util.{Failure, Success, Try}

import com.typesafe.config.{ConfigFactory, Config}

import org.flowforwarding.warp.protocol.OfpMsg
import org.flowforwarding.warp.controller.api.fixed.v13.Ofp13DriverApi
import org.flowforwarding.warp.controller.api.dynamic.{DynamicDriver, DynamicBuilderInput}
import spire.math.{ULong, UByte}

class ReflectiveMessageDriver(underlyingDriver: DriverWithReflectionSupport[_ <: OfpMsg[_, _]],
                              classes: Iterable[Class[_]]) extends DynamicDriver[ReflectiveStructure]{

  private val buildersCache = scala.collection.mutable.Map[String, Try[ReflectiveStructureBuilder]]()

  private def getBuilder(msgType: String): Try[ReflectiveStructureBuilder] = {
    if(!buildersCache.contains(msgType)){
      buildersCache(msgType) = Try { new ReflectiveStructureBuilder(classes.find(_.getSimpleName == msgType).get, this) }
    }
    buildersCache(msgType)
  }

  def getDPID(in: Array[Byte]): Try[ULong] = underlyingDriver.getDPID(in)

  def getXid(msg: ReflectiveStructure) = getXidResolvingExistential(underlyingDriver, msg).get

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

  def getHelloMessage(supportedVersions: Array[UByte]): Array[Byte] = underlyingDriver.getHelloMessage(supportedVersions)

  def rejectVersionError(reason: String): Array[Byte] = underlyingDriver.rejectVersionError(reason)

  def getFeaturesRequest: Array[Byte] =  underlyingDriver.getFeaturesRequest

  override def build(input: DynamicBuilderInput): Try[ReflectiveStructure] =
    getBuilder(input.structureName) flatMap { _.build(input) }
}

object ReflectiveMessageDriver{

  def apply(underlyingDriver: DriverWithReflectionSupport[_ <: OfpMsg[_, _]], classes: Iterable[Class[_]]) = {
    underlyingDriver.versionCode.toShort match {
      case 4 => new ReflectiveMessageDriver(underlyingDriver, classes) with Ofp13DriverApi{
        val namesConfig: Config = ConfigFactory.load("reference").getConfig("ofp13")
      }
      case _ => new ReflectiveMessageDriver(underlyingDriver, classes)
    }
  }
}
