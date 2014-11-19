/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.bus

import java.util.concurrent.{TimeoutException, TimeUnit, ConcurrentHashMap}

import scala.collection.JavaConversions._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.{Actor, ActorRef}
import akka.pattern.{ask, pipe}
import akka.util.Timeout

trait MessageEnvelope
trait ServiceRequest
trait MessageEnvelopePredicate { def test(m: MessageEnvelope): java.lang.Boolean }

trait MessageBus {
  protected final val subscribers = new ConcurrentHashMap[ActorRef, ConcurrentHashMap[String, MessageEnvelope => Boolean]]

  def subscribe(subscriber: ActorRef, subscriptionName: String, subscriptionPredicate: PartialFunction[MessageEnvelope, Boolean]): Unit = {
    subscribers.putIfAbsent(subscriber, new ConcurrentHashMap)
    subscribers.get(subscriber).put(subscriptionName, subscriptionPredicate orElse { case _ => false })
  }

  def unsubscribe(subscriber: ActorRef, subscriptionName: String): Unit =
    subscribers.get(subscriber).remove(subscriptionName)

  def unsubscribe(subscriber: ActorRef): Unit =
    subscribers.remove(subscriber)

  def publishMessage(event: MessageEnvelope): Unit =
    subscribers foreach { case (subscriber, predicates) =>
      if (predicates exists { _._2(event) }) subscriber ! event
    }
}

class ServiceNotFoundException extends Exception

/* TODO: Register multiple services per actor */
trait ServiceBus{
  private implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  protected final val services = new ConcurrentHashMap[ActorRef, ServiceRequest => Boolean]

  def registerService(service: ActorRef, acceptedRequests: PartialFunction[ServiceRequest, Boolean]): Unit =
    services.put(service, acceptedRequests orElse { case _ => false })

  def unregisterService(subscriber: ActorRef): Unit =
    services.remove(subscriber)

  def askFirst(requester: ActorRef, request: ServiceRequest): Future[Any] = {
    services collectFirst { // collect ???
      case (subscriber, accepts) if accepts(request) => subscriber ? request
    } match {
      case Some(response) => response pipeTo requester
      case None => Future.failed(new ServiceNotFoundException)
    }
  }

  def askAll(requester: ActorRef, request: ServiceRequest): Future[Array[Any]] = {
    val responses = services collect { // collect ???
      case (subscriber, accepts) if accepts(request) =>
        subscriber ? request recoverWith {
          case to: TimeoutException => Future.failed(new ServiceNotFoundException)
          case t: Throwable => Future.failed(t)
        }
    }
    Future.sequence(responses) map {_.toArray } pipeTo requester
  }
}

trait ServiceBusActor extends Actor{
  protected val bus: ServiceBus

  protected final def askFirst(request: ServiceRequest): Future[Any] = bus.askFirst(self, request)

  protected final def askAll(request: ServiceRequest): Future[Array[Any]] = bus.askAll(self, request)

  protected final def registerService(acceptedRequests: PartialFunction[ServiceRequest, Boolean]) =
    bus.registerService(self, acceptedRequests: PartialFunction[ServiceRequest, Boolean])

  protected final def unregisterService(): Unit = bus.unregisterService(self)
}

trait MessageBusActor extends Actor{
  protected val bus: MessageBus

  protected final def publishMessage(msg: MessageEnvelope) = bus.publishMessage(msg)

  protected final def subscribe(subscriptionName: String)(subscriptionPredicate: PartialFunction[MessageEnvelope, Boolean]): Unit =
    bus.subscribe(self, subscriptionName, subscriptionPredicate)

  /* Java API */
  protected final def subscribe(subscriptionName: String, subscriptionPredicate: MessageEnvelopePredicate): Unit =
    bus.subscribe(self, subscriptionName, { case me => subscriptionPredicate.test(me)})

  protected final def unsubscribe(subscriptionName: String): Unit = bus.unsubscribe(self, subscriptionName)

  protected final def unsubscribe(): Unit = bus.unsubscribe(self)
}

trait ControllerBus extends ServiceBus with MessageBus
trait ControllerBusActor extends ServiceBusActor with MessageBusActor { val bus: ControllerBus }
