package org.flowforwarding.warp.controller.driver_interface

import scala.util.{Failure, Success, Try}
import scala.reflect.ClassTag

import org.flowforwarding.warp.controller._
import spire.math.{UShort, UByte, UInt, ULong}
import org.flowforwarding.warp.controller.bus.{ControllerBus, MessageEnvelope, ServiceBusActor}
import org.flowforwarding.warp.controller.modules.MessageConsumer
import org.flowforwarding.warp.controller.SwitchConnector.{SwitchDisconnected, SwitchOutgoingMessage, SwitchIncomingMessage, SwitchHandshake}

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

trait OfpFeaturesExtractor[-T <: OFMessage] extends OfpVersionSupport{
  def getDPID(in: Array[Byte]): Try[ULong]
  def getXid(msg: T): UInt
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
            messages foreach { m => publishMessage(SwitchOutgoingMessage(dpid, m)) }
          case Failure(t) =>
            t.printStackTrace()
        }
      }
    case SwitchDisconnected(dpid, driver: MessageDriver[_]) =>
      castDriver(driver) foreach { d => handleDisconnected(d, dpid) }
  }
}
