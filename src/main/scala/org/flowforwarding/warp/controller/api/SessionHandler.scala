package org.flowforwarding.warp.controller.api

import scala.util.{Failure, Success, Try}
import scala.collection.JavaConversions

import org.flowforwarding.warp.controller.api.fixed.SpecificVersionMessageHandler
import org.flowforwarding.warp.controller.api.dynamic.{DynamicMessageHandler, DynamicStructure, DynamicDriver}
import org.flowforwarding.warp.controller.session.{MessageDriver, SessionHandlerRef, MessageDriverFactory, LowLevelSessionHandler}

class NoSuitableMessageHandlerException extends Exception

private[api] class SessionHandler[DriverType <: DynamicDriver[_, StructureType],
                                  StructureType <: DynamicStructure[StructureType]]
   (driversFactory:         MessageDriverFactory[StructureType, DriverType],
    dynamicMessageHandlers: scala.collection.Set[DynamicMessageHandler[DriverType, StructureType]],
    versionHandlers:        scala.collection.Set[SpecificVersionMessageHandler[_, StructureType]]) extends LowLevelSessionHandler[StructureType, DriverType](driversFactory) {

  def definedHandlersVersions: Array[Short] = (versionHandlers.map(_.versionCode) ++ dynamicMessageHandlers.map(_.supportedVersions).flatten).toArray

  protected def onReceivedMessage(driver: DriverType, dpid: Long, msg: StructureType): Seq[StructureType] = {
    val tryHandle = versionHandlers
                      .collectFirst { case h => h.handle(driver, dpid, msg) }
                      .flatten
                      .getOrElse { Try { dynamicMessageHandlers
                                          .collectFirst { case h if h.supportsVersion(driver.versionCode) => h.onDynamicMessage(driver, dpid, msg) }
                                          .getOrElse { throw new NoSuitableMessageHandlerException }}}

    tryHandle match {
      case Success(result) => result
      case Failure(e) => e.printStackTrace(); Seq()
    }
  }
}

object SessionHandler{
  def makeRef[DriverType <: DynamicDriver[_, StructureType],
             StructureType <: DynamicStructure[StructureType]]
      (dynamicMessageHandler:   scala.collection.Set[DynamicMessageHandler[DriverType, StructureType]] = Set.empty[DynamicMessageHandler[DriverType, StructureType]],
       versionHandlers:         scala.collection.Set[SpecificVersionMessageHandler[_, StructureType]]  = Set.empty[SpecificVersionMessageHandler[_, StructureType]] )
      (implicit driversFactory: MessageDriverFactory[StructureType, DriverType]): SessionHandlerRef = {

    new SessionHandlerRef(classOf[SessionHandler[DriverType, StructureType]], driversFactory, dynamicMessageHandler, versionHandlers)
  }

  def makeRef[DriverType <: DynamicDriver[_, StructureType],
              StructureType <: DynamicStructure[StructureType]]
    (driversFactory:         MessageDriverFactory[StructureType, DriverType],
     dynamicMessageHandlers: java.util.Set[DynamicMessageHandler[DriverType, StructureType]],
     versionHandlers:        java.util.Set[SpecificVersionMessageHandler[_, StructureType]]): SessionHandlerRef = {

    import JavaConversions.asScalaSet

    val x: scala.collection.Set[DynamicMessageHandler[DriverType, StructureType]] =
      asScalaSet[DynamicMessageHandler[DriverType, StructureType]](dynamicMessageHandlers)

    val y: scala.collection.Set[SpecificVersionMessageHandler[_, StructureType]] =
      asScalaSet[SpecificVersionMessageHandler[_, StructureType]](versionHandlers)

    makeRef(x, y)(driversFactory)
  }
}



