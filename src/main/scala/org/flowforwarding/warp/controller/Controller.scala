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
  def launch(sessionHandlers: scala.collection.Set[SessionHandlerRef], config: Configuration)
            (implicit actorSystem: ActorSystem = ActorSystem.create("OfController")) = {
    val manager = Tcp.get(actorSystem).manager
    actorSystem.actorOf(Props.create(classOf[Controller], manager, config, sessionHandlers), "Controller-Dispatcher")
  }

  def launch(sessionHandlers: SessionHandlerRef*): ActorRef = {
    launch(sessionHandlers.toSet, Configuration())
  }

  def launch(sessionHandlers: java.util.Set[SessionHandlerRef], config: Configuration, actorSystem: ActorSystem) {
    val sh = scala.collection.JavaConversions.asScalaSet(sessionHandlers)
    launch(sh, config)(actorSystem)
  }

  def launch(sessionHandlers: java.util.Set[SessionHandlerRef]) {
    launch(sessionHandlers, Configuration(), ActorSystem.create("OfController"))
  }
}

private class Controller(manager: ActorRef, config: Configuration, messageHandlers: scala.collection.Set[SessionHandlerRef]) extends Actor {

  var sessionHandlers: scala.collection.Set[ActorRef] = Set.empty

  override def preStart() {
    manager ! TcpMessage.bind(self, new InetSocketAddress(config.ip, config.tcpPort), 100)
  }

  def receive = {
    case msg: Tcp.Bound =>
      manager ! msg
      sessionHandlers = messageHandlers map { _.launch }
    case Tcp.CommandFailed =>
      context stop self
    case c @ Tcp.Connected(remoteAddress, localAddress) =>
      manager ! c
      println("[INFO] Getting Switch connection \n")
      val connectionHandler = context.actorOf(Props.create(classOf[SwitchNurse], sessionHandlers, remoteAddress, localAddress))
      sender ! TcpMessage.register(connectionHandler)
    // TODO: handle messages from RestApi
  }
}

