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

import scala.util.Try

import akka.actor.{ActorRefFactory, Props, Actor, ActorRef}
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask
import akka.util.Timeout

class Switch

case class SessionHandlerLauncher(handlerClass: Class[_], launchArgs: AnyRef*) {
  def launch(implicit f: ActorRefFactory) = f.actorOf(Props.create(handlerClass, launchArgs: _*))
}

object OFSessionHandler{
  case class InitialMessageData(sw: Switch, data: Array[Byte])
  case class ReceivedMessage(sw: Switch, data: Array[Byte])
}

case class SwitchInfo[T <: OFMessage](dpid: Long, tcpChannel: ActorRef, driver: MessageDriver[T])

abstract class OFSessionHandler[T <: OFMessage](driverFactory: MessageDriverFactory[T]) extends Actor {
  import OFSessionHandler._
  import SwitchNurse._
  
  private val swInfo = scala.collection.mutable.Map[Switch, SwitchInfo[T]]()

  implicit val timeout = Timeout(5000)

  // TODO: add closed
  def handshaked(versionCode: Short, dpid: Long) { }
  def connected(versionCode: Short) { }

  protected def getHandshakeMessage(versionCode: Short, msg: T): Seq[T]
  protected def onReceivedMessage(versionCode: Short, dpid: Long, msg: T): Seq[T]

  def sequence[A](s: Seq[Try[A]]): Try[Seq[A]] = Try(s map { _.get })

  def handleIncoming(driver: MessageDriver[T], msg: Array[Byte], f: T => Seq[T]): Try[Array[Byte]] =
    for{decoded <- driver.decodeMessage(msg)
        messages <- sequence(f(decoded) map driver.encodeMessage)
        reply = messages reduce { _ ++ _ }
    } yield reply

  def receive = {
    case InitialMessageData(sw, data) =>
      val version = driverFactory.getVersion(data).get
      connected(version)
      val cRef = sender
      val hRef = self
      driverFactory.get(data) flatMap {
        d =>
          val processHandshaked = (getHandshakeMessage _).curried(version)
          handleIncoming(d, data, processHandshaked).toOption.map((d, _))
      } match {
        case Some((d, hs)) =>
          sender ? AcceptVersion(hRef, d.versionCode, hs) map { reply =>
            val incoming = reply.asInstanceOf[ReceivedMessage].data // this message must contain "features_reply" structure
            d.getDPID(incoming) map { dpid =>
              swInfo(sw) = SwitchInfo(dpid, cRef, d)
              handshaked(d.versionCode, dpid)
              hRef ! reply  // forward for further processing
            }
          }
        case None => sender ! RejectVersion(hRef, version)
      }
    case ReceivedMessage(sw, data) =>
      val info = swInfo(sw)
      val processReceived = (onReceivedMessage _).curried(info.driver.versionCode)(info.dpid)
      handleIncoming(info.driver, data, processReceived) foreach { info.tcpChannel ! SendToSwitch(_) }
  }
}