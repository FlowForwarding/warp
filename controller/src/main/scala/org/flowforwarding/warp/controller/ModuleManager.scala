/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller

import java.util.concurrent.TimeUnit
import java.net.InetSocketAddress

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor._
import akka.io.{Tcp, TcpMessage}
import akka.io.Tcp.Bound
import akka.util.Timeout
import akka.pattern.{ask, pipe}

import spire.math.UByte

import org.flowforwarding.warp.controller.driver_interface._
import org.flowforwarding.warp.controller.modules.Module.CheckCompatibility
import org.flowforwarding.warp.controller.bus.{ControllerBusActor, ControllerBus, ServiceRequest}
import org.flowforwarding.warp.controller.modules.Module
import org.flowforwarding.warp.controller.SwitchConnector.NewDriverFactory
import org.flowforwarding.warp.controller.util.NonCachingClassLoader

object ModuleManager {
  case class Start(address: InetSocketAddress)
  case class Started(address: InetSocketAddress, failure: Option[Throwable])

  case class SetFactory(factoryClass: String, args: Array[String])
  case class SetFactoryResponse(failure: Option[Throwable])

  case class AddModule(moduleName: String, moduleClass: String, args: Array[String]) extends ServiceRequest
  case class AddModuleResponse(moduleName: String, failure: Option[Throwable])

  case class RemoveModule(moduleName: String) extends ServiceRequest
  case class RemoveModuleResponse(moduleName: String, failure: Option[Throwable])

  case class DriverByVersion(version: UByte) extends ServiceRequest
  case class DriverByHello(helloMessage: Array[Byte]) extends ServiceRequest
  case class DriverFoundResponse(driver: MessageDriver[_ <: OFMessage], supportedVersions: Array[UByte])
  case class DriverNotFoundResponse(errorData: Array[Byte], rejectedVersions: Array[UByte])

  def main(args: Array[String]){
    implicit val actorSystem = ActorSystem.create("OfController")
    val manager = Tcp.get(actorSystem).manager
    val controllerBus = new ControllerBus { }
    val controller = actorSystem.actorOf(Props.create(classOf[ModuleManager], controllerBus, manager), "Controller-Dispatcher")
    actorSystem.actorOf(Props.create(classOf[InputHandler], controller), "Input-Handler")
  }
}


private class ModuleManager(val bus: ControllerBus, manager: ActorRef) extends ControllerBusActor {
  import org.flowforwarding.warp.controller.ModuleManager._

  type T <: OFMessage
  type DriverType <: MessageDriver[T]

  implicit val timeout = Timeout(2, TimeUnit.SECONDS)

  // change to become
  var driverFactory: MessageDriverFactory[T] = null
  val modules = scala.collection.mutable.Map.empty[String, ActorRef]

  def tryConstruct[T: ClassTag](cls: String, args: IndexedSeq[IndexedSeq[AnyRef]]): Try[T] = {
    Try { Class.forName(cls) } flatMap { c =>
      val obj = args.map(a => c.getConstructors.collectFirst {
        case const if const.getParameterTypes.length == a.length &&
                     (const.getParameterTypes zip a forall { case (ct, aa) => ct.isAssignableFrom(aa.getClass) }) => (const, a)
      }).collectFirst {
        //case Some((constructor, a)) if a.isEmpty => constructor.newInstance()
        case Some((constructor, a)) => constructor.newInstance(a: _*)
      }
      Try { implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]].cast(obj.get) }
    }
  }

  override def preStart(): Unit = {
    super.preStart()
    registerService { case _: AddModule | _: RemoveModule => true }
  }

  def receive = {

    case msg: Tcp.Bound =>
      manager ! msg

    case Tcp.CommandFailed =>
      context stop self

    case c: Tcp.Connected =>
      manager ! c
      println("[INFO] Getting Switch connection \n")
      val s = sender()
      val connectionHandler = context.actorOf(Props.create(classOf[SwitchConnector[T, DriverType]], bus, self))
      connectionHandler ? CheckCompatibility(driverFactory) onComplete {
        case Success(true) =>
          s ! TcpMessage.register(connectionHandler)
        case _ =>
          context stop connectionHandler
      }

    case Start(address) =>
      manager ? TcpMessage.bind(self, address, 100) map {
        case Bound(a) =>
          Started(a, None)
        case _ =>
          Started(address, Some(new Exception("Unable to bind address " + address + ". It is probably already in use.")))
      } pipeTo sender
      registerService { case _: DriverByVersion | _: DriverByHello | _: AddModule | _: RemoveModule => true }

    case DriverByVersion(version) =>
      sender ! DriverFoundResponse(driverFactory.get(version), driverFactory.supportedVersions)
      
    case DriverByHello(helloMessage) =>
      driverFactory.highestCommonVersion(helloMessage) match {
        case Left(version) =>
          sender ! DriverFoundResponse(driverFactory.get(version), driverFactory.supportedVersions)
        case Right((versions, error)) =>
          sender ! DriverNotFoundResponse(error, versions)
      }

    case SetFactory(factoryClass, args) =>
      tryConstruct[MessageDriverFactory[T]](
        factoryClass,
        IndexedSeq(IndexedSeq(args.toArray), IndexedSeq()))
      match {
        case Success(f) if driverFactory == null =>
          driverFactory = f
          publishMessage(NewDriverFactory(self))
          sender ! SetFactoryResponse(None)
        case Success(_) =>
          sender ! SetFactoryResponse(Some(new Exception("Driver factory is already set")))
        case Failure(t) =>
          sender ! SetFactoryResponse(Some(t))
      }

    case AddModule(moduleName, moduleClass, args) =>
      val req = sender()
      Try {
        implicit val parentCl = this.getClass.getClassLoader
        val moduleLoader = new NonCachingClassLoader(_.startsWith(moduleClass))
        context.actorOf(Props.create(moduleLoader loadClass moduleClass, bus +: args: _*), "Module-" + moduleName)
      } match {
        case Success(module) if driverFactory == null =>
          module ! Module.Shutdown
          req ! AddModuleResponse(moduleName, Some(new Exception("Driver factory must be set before adding of any module")))
        case Success(module) =>
          module ? CheckCompatibility(driverFactory) onComplete {
            case Success(true) =>
              req ! AddModuleResponse(moduleName, None)
              modules(moduleName) = module
            case Success(false) =>
              req ! AddModuleResponse(moduleName, Some(new Exception("Module is not compatible with drivers factory")))
              module ! Module.Shutdown
            case Success(_) =>
              req ! AddModuleResponse(moduleName, Some(new Exception("Wrong compatibility response")))
              module ! Module.Shutdown
            case Failure(t) =>
              req ! AddModuleResponse(moduleName, Some(t))
              context stop module
          }
        case Failure(t) =>
          req ! AddModuleResponse(moduleName, Some(t))
      }

    case RemoveModule(moduleName) =>
      val req = sender()
      modules.remove(moduleName) match {
        case Some(m) =>
          m ! Module.Shutdown
          req ! RemoveModuleResponse(moduleName, None)
        case None =>
          req ! RemoveModuleResponse(moduleName, Some(new Exception("Module is not loaded")))
      }
  }
}
