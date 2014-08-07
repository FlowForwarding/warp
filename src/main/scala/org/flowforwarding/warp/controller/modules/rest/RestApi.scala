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

import org.flowforwarding.warp.controller._
import org.flowforwarding.warp.controller.driver_interface.{MessageDriver, MessageDriverFactory}
import org.flowforwarding.warp.controller.bus.{ServiceBusActor, ServiceBus, ServiceRequest}
import org.flowforwarding.warp.controller.modules.{Module, Service}

case class RestApiRequest(appName: String, request: HttpRequest) extends ServiceRequest

class RestApiServer(val bus: ServiceBus, serverPrefix: String) extends Module with ServiceBusActor{
  override def started(): Unit = {
    IO(Http)(context.system) ! Http.Bind(self, interface = "localhost", port = 8080)
  }

  override def shutdown(): Unit = { }

  override def compatibleWith(factory: MessageDriverFactory[_]): Boolean = true

  override def moduleReceive = super.moduleReceive orElse {
    case r @ HttpRequest(method, uri, headers, entity, protocol) if uri.path.tail.head.toString == serverPrefix =>
      println("Server prefix: " + uri.path.tail.head)
      println("Service prefix: "  + uri.path.tail.tail.tail.head)
      // Skip slash, restPrefix, slash and get next segment. It must be prefix of a rest service.
      // There should be a more convenient way to match paths.
      val p = uri.path.tail.tail.tail.head.toString
      askService(RestApiRequest(p, r)) pipeTo sender()

    case c: Http.Connected => // TODO: handle other Http messages
      sender() ! Http.Register(self)
  }
}

abstract class RestApiService(serverPrefix: String) extends Module with Service with HttpServiceBase{

  val servicePrefix: String
  def route: Route

  private implicit val timeout = Timeout(5, TimeUnit.SECONDS)

  override def started(): Unit = {
    registerService { case RestApiRequest(`servicePrefix`, _) => true }
  }

  override def compatibleWith(factory: MessageDriverFactory[_]): Boolean = true

  override protected def handleRequest(e: ServiceRequest): Future[HttpResponse] = e match {
    case RestApiRequest(`servicePrefix`, request) =>
      self ? request map {
        case r: HttpResponse => r
        case value => HttpResponse(StatusCodes.InternalServerError, HttpEntity(s"Value $value is not a valid response."))
      }
  }

  override def moduleReceive = super.moduleReceive orElse runRoute(pathPrefix(serverPrefix / servicePrefix) { route })
}