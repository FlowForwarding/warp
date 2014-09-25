/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.managers

import org.flowforwarding.warp.controller.bus.{ServiceRequest, ControllerBus}
import org.flowforwarding.warp.controller.driver_interface.MessageDriverFactory
import org.flowforwarding.warp.controller.modules.managers.sal.{NodeConnector, Node}
import org.flowforwarding.warp.controller.modules.{MessageConsumer, Service}

import scala.concurrent.Future
import scala.reflect.ClassTag

trait Broadcast

trait NodeTag[NodeType <: Node[_], ConnectorType <: NodeConnector[_, NodeType]] {
  protected implicit val nodeTag: ClassTag[NodeType]
  protected implicit val connectorTag: ClassTag[ConnectorType]
}

abstract class AbstractManager[R <: ServiceRequest: ClassTag] extends Service{

  override protected def compatibleWith(factory: MessageDriverFactory[_]): Boolean = true

  override def started() = { registerService {
    case req => implicitly[ClassTag[R]].runtimeClass.isAssignableFrom(req.getClass) && !req.isInstanceOf[Broadcast]
  }}

  import AbstractService._

  protected def reduceServiceResponses(iterable: Array[Any]) =
    if (iterable contains Done) Done
    else if (iterable forall { _ == NodeNotFound }) NodeNotFound
    else if (iterable forall { _ == NotAcceptable }) NotAcceptable
    else if (iterable forall { _ == Conflict }) Conflict
    else InvalidParams
}

trait AbstractService[NodeType <: Node[_], ConnectorType <: NodeConnector[_, NodeType]] extends Service with MessageConsumer {
  tag: NodeTag[NodeType, ConnectorType]  =>
  protected val bus: ControllerBus

  protected def checkNode(node: Node[_]) = implicitly[ClassTag[NodeType]].runtimeClass == node.getClass
  protected def castNode(node: Node[_]): NodeType = implicitly[ClassTag[NodeType]].runtimeClass.asInstanceOf[Class[NodeType]].cast(node)

  protected def checkConnector(connector: NodeConnector[_, _]) = implicitly[ClassTag[ConnectorType]].runtimeClass == connector.getClass
  protected def castConnector(connector: NodeConnector[_, _]): ConnectorType = implicitly[ClassTag[ConnectorType]].runtimeClass.asInstanceOf[Class[ConnectorType]].cast(connector)

  val handleRequestImpl: PartialFunction[ServiceRequest, Future[Any]]

  override protected def handleRequest(e: ServiceRequest): Future[Any] = handleRequestImpl(e)

  override protected def started(): Unit = { registerService {
    case req: ServiceRequest with Broadcast => handleRequestImpl isDefinedAt req
  }}
}

object AbstractService {
  trait ServiceResponse
  case object Done extends ServiceResponse
  case object NodeNotFound extends ServiceResponse
  case object InvalidParams extends ServiceResponse
  case object NotAcceptable extends ServiceResponse
  case object Conflict extends ServiceResponse
}