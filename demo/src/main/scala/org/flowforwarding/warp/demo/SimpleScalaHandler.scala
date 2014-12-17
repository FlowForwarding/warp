/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.demo


import org.flowforwarding.warp.driver_api.XidGenerator

import scala.language.dynamics

import spire.math.{ULong, UByte}

import org.flowforwarding.warp.controller._
import org.flowforwarding.warp.controller.message_handlers.dynamic.scala_api._
import org.flowforwarding.warp.controller.bus.ControllerBus
import org.flowforwarding.warp.controller.SwitchConnector.SwitchIncomingMessage

class SimpleScalaHandler(bus: ControllerBus) extends DynamicMessageHandlers(bus) {

  def started() = subscribe("incomingMessage") { case me: SwitchIncomingMessage[_] => true }

  def supportedVersions = Array(UByte(4))

  private implicit class BuilderExt(b: Builder){
    def messageInputs = new Dynamic {
      def applyDynamic(s: String)(xid: Long) = {
        val header = b.ofp_header
        header.xid = xid

        val message = b.selectDynamic(s)
        message.header = header
        message
      }
    }
  }

  def handleMessage(api: Builder, dpid: ULong, msg: Structure): Array[Input] = {
    if (msg.?.ofp_switch_features_reply) {
      System.out.println("DPID from dynamic message: " + msg.->.datapathId)

      val request = api.messageInputs.echo_request(XidGenerator.nextXid())
      request.elements = Array[Long](2, 2, 2, 2, 2)

      Array[Input](request)
    }
    else if (msg.?.echo_reply) {
      System.out.println("[OF-INFO] DPID: " + dpid + " Length of echo reply: " + msg.-->.elements.length)
      Array.empty
    }
    else Array.empty
  }
}


