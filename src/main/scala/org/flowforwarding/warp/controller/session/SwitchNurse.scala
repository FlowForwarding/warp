/**
 * Copyright 2014 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flowforwarding.warp.controller.session

import akka.util.{Timeout, ByteString}
import akka.actor.{Actor, ActorRef}
import akka.io.{Tcp, TcpMessage}

object SwitchNurse{
  case class SendToSwitch(msg: Array[Byte])
  case class AcceptVersion(handler: ActorRef, versionCode: Long, handshake: Array[Byte])
  case class RejectVersion(handler: ActorRef, versionCode: Long)
}

/* One handler per version of protocol */
class SwitchNurse(ofSessionHandlers: Set[ActorRef]) extends Actor {
  import SwitchNurse._
  import OFSessionHandler._

  def receive = startingState orElse handleClosed

  def startingState: Actor.Receive = {
    case Tcp.Received(data) =>
      val sw = new Switch
      ofSessionHandlers foreach { _ ! InitialMessageData(sw, data.toArray) }
      println("[OF-INFO] Hello from switch " + sw)
      context become (waitingForVersion(sw, sender) orElse handleClosed)
  }

  var rejectRepliesCount = 0

  def waitingForVersion(sw: Switch, tcpChannel: ActorRef): Actor.Receive = {
    case AcceptVersion(handler, vc, data) => // handshake message to be sent to the switch
      implicit val timeout = Timeout(5000)
      tcpChannel ! TcpMessage.write(ByteString.fromArray(data))
      println("[OF-INFO] Hello to switch " + sw)
      println("[OF-INFO] Code of protocol version is 0x" + vc.toHexString)
      context become (waitingForDPID(sw, tcpChannel, sender, handler) orElse handleClosed)
    case RejectVersion(_, vc) =>
      rejectRepliesCount += 1
      if (rejectRepliesCount == ofSessionHandlers.size){
        println("[OF-INFO] None of specified handlers can handle OpenFlow protocol version with code 0x" + vc.toHexString)
        context become handleClosed
      }
  }

  def waitingForDPID(sw: Switch, tcpChannel: ActorRef, requester: ActorRef, handler: ActorRef): Actor.Receive = {
    case Tcp.Received(data) =>
      println("[OF-INFO] Handshaked with Switch  " + sw)
      requester ! ReceivedMessage(sw, data.toArray)
      context become (handshakedState(sw, tcpChannel, handler) orElse handleClosed)
  }

  def handshakedState(sw: Switch, tcpChannel: ActorRef, handler: ActorRef): Actor.Receive = {
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




