/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.opendaylight_rest.managers

import scala.concurrent.Future
import scala.reflect.ClassTag

import org.flowforwarding.warp.driver_api.MessageDriverFactory
import org.flowforwarding.warp.controller.bus.{ServiceRequest, ControllerBus}
import org.flowforwarding.warp.controller.modules.opendaylight_rest.sal.{NodeConnector, Node}
import org.flowforwarding.warp.controller.modules.{MessageConsumer, Service}

trait Broadcast
trait ProtocolInternal

trait NodeTag[NodeType <: Node[_], ConnectorType <: NodeConnector[_, NodeType]] {
  protected implicit val nodeTag: ClassTag[NodeType]
  protected implicit val connectorTag: ClassTag[ConnectorType]
}

abstract class AbstractManager[R <: ServiceRequest: ClassTag] extends Service{

  override protected def compatibleWith(factory: MessageDriverFactory[_]): Boolean = true

  override def started() = { registerService {
    case req => implicitly[ClassTag[R]].runtimeClass.isAssignableFrom(req.getClass) && !req.isInstanceOf[Broadcast] && !req.isInstanceOf[ProtocolInternal]
  }}

  import AbstractService._

  protected def reduceResponses[T](f: Array[Any] => T)(nodes: Array[Any]) =
    nodes collectFirst { case ip: InvalidParams => ip } getOrElse {
      if (nodes.nonEmpty && nodes.forall { _ == NotFound }) NotFound
      else f(nodes)
    }
}

trait AbstractService[NodeType <: Node[_], ConnectorType <: NodeConnector[_, NodeType]] extends Service with MessageConsumer {
  tag: NodeTag[NodeType, ConnectorType]  =>
  protected val bus: ControllerBus

  protected def checkNode(node: Node[_]) = implicitly[ClassTag[NodeType]].runtimeClass == node.getClass
  protected def castNode(node: Node[_]): NodeType = implicitly[ClassTag[NodeType]].runtimeClass.asInstanceOf[Class[NodeType]].cast(node)

  protected def checkConnector(connector: NodeConnector[_, _]) = implicitly[ClassTag[ConnectorType]].runtimeClass == connector.getClass
  protected def castConnector(connector: NodeConnector[_, _]): ConnectorType = implicitly[ClassTag[ConnectorType]].runtimeClass.asInstanceOf[Class[ConnectorType]].cast(connector)

  def handleRequestImpl: PartialFunction[ServiceRequest, Future[Any]]

  override protected def handleRequest(e: ServiceRequest): Future[Any] = handleRequestImpl(e)

  override protected def started(): Unit = { registerService {
    case req @ (_: ServiceRequest with Broadcast | _: ServiceRequest with ProtocolInternal) => handleRequestImpl isDefinedAt req
  }}
}

object AbstractService {
  trait ServiceResponse
  case object Done extends ServiceResponse
  case object NotFound extends ServiceResponse
  case class InvalidParams(message: String) extends ServiceResponse
  case class NotAcceptable(message: String) extends ServiceResponse
  case class Conflict(message: String) extends ServiceResponse
}