/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.controller

import java.io.File
import java.util.concurrent.TimeUnit
import java.net.{URL, URLClassLoader, InetSocketAddress}

import scala.io.StdIn
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

private class InputHandler(controller: ActorRef) extends Actor{

  override def preStart() {
    val input = StdIn.readLine(">>")
    self ! input
  }

  import org.flowforwarding.warp.controller.ModuleManager._

  object Port{
    def unapply(input: String): Option[Int] = Try { input.toInt } filter { i => i >= 0 && i <= 65535 } toOption
  }

  def addPath(s: String) {
    val urlClassLoader = Thread.currentThread.getContextClassLoader.asInstanceOf[URLClassLoader]
    val urlClass = classOf[URLClassLoader]
    val method = urlClass.getDeclaredMethod("addURL", classOf[URL])
    method.setAccessible(true)
    method.invoke(urlClassLoader, new File(s).toURI.toURL)
  }

  object ControllerCommand {
    def unapply(input: String): Option[Any] = input.split(' ') match {
      case Array("start", ip, Port(tcpPort)) =>
        Some(Start(new InetSocketAddress(ip, tcpPort)))  // TODO: Match IP
      case Array("set", "factory", factoryClass, "-p", args @ _*) =>
        Some(SetFactory(factoryClass, args.toArray))
      case Array("add", "module", moduleName, "of", "type", moduleClass, "-p", args @ _*) =>
        Some(AddModule(moduleName, moduleClass, args.toArray))
      case Array("rm", "module", moduleName) =>
        Some(RemoveModule(moduleName))
      case _ => None
    }
  }

  implicit val t = new Timeout(40, TimeUnit.SECONDS)

  def receive = {
    case ControllerCommand(c) =>

      controller ? c onComplete println
      self ! StdIn.readLine(">>")
    case s: String if s == "" | s.startsWith("#") =>
      self ! StdIn.readLine()
    case c =>
      println("Invalid command " + c + ": " + c.getClass)
  }
}
