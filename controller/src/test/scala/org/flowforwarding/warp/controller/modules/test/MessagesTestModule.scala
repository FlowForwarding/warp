/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.test

import java.util.concurrent.TimeUnit

import scala.util.{Failure, Success, Try}
import scala.concurrent.{Await, TimeoutException, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

import org.flowforwarding.warp.controller._
import org.flowforwarding.warp.controller.api.dynamic.{DynamicDriver, DynamicStructure}
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.driver_interface.{OfpFeaturesExtractor, MessageDriver, MessageDriverFactory}
import org.flowforwarding.warp.controller.util.ClassReloader
import spire.math.{UInt, UByte, ULong}
import org.flowforwarding.warp.controller.bus.{MessageEnvelope, ControllerBus, ServiceBusActor}
import org.flowforwarding.warp.controller.SwitchConnector.{SwitchOutgoingMessage, MessageSendingResult, SwitchIncomingMessage}
import org.flowforwarding.warp.controller.ModuleManager.{DriverFoundResponse, DriverByVersion}
import org.flowforwarding.warp.controller.modules.MessageConsumer

class MessagesTestModule[DriverType <: OfpFeaturesExtractor[DynamicStructure] with MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _]]]
  (val bus: ControllerBus, testsSetClass: String, id: String, v: String) extends ServiceBusActor with MessageConsumer {

  type Response = DriverType#OfpMessage[_]

  trait ResponseTest{
    def test(response: Try[Response]): (Boolean, String)
  }

  case class EnsureNoError(errorClass: Class[_]) extends ResponseTest{
    def test(response: Try[Response]) = response match {
      case Success(m) if Try { errorClass cast m } isSuccess =>
        (false, "Error received: " + m)
      case Success(r) =>
        (false, "Unexpected message received: " + r)
      case Failure(t: TimeoutException) => (true, "No error response")
      case Failure(t) => (false, "Unexpected exception: " + t.getMessage + "\n" + t.getStackTrace.mkString("\n"))
    }
  }

  case class ValidateResponse(predicate: PartialFunction[Response, Boolean]) extends ResponseTest{
    private val fail: PartialFunction[Response, Boolean] = { case _ => false }

    def test(response: Try[Response]) = response match {
      case Success(m) => ((predicate orElse fail)(m), "Test response of type " + m.getClass.getName)
      case Failure(t) => (false, "Failure: " + t.getMessage + "\n" + t.getStackTrace.mkString("\n"))
    }
  }

  implicit val classLoader = this.getClass.getClassLoader
  val testsSet = new ClassReloader(s => s.endsWith("Tests") || s.endsWith("Test") || s.contains("Test$")).loadClass(testsSetClass).newInstance().asInstanceOf[MessageTestsSet[DriverType]]
  val version = UByte(v.toInt)
  val dpid = ULong(id)

  val awaitingResponses = scala.collection.mutable.Map[UInt, Promise[Response]]()

  override def started() = {
    subscribe("incomingResponses") { case SwitchIncomingMessage(`dpid`, _, _) => true }
    subscribe("failedResponses") { case MessageSendingResult(`dpid`, _, Some(_)) => true }
    askFirst(DriverByVersion(version)) map {
      case response: DriverFoundResponse =>
        val driver = response.driver.asInstanceOf[DriverType]
        Try { testsSet.tests } match {
          case Success(t) => Future.fold(testMessages(driver, t))("[MSG TEST] --- START TESTS ---\n\n")(_ + _) foreach println
          case Failure(t) => println("[MSG TEST] Unable to start tests \n" +  t.getMessage + "\n" + t.getStackTrace.mkString("\n"))
        }

    }
  }

  private def testMessages(driver: DriverType, tests: Map[DriverType#MessageInput, MessageTestsSet[DriverType]#TestData]) =
    tests map { case (input, testData) =>
      val f = driver.buildDynamic(input) match {
        case Success(msg) =>
          val messageTest = testData match {
            case rt: MessageTestsSet[DriverType]#TestResponse => ValidateResponse(rt.test)
            case nrt: MessageTestsSet[DriverType]#TestNoError => EnsureNoError(nrt.errorClass)
          }

          val p = Promise[Response]()
          awaitingResponses(driver.getXid(msg)) = p
          publishMessage(SwitchOutgoingMessage(dpid, msg))
          Future { messageTest test { Try { Await.result(p.future, Duration(7, TimeUnit.SECONDS)) }}}
        case Failure(t) =>
          Future.successful((false, s"Unable to build structure ${input.getClass.getName}: ${t.getMessage}.\n" + t.getStackTrace.mkString("\n")))
      }
      f map { case (succeed, report) =>
        s"[MSG TEST] Test input $input\n" +
        s"[MSG TEST] $report\n" +
        s"[MSG TEST] TEST ${if (succeed) "SUCCEED" else "FAILED"} [${testData.description}].\n\n"
      }
    }

  protected def handleEvent(e: MessageEnvelope): Unit = e match {
    case m @ SwitchIncomingMessage(`dpid`, driver: MessagesDescriptionHelper[_], msg: DynamicStructure) =>
      val xid = driver.getXid(msg)
      awaitingResponses.remove(xid) foreach {
        val response: Try[Response] =
          driver.toConcreteMessage(msg).flatMap[Response] { x => Try { x.asInstanceOf[Response] } }
        _ complete response
      }
    case m @ MessageSendingResult(`dpid`, xid, Some(t)) =>
      awaitingResponses.remove(xid) foreach {
        _ failure t
      }
  }

  override protected def compatibleWith(factory: MessageDriverFactory[_]): Boolean = true // TODO
}
