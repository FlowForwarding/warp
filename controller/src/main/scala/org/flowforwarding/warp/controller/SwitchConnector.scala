/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller

import java.util.concurrent.TimeUnit

import scala.annotation.tailrec
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.{Actor, ActorRef}
import akka.io.{Tcp, TcpMessage}
import akka.util.{Timeout, ByteString}

import org.flowforwarding.warp.controller.driver_interface.{MessageDriver, OFMessage}
import spire.math.{UByte, UInt, ULong}
import org.flowforwarding.warp.controller.bus.{MessageEnvelope, ControllerBus, ControllerBusActor}
import org.flowforwarding.warp.controller.ModuleManager.{DriverByVersion, DriverNotFoundResponse, DriverFoundResponse, DriverByHello}
import org.flowforwarding.warp.controller.SwitchConnector._
import org.flowforwarding.warp.controller.SwitchConnector.SwitchOutgoingMessage
import org.flowforwarding.warp.controller.ModuleManager.DriverFoundResponse
import org.flowforwarding.warp.controller.ModuleManager.DriverByVersion
import org.flowforwarding.warp.controller.SwitchConnector.SwitchIncomingMessage
import scala.util.Failure
import scala.Some
import org.flowforwarding.warp.controller.SwitchConnector.SwitchHandshake
import org.flowforwarding.warp.controller.ModuleManager.DriverByHello
import org.flowforwarding.warp.controller.ModuleManager.DriverNotFoundResponse
import scala.util.Success
import org.flowforwarding.warp.controller.SwitchConnector.NewDriverFactory

object SwitchConnector{
  // switch -> controller
  case class SwitchIncomingMessage[T <: OFMessage](id: ULong, driver: MessageDriver[T], msg: T) extends MessageEnvelope
  // controller -> switch
  case class SwitchOutgoingMessage[T <: OFMessage](id: ULong, msg: T) extends MessageEnvelope
  case class MessageSendingResult(dpid: ULong, xid: UInt, failure: Option[Throwable]) extends MessageEnvelope

  case class NewDriverFactory(sender: ActorRef) extends MessageEnvelope

  case class SwitchDisconnected(dpid: ULong, driver: MessageDriver[_ <: OFMessage]) extends MessageEnvelope
  case class SwitchHandshake(dpid: ULong, driver: MessageDriver[_ <: OFMessage]) extends MessageEnvelope

  case class ForceDisconnect(dpid: ULong) extends MessageEnvelope
}

private class SwitchConnector[T <: OFMessage, DriverType <: MessageDriver[T]](val bus: ControllerBus, controller: ActorRef) extends ControllerBusActor{

  implicit val timeout = Timeout(20, TimeUnit.SECONDS)

  subscribe("driverFactoryChange") { case NewDriverFactory(`controller`) => true }

  def receive = startingState orElse handleClosed

  def startingState: Actor.Receive = {
    case Tcp.Received(data) =>
      val tcpChannel = sender()
      askFirst(DriverByHello(data.toArray)) onSuccess {
        case DriverFoundResponse(driver: DriverType, supportedVersions) =>
          val handshake = driver.getHelloMessage(supportedVersions) ++ driver.getFeaturesRequest
          tcpChannel ! TcpMessage.write(ByteString.fromArray(handshake))
          context become (waitingForPDID(tcpChannel, driver) orElse handleClosed)
        case nf: DriverNotFoundResponse =>
          context become handleClosed
      }
  }

  @tailrec
  private def decodeMessages(driver: DriverType, messages: List[T], data: Array[Byte]): (List[T], Array[Byte]) = {
    driver decodeMessage data match {
      case (Success(msg), rest) if rest.length > 0  || rest.length == data.length => decodeMessages(driver, msg :: messages, rest)
      case (Success(msg), rest) if rest.length == 0                               => (msg :: messages, rest)
      case (Failure(t),   rest) if rest.length == data.length                     => (messages, rest)
      case (Failure(t),   rest)                                                   => t.printStackTrace(); (messages, rest)
    }
  }

  private def publishMessages(driver: DriverType, messages: Seq[T], dpid: ULong) = {
    messages foreach { m => publishMessage(SwitchIncomingMessage(dpid, driver, m)) }
  }

  def newDriverFactory(askVersion: UByte, newState: DriverType => Receive) =
    askFirst(DriverByVersion(askVersion)) onSuccess {
      case DriverFoundResponse(driver: DriverType, _) => context become newState(driver)
    }

  def waitingForPDID(tcpChannel: ActorRef, driver: DriverType): Actor.Receive = {
    case Tcp.Received(data) =>
      driver.getDPID(data.toArray) match {
        case Success(dpid) =>
          val (messages, rest) = decodeMessages(driver, Nil, data.toArray)
          context become handshakedState(tcpChannel, driver, dpid, rest)
          publishMessage(SwitchHandshake(dpid, driver))
          subscribe("afterHandshakeMessages") {
            case SwitchOutgoingMessage(`dpid`, _: T) | ForceDisconnect(_) => true
          }
          publishMessages(driver, messages, dpid)
        case Failure(t) =>
          println("Unable to get DPID: " + t)
          context stop self
      }

    case NewDriverFactory(`controller`) =>
      newDriverFactory(driver.versionCode, d => waitingForPDID(tcpChannel, d) orElse handleClosed)
  }

  def handshakedState(tcpChannel: ActorRef, driver: DriverType, dpid: ULong, prevBytes: Array[Byte]): Actor.Receive = {
    case Tcp.Received(data) =>
      val (messages, rest) = decodeMessages(driver, Nil, prevBytes ++: data.toArray)
      publishMessages(driver, messages, dpid)
      context become handshakedState(tcpChannel, driver, dpid, rest)

    case SwitchOutgoingMessage(`dpid`, msg: T) =>
      driver encodeMessage msg match {
        case Success(bytes) =>
          println("[DEBUG]: " + bytes.toVector)
          tcpChannel ! TcpMessage.write(ByteString.fromArray(bytes))
          publishMessage(MessageSendingResult(dpid, driver.getXid(msg), None))
        case Failure(t) =>
          t.printStackTrace()
          publishMessage(MessageSendingResult(dpid, driver.getXid(msg), Some(t)))
      }

    case ForceDisconnect(`dpid`) =>
      tcpChannel ! TcpMessage.close

    case NewDriverFactory(`controller`) =>
      newDriverFactory(driver.versionCode, d => handshakedState(tcpChannel, d, dpid, prevBytes))

    case _: Tcp.ConnectionClosed =>
      publishMessage(SwitchDisconnected(dpid, driver))
      context stop self
  }

  def handleClosed: Actor.Receive = {
    case _: Tcp.ConnectionClosed =>
      context stop self
  }
}




