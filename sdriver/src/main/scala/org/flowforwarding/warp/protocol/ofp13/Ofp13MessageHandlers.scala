/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13

import scala.util.Try

import org.flowforwarding.warp.protocol.ofp13.structures._
import org.flowforwarding.warp.controller.driver_interface.MessageHandlers
import org.flowforwarding.warp.controller.bus.ControllerBus
import spire.math.{ULong, UByte}
import org.flowforwarding.warp.protocol.ofp13.structures.ofp13_messages.Ofp13Msg

abstract class Ofp13MessageHandlers(bus: ControllerBus) extends MessageHandlers[Ofp13Msg, Ofp13Impl](bus) {

  def supportedVersions: Array[UByte] =
    Array(UByte(4))

  def handleMessage(driver: Ofp13Impl, dpid: ULong, msg: Ofp13Msg): Try[Array[Ofp13Msg]] = Try {
    msg.structure match {
      case m: ofp_switch_features_reply =>
        featuresReply(dpid, m)
      case m: ofp_error_msg =>
        error(dpid, m)
      case m: echo_request =>
        echoRequest(dpid, m)
      case m: echo_reply =>
        echoReply(dpid, m)
      case m: ofp_switch_config_reply =>
        switchConfig(dpid, m)
      case m: ofp_packet_in =>
        packetIn(dpid, m)
      case unknown => throw new Exception("Unknown message type: " + unknown)
    }
  }

  def featuresReply(dpid: ULong, reply: ofp_switch_features_reply): Array[Ofp13Msg] = Array()
  def error(dpid: ULong, error: ofp_error_msg): Array[Ofp13Msg] = Array()
  def packetIn(dpid: ULong, pIn: ofp_packet_in): Array[Ofp13Msg] = Array()
  def switchConfig(dpid: ULong, config: ofp_switch_config_reply): Array[Ofp13Msg] = Array()
  def echoRequest(dpid: ULong, echoReq: echo_request): Array[Ofp13Msg] = Array()
  def echoReply(dpid: ULong, echoReq: echo_reply): Array[Ofp13Msg] = Array()
}