/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed

import java.lang.reflect.Method

import scala.util.{Failure, Try}
import scala.reflect.ClassTag

import spire.math._

import org.flowforwarding.warp.controller.bus.{ControllerBus, MessageEnvelope}
import org.flowforwarding.warp.controller.api.dynamic.{DynamicStructureBuilder, DynamicStructure}
import org.flowforwarding.warp.controller.driver_interface.{MessageDriver, MessageDriverFactory, MessageHandlers}
import org.flowforwarding.warp.controller.SwitchConnector.SwitchIncomingMessage

trait IncomingMessagePredicate { def test(dpid: ULong, payload: Any): java.lang.Boolean }

abstract class SpecificVersionMessageHandlers[Self <: SpecificVersionMessageHandlers[_, _],
                                              D <: MessagesDescriptionHelper[Self]: ClassTag](controllerBus: ControllerBus, versionCode: UByte)
  extends MessageHandlers[DynamicStructure, DynamicStructureBuilder[_]](controllerBus){

  private def castDescription(driver: MessageDriver[_]): Try[D] = Try { implicitly[ClassTag[D]].runtimeClass.asInstanceOf[Class[D]].cast(driver) }

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

  val supportedVersions = Array(versionCode)

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