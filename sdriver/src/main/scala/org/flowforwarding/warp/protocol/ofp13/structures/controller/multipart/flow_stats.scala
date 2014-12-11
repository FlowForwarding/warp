/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_flow_mod_flags.OFP_FLOW_MOD_FLAGS
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_multipart_type._
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_port_no.OFP_PORT_NO

case class ofp_flow_stats_request private [protocol](
  table_id: UInt8, /* ID of table to read (from ofp_table_stats), OFPTT_ALL for all tables. */
  pad: Pad3, /* Align to 32 bits. */
  out_port: OFP_PORT_NO, /* Require matching entries to include this  as an output port. A value of OFPP_ANY indicates no restriction. */
  out_group: UInt32, /* Require matching entries to include this as an output group. A value of OFPG_ANY indicates no restriction. */
  pad2: Pad4, /* Align to 64 bits. */
  cookie: UInt64, /* Require matching entries to contain this cookie value */
  cookie_mask: UInt64, /* Mask used to restrict the cookie bits that must match. A value of 0 indicates no restriction. */
  `match`: ofp_match /* Fields to match. Variable size. */
)

object ofp_flow_stats_request{
  private[protocol] def build(table_id: UInt8,
                              out_port: OFP_PORT_NO,
                              out_group: UInt32,
                              cookie: UInt64,
                              cookie_mask: UInt64,
                              `match`: ofp_match): ofp_flow_stats_request = {
    ofp_flow_stats_request(table_id, Pad3(), out_port, out_group, Pad4(), cookie, cookie_mask, `match`)
  }

  def apply(table_id: UInt8,
                              out_port: OFP_PORT_NO,
                              out_group: UInt32,
                              cookie: UInt64,
                              cookie_mask: UInt64,
                              `match`: ofp_match) = {
    build(table_id, out_port, out_group, cookie, cookie_mask, `match`)
  }
}

/* Body of reply to OFPMP_FLOW request. */
case class ofp_flow_stats(
  length: UInt16, /* Length of this entry. */
  table_id: UInt8, /* ID of table flow came from. */
  pad: Pad1,
  duration_sec: UInt32, /* Time flow has been alive in seconds. */
  duration_nsec: UInt32, /* Time flow has been alive in nanoseconds beyond duration_sec. */
  priority: UInt16, /* Priority of the entry. */
  idle_timeout: UInt16, /* Number of seconds idle before expiration. */
  hard_timeout: UInt16, /* Number of seconds before expiration. */
  flags: OFP_FLOW_MOD_FLAGS, /* Bitmap of OFPFF_* flags. */
  pad2: Pad4, /* Align to 64-bits. */
  cookie: UInt64, /* Opaque controller-issued identifier. */
  packet_count: UInt64, /* Number of packets in flow. */
  byte_count: UInt64, /* Number of bytes in flow. */
  `match`: ofp_match, /* Description of fields. Variable size. */
  /* The variable size and padded match is always followed by instructions. */
  instructions: RawSeq[ofp_instruction] /* Instruction set - 0 or more. */
)

object ofp_flow_stats extends RawSeqFieldsInfo {
  override val rawFieldsLengthCalculator: ofp_flow_stats.LengthCalculator = {
    case 14 => Some { (s: Seq[Any]) => lenField(0)(s) - (56 + (-8)  // length of match is calculated manually
                                                            + ofp_match.totalLength(s(13).asInstanceOf[ofp_match])) }
  }
}

case class ofp_multipart_flow_stats_request private [protocol] (structure: ofp_flow_stats_request) extends MultipartRequest[ofp_flow_stats_request] {
  override def tp: OFP_MULTIPART_TYPE = ofp_multipart_type.OFPMP_FLOW

  override def structures = Seq(structure)
}

object ofp_multipart_flow_stats_request {
  private [protocol] def build(structure: ofp_flow_stats_request) = ofp_multipart_flow_stats_request(structure)
}

case class ofp_multipart_flow_stats_reply private [protocol] (stats: RawSeq[ofp_flow_stats])