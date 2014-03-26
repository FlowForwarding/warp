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
 
package org.flowforwarding.warp.controller

import java.net.InetSocketAddress

import akka.actor._
import akka.io.{Tcp, TcpMessage}

import org.flowforwarding.warp.controller.session._

case class Configuration(ip: String = "10.17.10.126", tcpPort: Int = 6633)

object Controller {
  def launch(protocolHandlers: Set[SessionHandlerLauncher], config: Configuration = Configuration())
            (implicit actorSystem: ActorSystem = ActorSystem.create("OfController")) = {
    val manager = Tcp.get(actorSystem).manager
    actorSystem.actorOf(Props.create(classOf[Controller], manager, config, protocolHandlers), "Controller-Dispatcher")
  }
}

private class Controller(manager: ActorRef, config: Configuration, messageHandlers: Set[SessionHandlerLauncher]) extends Actor {

  var sessionHandlers: Set[ActorRef] = Set.empty

  override def preStart() {
    manager ! TcpMessage.bind(self, new InetSocketAddress(config.ip, config.tcpPort), 100)
  }

  def receive = {
    case msg: Tcp.Bound =>
      manager ! msg
      sessionHandlers = messageHandlers map { _.launch }
    case Tcp.CommandFailed =>
      context stop self
    case connected: Tcp.Connected =>
      manager ! connected
      println("[INFO] Getting Switch connection \n")
      val connectionHandler = context.actorOf(Props.create(classOf[session.SwitchNurse], sessionHandlers))
      sender ! TcpMessage.register(connectionHandler)
    // TODO: handle messages from RestApi
  }
}

