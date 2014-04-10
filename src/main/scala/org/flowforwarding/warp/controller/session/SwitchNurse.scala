/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.controller.session

import akka.actor.{Actor, ActorRef}
import akka.io.{Tcp, TcpMessage}
import akka.util.{Timeout, ByteString}
import akka.pattern.ask
import java.net.InetSocketAddress

object SwitchNurse{
  case class SendToSwitch(msg: Array[Byte])
  case class AcceptConnection(handler: ActorRef, versionCode: Short, handshake: Array[Byte])
  case class RejectConnection(handler: ActorRef, versionCodes: Array[Short], errorData: Array[Byte])
}

/* One handler - one protocol factory */
class SwitchNurse(ofSessionHandlers: Set[ActorRef], remoteAddress: InetSocketAddress, localAddress: InetSocketAddress) extends Actor {
  import SwitchNurse._
  import LowLevelSessionHandler._

  val sw = Switch(remoteAddress, localAddress)

  def receive = startingState orElse handleClosed

  def startingState: Actor.Receive = {
    case Tcp.Received(data) =>
      ofSessionHandlers foreach { _ ! InitialMessageData(sw, data.toArray) }
      println("[OF-INFO] Hello from switch " + sw)
      context become (waitingForVersion(sender) orElse handleClosed)
  }

  var rejectRepliesCount = 0

  def waitingForVersion(tcpChannel: ActorRef): Actor.Receive = {
    case AcceptConnection(handler, vc, data) => // handshake message to be sent to the switch  // TODO: add handlers priority??
      implicit val timeout = Timeout(5000)
      tcpChannel ! TcpMessage.write(ByteString.fromArray(data))
      println("[OF-INFO] Hello to switch " + sw)
      println("[OF-INFO] Code of protocol version is 0x" + vc.toLong.toHexString)
      context become (waitingForPDID(tcpChannel, sender, handler) orElse handleClosed)
    case RejectConnection(_, versions, errorData) =>
      rejectRepliesCount += 1
      if (rejectRepliesCount == ofSessionHandlers.size){
        println(versions.map(_.toLong.toHexString).mkString("[OF-INFO] None of specified handlers can handle OpenFlow protocol versions ", ", 0x", ""))
        tcpChannel ! TcpMessage.write(ByteString.fromArray(errorData))
        context become handleClosed
      }
  }

  def waitingForPDID(tcpChannel: ActorRef, requester: ActorRef, handler: ActorRef): Actor.Receive = {
    case Tcp.Received(data) =>
      println("[OF-INFO] Handshaked with Switch  " + sw)
      requester ! ReceivedMessage(sw, data.toArray)
      context become (handshakedState(tcpChannel, handler) orElse handleClosed)
  }

  def handshakedState(tcpChannel: ActorRef, handler: ActorRef): Actor.Receive = {
    case Tcp.Received(data) =>
      println("[OF-INFO] Connected to Switch " + sw)
      handler ! ReceivedMessage(sw, data.toArray)
    case SendToSwitch(data) =>
      tcpChannel ! TcpMessage.write(ByteString.fromArray(data))
  }

  def handleClosed: Actor.Receive = {
    case _: Tcp.ConnectionClosed => context stop self
  }
}




