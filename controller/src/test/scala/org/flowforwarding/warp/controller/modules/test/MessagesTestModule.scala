/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.test

import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, TimeoutException}

import spire.math.{UByte, ULong}

import org.flowforwarding.warp.controller.SwitchConnector.{MultipartMessageSwitchResponse, SingleMessageSwitchResponse, SendingResult, SwitchResponse}
import org.flowforwarding.warp.controller.api.dynamic.DynamicStructure
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.bus.ControllerBus
import org.flowforwarding.warp.controller.driver_interface.{MessageDriverFactory, OfpFeaturesExtractor}
import org.flowforwarding.warp.controller.modules.Module
import org.flowforwarding.warp.controller.util.NonCachingClassLoader

class MessagesTestModule[DriverType <: OfpFeaturesExtractor[DynamicStructure] with MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _]]]
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
      val f1: OnResponse  = {
        case SingleMessageSwitchResponse(msg) =>
          val fail: PartialFunction[FixedOfpMessage, Boolean] = {
            case _ => false
          }
          ((tr.test orElse fail)(msg), "Test response of type " + msg.getClass.getName)
        case MultipartMessageSwitchResponse(msgs) =>
          (false, "Testing of multipart messages is not implemented yet")
      }
      val f2: OnFailure = {
        case t => (false, "Failure: " + t.getMessage + "\n" + t.getStackTrace.mkString("\n"))
      }
      (f1, f2)

    case tnr: MessageTestsSet[DriverType]#TestNoError =>
      val f1: OnResponse = {
        case SingleMessageSwitchResponse(msg) =>
          val error = scala.util.Try(tnr.errorClass cast msg).isSuccess
          (false, (if (error) "Error received: " else "Unexpected message received: ") + msg)
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
        Future.fold(results)(s"[MSG TEST] --- START TESTS (${ts.size}) ---\n\n")(_ + _) foreach println
      case Failure(t) =>
        println("[MSG TEST] Unable to start tests \n" +  t.getMessage + "\n" + t.getStackTrace.mkString("\n"))
    }
  }

  override protected def compatibleWith(factory: MessageDriverFactory[_]): Boolean = true // TODO
}
