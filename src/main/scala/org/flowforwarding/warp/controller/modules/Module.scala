package org.flowforwarding.warp.controller.modules

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import akka.pattern.pipe
import akka.actor.Actor

import org.flowforwarding.warp.controller.driver_interface.{OFMessage, MessageDriverFactory}
import org.flowforwarding.warp.controller.bus.{ServiceRequest, ServiceBusActor, MessageEnvelope, MessageBusActor}
import org.flowforwarding.warp.controller.modules.Module._

trait Module extends Actor{

  protected def compatibleWith(factory: MessageDriverFactory[_]): Boolean
  protected def started(): Unit
  protected def shutdown(): Unit = { }

  override def receive: Receive = moduleReceive

  def moduleReceive: Receive = {
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
}

object Module{
  // direct messages, they do not go through event bus
  case class CheckCompatibility[T <: OFMessage](module: MessageDriverFactory[T])
  case object Shutdown
}


trait MessageConsumer extends MessageBusActor with Module{
  protected def handleEvent(e: MessageEnvelope): Unit

  abstract override def moduleReceive: Receive = super.moduleReceive orElse { case e: MessageEnvelope => handleEvent(e)}

  abstract override def shutdown(): Unit = {
    super.shutdown()
    unsubscribe()
  }
}

trait Service extends ServiceBusActor with Module{
  protected def handleRequest(e: ServiceRequest): Future[Any]

  abstract override def moduleReceive: Receive = super.moduleReceive orElse { case r: ServiceRequest => handleRequest(r) pipeTo sender}

  abstract override def shutdown(): Unit = {
    super.shutdown()
    unregisterService()
  }
}