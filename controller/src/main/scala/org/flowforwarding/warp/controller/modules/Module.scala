/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules

import scala.collection.immutable.Iterable
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.concurrent.ExecutionContext.Implicits.global

import akka.pattern.pipe
import akka.actor._

import org.flowforwarding.warp.controller.driver_interface.{OFMessage, MessageDriverFactory}
import org.flowforwarding.warp.controller.bus.{ServiceRequest, ServiceBusActor, MessageEnvelope, MessageBusActor}
import org.flowforwarding.warp.controller.modules.Module._

trait Module extends Actor with ActorLogging{

  protected def compatibleWith(factory: MessageDriverFactory[_]): Boolean
  protected def started(): Unit
  protected def shutdown(): Unit = { }

  override def receive: Receive = auxReceive

  /* Always mix this function when use become method
     in order to keep actor being warp-compatible module */
  protected def auxReceive: Receive = {
    case CheckCompatibility(factory) =>
      if (compatibleWith(factory)){
        started()
        sender ! true
      } else {
        context stop self
        sender ! false
      }
    case Shutdown =>
      shutdown()
      context stop self
  }

  /* Use this method instead of context.become */
  protected def setReceive(rcv: Receive) = context become (auxReceive orElse rcv)
}

object Module{
  // direct messages, they do not go through event bus
  case class CheckCompatibility[T <: OFMessage](module: MessageDriverFactory[T])
  case object Shutdown
}


trait MessageConsumer extends MessageBusActor with Module{
  protected def handleEvent(e: MessageEnvelope): Unit

  protected abstract override def auxReceive: Receive = super.auxReceive orElse { case e: MessageEnvelope => handleEvent(e)}

  abstract override def shutdown(): Unit = {
    super.shutdown()
    unsubscribe()
  }
}

trait Service extends ServiceBusActor with Module{
  protected def handleRequest(e: ServiceRequest): Future[Any]

  protected abstract override def auxReceive: Receive = super.auxReceive orElse { case r: ServiceRequest => handleRequest(r) pipeTo sender}

  abstract override def shutdown(): Unit = {
    super.shutdown()
    unregisterService()
  }
}