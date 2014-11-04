/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.driver_interface

import org.flowforwarding.warp.controller.SwitchConnector.{SwitchDisconnected, SendToSwitch, SwitchIncomingMessage, SwitchHandshake}
import org.flowforwarding.warp.controller.bus.{MessageEnvelope, ServiceBusActor, ControllerBus}
import org.flowforwarding.warp.controller.modules.MessageConsumer
import spire.math.{ULong, UByte}

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

abstract class MessageHandlers[T <: OFMessage, ApiSupport <: OfpVersionSupport](protected val bus: ControllerBus)(implicit protected val mc: ClassTag[T], dc: ClassTag[ApiSupport])
  extends MessageConsumer with ServiceBusActor{
  def supportedVersions: Array[UByte]

  def handleMessage(api: ApiSupport, dpid: ULong, msg: T): Try[Array[T]]
  def handleDisconnected(api: ApiSupport, dpid: ULong): Unit = { } // check the signature
  def handleHandshake(api: ApiSupport, dpid: ULong): Unit = { } // check the signature

  private def castDriver(driver: MessageDriver[_]): Try[ApiSupport] = Try { dc.runtimeClass.asInstanceOf[Class[ApiSupport]].cast(driver) }

  private def castMessage(message: OFMessage): Try[T] = Try { mc.runtimeClass.asInstanceOf[Class[T]].cast(message) }

  override def compatibleWith(factory: MessageDriverFactory[_]) =
    this.supportedVersions.forall( { factory.get _ } andThen castDriver andThen { _.isSuccess })

  //castDriver((this.supportedVersions.head)).isSuccess

  final override def handleEvent(event: MessageEnvelope): Unit = event match {
    case SwitchHandshake(dpid, driver: MessageDriver[_]) =>
      castDriver(driver) foreach { d => handleHandshake(d, dpid) }
    case SwitchIncomingMessage(dpid, driver: MessageDriver[_], message: OFMessage) =>
      for{castedDriver <- castDriver(driver)
          if supportedVersions contains castedDriver.versionCode
          castedMessage <- castMessage(message)} {
        val response = handleMessage(castedDriver, dpid, castedMessage)
        response match {
          case Success(messages) => //println("MESSAGES                " + messages.toList);
            messages foreach { m => askFirst(SendToSwitch(dpid, m, false)) }
          case Failure(t) =>
            t.printStackTrace()
        }
      }
    case SwitchDisconnected(dpid, driver: MessageDriver[_]) =>
      castDriver(driver) foreach { d => handleDisconnected(d, dpid) }
  }
}
