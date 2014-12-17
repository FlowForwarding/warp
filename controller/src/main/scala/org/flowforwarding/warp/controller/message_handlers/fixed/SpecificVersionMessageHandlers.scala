/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.message_handlers.fixed

import java.lang.reflect.Method


import scala.reflect.ClassTag
import scala.util.{Failure, Try}

import spire.math._

import org.flowforwarding.warp.controller.message_handlers.MessageHandlers
import org.flowforwarding.warp.controller.SwitchConnector.SwitchIncomingMessage
import org.flowforwarding.warp.controller.bus.{ControllerBus, MessageEnvelope}

import org.flowforwarding.warp.driver_api.{MessageDriverFactory, MessageDriver}
import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.dynamic.{DynamicStructure, DynamicStructureBuilder}

trait IncomingMessagePredicate { def test(dpid: ULong, payload: Any): java.lang.Boolean }

abstract class SpecificVersionMessageHandlers[HandlersSer <: SpecificVersionMessageHandlersSet[_, _],
                                              Desc <: MessagesDescriptionHelper[HandlersSer]: ClassTag](controllerBus: ControllerBus)
  extends MessageHandlers[DynamicStructure, DynamicStructureBuilder[_]](controllerBus)
{
  handlers: SpecificVersionMessageHandlersSet[HandlersSer, Desc] =>

  private def castDescription(driver: MessageDriver[_]): Try[Desc] = Try { implicitly[ClassTag[Desc]].runtimeClass.asInstanceOf[Class[Desc]].cast(driver) }

  override def compatibleWith(factory: MessageDriverFactory[_]): Boolean = {
    super.compatibleWith(factory) && castDescription(factory.get(versionCode)).isSuccess
  }

  private val handlersCache = scala.collection.mutable.Map[Class[_], Option[Method]]()

  private def canHandleClass(c: Class[_])(m: Method)  = {
    val L = classOf[Long]
    m.getParameterTypes match {
      case Array(L, handlingClass) => handlingClass.isAssignableFrom(c) && m.getReturnType == classOf[Array[BuilderInput]]
      case _ => false
    }
  }

  private def handle(dpid: ULong, message: FixedOfpMessage): Try[Array[FixedMessageInput]] = {
    val msgClass = message.getClass
    if(!handlersCache.contains(msgClass)) {
      val lookupRes = this.getClass.getMethods find canHandleClass(msgClass)
      lookupRes foreach { _.setAccessible(true) }
      handlersCache(msgClass) = lookupRes
    }
    handlersCache(msgClass) match {
      case Some(m) => Try {
        m.invoke(this, Long.box(dpid.signed), message).asInstanceOf[Array[FixedMessageInput]]
      }
      case None => Failure {
        new RuntimeException(s"Unable to handle message of type $msgClass: no appropriate method found.")
      }
    }
  }

  def testIncomingMessage(predicate: IncomingMessagePredicate): PartialFunction[MessageEnvelope, Boolean] = {
    case SwitchIncomingMessage(id, driver: MessagesDescriptionHelper[_], msg: DynamicStructure) if driver.versionCode == this.versionCode =>
      driver.toConcreteMessage(msg) map { m => predicate.test(id, m).booleanValue() } getOrElse false
  }

  def supportedVersions = Array(handlers.versionCode)

  override def handleMessage(driver: DynamicStructureBuilder[_], dpid: ULong, msg: DynamicStructure): Try[Array[DynamicStructure]] = {
      driver match {
        case api: MessagesDescriptionHelper[_] if api.versionCode == this.versionCode =>
          for{m <- api.toConcreteMessage(msg)
              inputs <- handle(dpid, m)
          } yield { inputs map { m => api.buildDynamic(m).get } }
        case _ => Failure(new RuntimeException("The instance of DynamicBuildersProvider is not MessagesDescriptionHelper or has a wrong version."))
      }
    }
}