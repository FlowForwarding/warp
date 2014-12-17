/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.rest

import java.util.concurrent.TimeUnit

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._

import akka.actor.ActorRef
import akka.io.IO
import akka.util.Timeout
import akka.pattern.{ask, pipe}

import spray.routing._
import spray.can.Http
import spray.http.{HttpEntity, StatusCodes, HttpResponse, HttpRequest}

import org.flowforwarding.warp.driver_api._
import org.flowforwarding.warp.controller.bus.{ServiceBusActor, ServiceBus, ServiceRequest}
import org.flowforwarding.warp.controller.modules.{Module, Service}

import scala.concurrent.duration.Duration

case class RestApiRequest(appName: String, request: HttpRequest) extends ServiceRequest

class RestApiServer(val bus: ServiceBus, serverPrefixes: Array[String]) extends Module with ServiceBusActor{
  // TODO: fix creation of modules in module manager (allow passing arrays of String to a constructor)
  def this(bus: ServiceBus, serverPrefix: String) = this(bus, Array(serverPrefix))

  var dispatcher: Option[ActorRef] = None

  override def started(): Unit = {
    IO(Http)(context.system) ! Http.Bind(self, interface = "localhost", port = 8080)
  }

  override def shutdown(): Unit = {
    dispatcher foreach  { _ ! Http.Unbind(Duration(0, TimeUnit.SECONDS)) }
    super.shutdown()
  }

  override def compatibleWith(factory: MessageDriverFactory[_]): Boolean = true

  override def auxReceive = super.auxReceive orElse {
    case r @ HttpRequest(method, uri, headers, entity, protocol) =>
      val path = uri.path.toString
      serverPrefixes.find(path.startsWith) foreach { serverPrefix =>
        val serviceUri = path.stripPrefix(serverPrefix)
        log.debug("Server prefix: " + serverPrefix)
        log.debug("Service uri: " + serviceUri)
        askFirst(RestApiRequest(serviceUri, r)) pipeTo sender()
      }
    case Http.Bound(_) =>
      dispatcher = Some(sender())
    case Http.CommandFailed(cmd)  =>
      log.debug("Command failed: " + cmd.failureMessage)
    case c: Http.Connected => // TODO: handle other Http messages
      sender() ! Http.Register(self)
  }
}

abstract class RestApiService(serverPrefix: String) extends Service with HttpServiceBase{

  // must starts with slash
  val servicePrefix: String
  def route: Route

  private implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  override def started(): Unit = {
    registerService { case RestApiRequest(path, _) => path startsWith servicePrefix }
  }

  override def compatibleWith(factory: MessageDriverFactory[_]): Boolean = true

  override protected def handleRequest(e: ServiceRequest): Future[HttpResponse] = e match {
    case RestApiRequest(path, request) =>
      self ? request map {
        case r: HttpResponse => r
        case value => HttpResponse(StatusCodes.InternalServerError, HttpEntity(s"Value $value is not a valid response."))
      }
  }

  override def auxReceive = {
    val pathPrefixMatcher = PathMatchers.separateOnSlashes(serverPrefix.drop(1) + servicePrefix)
    super.auxReceive orElse runRoute(pathPrefix(pathPrefixMatcher) { route })
  }
}