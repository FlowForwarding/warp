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

import akka.io.IO
import akka.util.Timeout
import akka.pattern.{ask, pipe}

import spray.routing._
import spray.can.Http
import spray.http.{HttpEntity, StatusCodes, HttpResponse, HttpRequest}

import org.flowforwarding.warp.controller.driver_interface.MessageDriverFactory
import org.flowforwarding.warp.controller.bus.{ServiceBusActor, ServiceBus, ServiceRequest}
import org.flowforwarding.warp.controller.modules.{Module, Service}

case class RestApiRequest(appName: String, request: HttpRequest) extends ServiceRequest

class RestApiServer(val bus: ServiceBus, serverPrefixes: Array[String]) extends Module with ServiceBusActor{
  // TODO: fix creation of modules in module manager (allow passing arrays of String to a constructor)
  def this(bus: ServiceBus, serverPrefix: String) = this(bus, Array(serverPrefix))

  override def started(): Unit = {
    IO(Http)(context.system) ! Http.Bind(self, interface = "localhost", port = 8080)
  }

  override def shutdown(): Unit = { }

  override def compatibleWith(factory: MessageDriverFactory[_]): Boolean = true

  override def moduleReceive = super.moduleReceive orElse {
    case r @ HttpRequest(method, uri, headers, entity, protocol) =>
      val path = uri.path.toString
      serverPrefixes.find(path.startsWith) foreach { serverPrefix =>
        val serviceUri = path.stripPrefix(serverPrefix)
        println("Server prefix: " + serverPrefix)
        println("Service uri: " + serviceUri)
        askFirst(RestApiRequest(serviceUri, r)) pipeTo sender()
      }
    case c: Http.Connected => // TODO: handle other Http messages
      sender() ! Http.Register(self)
  }
}

abstract class RestApiService(serverPrefix: String) extends Module with Service with HttpServiceBase{

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

  override def moduleReceive = {
    val pathPrefixMatcher = PathMatchers.separateOnSlashes(serverPrefix.drop(1) + servicePrefix)
    super.moduleReceive orElse runRoute(pathPrefix(pathPrefixMatcher) { route })
  }
}