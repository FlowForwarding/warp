/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.demo

import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_packet_in
import spire.math.ULong
import com.gensler.scalavro.types.supply.{UInt32, UInt8, RawSeq}

import org.flowforwarding.warp.sdriver.OfpMsg
import org.flowforwarding.warp.sdriver.ofp13._
import org.flowforwarding.warp.sdriver.ofp13.structures._
import org.flowforwarding.warp.driver_api.XidGenerator
import org.flowforwarding.warp.controller.SwitchConnector.SwitchIncomingMessage
import org.flowforwarding.warp.controller.bus.ControllerBus

class LowLevelHandlers(bus: ControllerBus) extends Ofp13MessageHandlers(bus){

  override def started() = {
     subscribe("incomingMessages") { case SwitchIncomingMessage(_, driver, _) => driver.isInstanceOf[Ofp13Impl] }
  }

  override def featuresReply(dpid: ULong, reply: ofp_switch_features_reply) = {
    println("[OF-INFO] DPID: " + dpid + " Switch Features is received from the Switch ")
    Array(OfpMsg(echo_request(XidGenerator.nextXid().toInt, Array[Byte](2, 2, 2, 2, 2))))
  }

  override def error(dpid: ULong, error: ofp_error_msg) = {
    println(s"[OF-INFO] DPID: $dpid error: $error")
    Array()
  }

  override def packetIn(dpid: ULong, pIn: ofp_packet_in)= {
    println(s"[OF-INFO] DPID: $dpid packetIn: $pIn")
    Array()
  }

  override def switchConfig(dpid: ULong, config: ofp_switch_config_reply) = {
    println(s"[OF-INFO] DPID: $dpid Configuration: $config")
    Array()
  }

  override def echoRequest(dpid: ULong, echoReq: echo_request) = {
    println(s"[OF-INFO] DPID: $dpid Echo request: $echo_request")
    Array()
  }

  override def echoReply(dpid: ULong, echoReply: echo_reply) = {
    println(s"[OF-INFO] DPID: $dpid Echo reply: ${echoReply.elements}")
    Array()
  }
}