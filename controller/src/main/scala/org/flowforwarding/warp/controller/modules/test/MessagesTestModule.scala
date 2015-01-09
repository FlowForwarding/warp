/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.test

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, TimeoutException}
import scala.util.{Failure, Success, Try}

import spire.math.{UByte, ULong}

import org.flowforwarding.warp.driver_api._
import org.flowforwarding.warp.driver_api.dynamic.DynamicStructure
import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.fixed.test.MessageTestsSet

import org.flowforwarding.warp.controller.SwitchConnector._
import org.flowforwarding.warp.controller.bus.{FixedStructuresSender, ControllerBus}
import org.flowforwarding.warp.controller.modules.Module
import org.flowforwarding.warp.controller.util.NonCachingClassLoader

class MessagesTestModule[DriverType <: OfpFeaturesExtractor[DynamicStructure] with MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _]]]
  (val bus: ControllerBus, testsSetClass: String, id: String, v: String) extends Module with FixedStructuresSender {

  implicit val classLoader = this.getClass.getClassLoader
  val testsSet = new NonCachingClassLoader(s => s.endsWith("Tests") || s.endsWith("Test") || s.contains("Test$"))
                    .loadClass(testsSetClass)
                    .newInstance()
                    .asInstanceOf[MessageTestsSet[DriverType]]
  val version = UByte(v.toInt)
  val dpid = ULong(id)

  type OnResponse = PartialFunction[SendingResult[FixedOfpMessage], (Boolean, String)]
  type OnFailure =  PartialFunction[Throwable, (Boolean, String)]

  def testFunctions(data: MessageTestsSet[DriverType]#TestData) = data match {
    case tr: MessageTestsSet[DriverType]#TestResponse =>
      val fail: PartialFunction[FixedOfpMessage, Boolean] = {
        case _ => false
      }
      val f1: OnResponse  = {
        case ErrorSwitchResponse(msg) =>
          val testError = (tr.test orElse fail)(msg)
          (testError, if (testError) "Expected error received" else "Unexpected error received")
        case SingleMessageSwitchResponse(msg) =>
          ((tr.test orElse fail)(msg), "Test single message response of type " + msg.getClass.getName)
        case MultipartMessageSwitchResponse(msgs) =>
          (msgs forall (tr.test orElse fail), "Testing of multipart message responses: " + msgs)
      }
      val f2: OnFailure = {
        case t => (false, "Failure: " + t.getMessage + "\n" + t.getStackTrace.mkString("\n"))
      }
      (f1, f2)

    case tnr: MessageTestsSet[DriverType]#TestNoError =>
      val f1: OnResponse = {
        case ErrorSwitchResponse(msg) =>
          (false, "Error received: " + msg)
        case SingleMessageSwitchResponse(msg) =>
          (false, "Unexpected message received: " + msg)
        case MultipartMessageSwitchResponse(msgs) =>
          (false, "Unexpected multipart response received: " + msgs)
      }
      val f2: OnFailure = {
        case t: TimeoutException => (true, "No error response")
        case t: Throwable => (false, "Unexpected exception: " + t.getMessage + "\n" + t.getStackTrace.mkString("\n"))
      }
      (f1, f2)
  }

  override def started() = {
    Try { testsSet.tests } match {
      case Success(ts) =>
        val results = ts map { case (input, testData) =>
          val (mapResponse, mapError) = testFunctions(testData)
          sendBuilderInput(version, dpid, input, true) map mapResponse recover mapError map {
            case (succeed, report) =>
              s"[MSG TEST] Test input $input\n" +
              s"[MSG TEST] $report\n" +
              s"[MSG TEST] TEST ${if (succeed) "SUCCEED" else "FAILED"} [${testData.description}].\n\n"
            }
          }
        Future.fold(results)(s"[MSG TEST] --- START TESTS (${ts.size}) ---\n\n")(_ + _) foreach log.info
      case Failure(t) =>
        log.info("[MSG TEST] Unable to start tests \n" +  t.getMessage + "\n" + t.getStackTrace.mkString("\n"))
    }
  }

  override protected def compatibleWith(factory: MessageDriverFactory[_]): Boolean = true // TODO
}
