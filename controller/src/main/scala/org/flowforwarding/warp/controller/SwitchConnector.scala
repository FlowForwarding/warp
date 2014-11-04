/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller

import java.util.concurrent.TimeUnit

import scala.util._
import scala.annotation.tailrec
import scala.concurrent.{Promise, Future}
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.{Actor, ActorRef}
import akka.io.{Tcp, TcpMessage}
import akka.util.{Timeout, ByteString}
import akka.pattern.{pipe, ask}

import spire.math.{UByte, UInt, ULong}

import org.flowforwarding.warp.controller.bus._
import org.flowforwarding.warp.controller.ModuleManager._
import org.flowforwarding.warp.controller.SwitchConnector._
import org.flowforwarding.warp.controller.modules.Service
import org.flowforwarding.warp.controller.driver_interface.{MessageDriverFactory, MessageDriver, OFMessage}

object SwitchConnector{
  // switch -> controller
  case class SwitchIncomingMessage[T <: OFMessage](id: ULong, driver: MessageDriver[T], msg: T) extends MessageEnvelope
  // controller -> switch
  case class SendToSwitch[T <: OFMessage](id: ULong, msg: T, needReply: Boolean) extends ServiceRequest
  private case class SendToSwitchInternal[T <: OFMessage](msg: T, needReply: Boolean) extends MessageEnvelope

  trait SendingResult[+T]{
    def map[R](f: T => R): SendingResult[R]
  }
  case object SendingSuccessfull extends SendingResult[Nothing] {
    override def map[R](f: Nothing => R): SendingResult[R] = this
  }
  case class SendingFailed(cause: Throwable) extends SendingResult[Nothing] {
    override def map[R](f: Nothing => R): SendingResult[R] = this
  }
  case class SwitchResponse[T](msg: T) extends SendingResult[T] {
    override def map[R](f: T => R): SendingResult[R] = SwitchResponse(f(msg))
  }

  case class NewDriverFactory(sender: ActorRef) extends MessageEnvelope

  case class SwitchDisconnected(dpid: ULong, driver: MessageDriver[_ <: OFMessage]) extends MessageEnvelope
  case class SwitchHandshake(dpid: ULong, driver: MessageDriver[_ <: OFMessage]) extends MessageEnvelope

  case class ForceDisconnect(dpid: ULong) extends ServiceRequest
  private case object ForceDisconnectInternal extends MessageEnvelope
}

private class SwitchConnector[T <: OFMessage, DriverType <: MessageDriver[T]](val bus: ControllerBus, controller: ActorRef) extends Service with MessageBusActor {

  override protected def handleRequest(e: ServiceRequest): Future[Any] = e match {
    case SendToSwitch(_, msg: T, needReply) =>
      self ? SendToSwitchInternal(msg, needReply)
    case ForceDisconnect(_) =>
      self ? ForceDisconnectInternal
  }

  override protected def started(): Unit = {
    subscribe("driverFactoryChange") { case NewDriverFactory(`controller`) => true}
    setReceive(startingState orElse handleClosed)
  }

  override protected def compatibleWith(factory: MessageDriverFactory[_]): Boolean = true // TODO: abstract over protocol

  implicit val timeout = Timeout(20, TimeUnit.SECONDS)

  def startingState: Actor.Receive = {
    case Tcp.Received(data) =>
      val tcpChannel = sender()
      askFirst(DriverByHello(data.toArray)) onSuccess {
        case DriverFoundResponse(driver: DriverType, supportedVersions) =>
          val handshake = driver.getHelloMessage(supportedVersions) ++ driver.getFeaturesRequest
          tcpChannel ! TcpMessage.write(ByteString.fromArray(handshake))
          setReceive(waitingForPDID(tcpChannel, driver) orElse handleClosed)
        case nf: DriverNotFoundResponse =>
          setReceive(handleClosed)
      }
  }

  @tailrec
  private def decodeMessages(driver: DriverType, messages: List[T], data: Array[Byte]): (List[T], Array[Byte]) = {
    driver decodeMessage data match {
      case (Success(msg), rest) if rest.length > 0 || rest.length == data.length => decodeMessages(driver, msg :: messages, rest)
      case (Success(msg), rest) if rest.length == 0 => (msg :: messages, rest)
      case (Failure(t), rest) if rest.length == data.length => (messages, rest)
      case (Failure(t), rest) => t.printStackTrace(); (messages, rest)
    }
  }

  private def publishMessages(driver: DriverType, messages: Seq[T], dpid: ULong) = {
    messages foreach { m => publishMessage(SwitchIncomingMessage(dpid, driver, m))}
  }

  def newDriverFactory(askVersion: UByte, newState: DriverType => Receive) =
    askFirst(DriverByVersion(askVersion)) onSuccess {
      case DriverFoundResponse(driver: DriverType, _) => setReceive(newState(driver))
    }

  def waitingForPDID(tcpChannel: ActorRef, driver: DriverType): Actor.Receive = {
    case Tcp.Received(data) =>
      driver.getDPID(data.toArray) match {
        case Success(dpid) =>
          val (messages, rest) = decodeMessages(driver, Nil, data.toArray)
          setReceive(handshakedState(tcpChannel, driver, dpid, rest))
          publishMessage(SwitchHandshake(dpid, driver))
          registerService {
            case SendToSwitch(`dpid`, _: T, _) | ForceDisconnect(`dpid`) => true
          }
          publishMessages(driver, messages, dpid)
        case Failure(t) =>
          println("Unable to get DPID: " + t)
          context stop self
      }

    case NewDriverFactory(`controller`) =>
      newDriverFactory(driver.versionCode, d => waitingForPDID(tcpChannel, d) orElse handleClosed)
  }

  // dpid x xid => promise of response
  val awaitingResponses = scala.collection.mutable.Map[(ULong, UInt), Promise[SwitchResponse[T]]]()

  def handshakedState(tcpChannel: ActorRef, driver: DriverType, dpid: ULong, prevBytes: Array[Byte]): Actor.Receive = {
    case Tcp.Received(data) =>
      val (messages, rest) = decodeMessages(driver, Nil, prevBytes ++: data.toArray)
      publishMessages(driver, messages, dpid)
      messages foreach { msg =>
        awaitingResponses.remove((dpid, driver.getXid(msg))) foreach {
          _ complete Success(SwitchResponse(msg))
        }
      }
      setReceive(handshakedState(tcpChannel, driver, dpid, rest))

    case SendToSwitchInternal(msg: T, needReply) =>
      driver encodeMessage msg match {
        case Success(bytes) =>
          println("[DEBUG]: " + bytes.toVector)
          tcpChannel ! TcpMessage.write(ByteString.fromArray(bytes))
          if(needReply) {
            val p = Promise[SwitchResponse[T]]()
            awaitingResponses((dpid, driver.getXid(msg))) = p
            p.future pipeTo sender()
          }
          else
            sender() ! SendingSuccessfull
        case Failure(t) =>
          t.printStackTrace()
          sender() ! SendingFailed(t)
      }

    case ForceDisconnect =>
      tcpChannel ! TcpMessage.close
      sender ! (()) // TODO: reasonable value??

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




