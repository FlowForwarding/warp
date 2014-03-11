/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
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

