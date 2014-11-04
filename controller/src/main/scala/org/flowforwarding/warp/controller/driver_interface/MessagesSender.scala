/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.driver_interface

import spire.math.{ULong, UByte}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.flowforwarding.warp.controller.ModuleManager.{DriverFoundResponse, DriverByVersion}
import org.flowforwarding.warp.controller.SwitchConnector.{SendToSwitch, SendingResult}
import org.flowforwarding.warp.controller.bus.ServiceBusActor

trait MessagesSender[M <: OFMessage] extends ServiceBusActor{
  def sendToSwitch[T, R](version: UByte, dpid: ULong, value: T, needReply: Boolean)(
    implicit toMessage: MessageDriver[_] => T => M,
             fromMessage: MessageDriver[_] => M => R): Future[SendingResult[R]] = {
    askFirst(DriverByVersion(version)) flatMap {
      case DriverFoundResponse(driver, _) =>
        sendToSwitch(dpid, toMessage(driver)(value), needReply) map {
          _ map fromMessage(driver)
        }
    }
  }

  def sendToSwitch(dpid: ULong, value: M, needReply: Boolean): Future[SendingResult[M]] = {
    askFirst(SendToSwitch(dpid, value, needReply)).mapTo[SendingResult[M]]
  }
}