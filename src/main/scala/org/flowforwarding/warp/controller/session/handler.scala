/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.controller.session

import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.{ActorRefFactory, Props, Actor, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import java.net.InetSocketAddress

case class Switch(remoteAddress: InetSocketAddress, localAddress: InetSocketAddress)

class SessionHandlerRef(handlerClass: Class[_], launchArgs: Array[AnyRef] = Array()) {

  /* handlerClass must be a subclass of LowLevelSessionHandler */
  assert(classOf[LowLevelSessionHandler[_, _]].isAssignableFrom(handlerClass))

  def this(handlerClass: Class[_], launchArgs: AnyRef*) = this(handlerClass, launchArgs.toArray)
  def launch(implicit f: ActorRefFactory) = f.actorOf(Props.create(handlerClass, launchArgs: _*))
}

object LowLevelSessionHandler{
  case class InitialMessageData(sw: Switch, data: Array[Byte])
  case class ReceivedMessage(sw: Switch, data: Array[Byte])
  case class ConnectionClosed(sw: Switch)
}

case class SwitchInfo[T <: OFMessage, DriverType <: MessageDriver[T]](dpid: Long, tcpChannel: ActorRef, driver: DriverType)

abstract class LowLevelSessionHandler[T <: OFMessage, DriverType <: MessageDriver[T]](driverFactory: MessageDriverFactory[T, DriverType]) extends Actor {
  import LowLevelSessionHandler._
  import SwitchNurse._
  
  private val swInfo = scala.collection.mutable.Map[Switch, SwitchInfo[T, DriverType]]()

  implicit val timeout = Timeout(5000)

  def handshaked(versionCode: Short, dpid: Long) { }
  def connected(remoteAddress: InetSocketAddress, localAddress: InetSocketAddress) { }
  def disconnected(remoteAddress: InetSocketAddress, localAddress: InetSocketAddress, versionCode: Short, dpid: Long) { }

  protected def onReceivedMessage(driver: DriverType, dpid: Long, msg: T): Seq[T]

  def sequence[A](s: Seq[Try[A]]): Try[Seq[A]] = Try(s map { _.get })

  def handleIncoming(driver: DriverType, msg: Array[Byte], f: T => Seq[T]): Try[Array[Byte]] =
    for{decoded <- driver.decodeMessage(msg)
        messages <- sequence(f(decoded) map driver.encodeMessage)
        reply = messages reduce { _ ++ _ }
    } yield reply

  def definedHandlersVersions: Array[Short]

  def receive = {
    case InitialMessageData(sw @ Switch(remoteAddress, localAddress), helloData) =>
      connected(remoteAddress, localAddress)
      val cRef = sender
      val hRef = self
      driverFactory.highestCommonVersion(helloData) match {
        case Left(version) if !definedHandlersVersions.contains(version) =>
          val driver = driverFactory.get(version)
          val errorData = driver.rejectVersionError("No handlers defined.")
          cRef ! RejectConnection(hRef, Array(version), errorData)
        case Left(version) =>
          val driver = driverFactory.get(version)
          val handshake = driver.getHelloMessage(driverFactory.supportedVersions) ++ driver.getFeaturesRequest
          sender ? AcceptConnection(hRef, version, handshake) map { reply =>
            val incoming = reply.asInstanceOf[ReceivedMessage].data // this message must contain "features_reply" structure
            driver.getDPID(incoming) map { dpid =>
              swInfo(sw) = SwitchInfo(dpid, cRef, driver)
              handshaked(version, dpid)
              hRef ! reply  // forward for further processing
            }
          }
        case Right((versions, error)) => cRef ! RejectConnection(hRef, versions, error)
      }
    case ReceivedMessage(sw, data) =>
      val info = swInfo(sw)
      val processReceived = (onReceivedMessage _).curried(info.driver)(info.dpid)
      handleIncoming(info.driver, data, processReceived) foreach { info.tcpChannel ! SendToSwitch(_) }
    case ConnectionClosed(sw @ Switch(remoteAddress, localAddress)) =>
      disconnected(remoteAddress, localAddress, swInfo(sw).driver.versionCode, swInfo(sw).dpid)
  }
}