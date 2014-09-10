/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import spire.math.UInt

import com.gensler.scalavro.types.supply._

import org.flowforwarding.warp.protocol.ofp13.structures.BufferId.OFP_BUFFER_ID
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_port_no.OFP_PORT_NO
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_length._
import org.flowforwarding.warp.protocol.dynamic.DynamicPath

object BufferId extends DWordEnum with AllowUnspecifiedValues[Int]{
  type OFP_BUFFER_ID = Value
  val OFP_NO_BUFFER = ##(0xffffffffL)
}
/* Send packet (controller -> datapath). */
case class ofp_packet_out private[protocol] (
  header: ofp_header,
  buffer_id: OFP_BUFFER_ID,    /* ID assigned by datapath ( if none). */
  in_port: OFP_PORT_NO,        /* Packet's input port or OFPP_CONTROLLER. */
  actions_len: UInt16,         /* Size of action array in bytes. */
  pad: Pad6,
  actions: RawSeq[ofp_action], /* Action list - 0 or more. */
  /* The variable size action list is optionally followed by packet data.
   * This data is only present and meaningful if buffer_id == -1. */
  data: RawSeq[UInt8])         /* Packet data. The length is inferred from the length field in the header. */

object ofp_packet_out extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = {
    case 5 => ???
    case 6 => ???
  } // TODO: Implement

  private[protocol] def build(@DynamicPath("header", "xid") xid: UInt32,
                              buffer_id: OFP_BUFFER_ID,
                              in_port: OFP_PORT_NO,
                              actions: RawSeq[ofp_action] = RawSeq(),
                              data: RawSeq[UInt8] = RawSeq()) = {
    val actionsLen = actions.foldLeft(0) { _ + _.len.data }
    val header = ofp_header(OFPL_PACKET_OUT_LEN + data.length + actionsLen, xid)
    ofp_packet_out(header, buffer_id, in_port, UInt16.fromShort(actionsLen.toShort), Pad6(), actions, data)
  }

  def apply(xid: Int,
            buffer_id: OFP_BUFFER_ID,
            in_port: OFP_PORT_NO,
            actions: Array[ofp_action] = Array(),
            data: Array[Byte] = Array()): ofp_packet_out =
    build(UInt32.fromInt(xid), buffer_id, in_port, RawSeq(actions: _*), RawSeq(data map UInt8.fromByte: _*))
}