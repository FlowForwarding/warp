/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_multipart_type._
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_port_no.OFP_PORT_NO

case class ofp_aggregate_stats_request private [sdriver](
  table_id: UInt8, /* ID of table to read (from ofp_table_stats), OFPTT_ALL for all tables. */
  pad: Pad3, /* Align to 32 bits. */
  out_port: OFP_PORT_NO, /* Require matching entries to include this  as an output port. A value of OFPP_ANY indicates no restriction. */
  out_group: UInt32, /* Require matching entries to include this as an output group. A value of OFPG_ANY indicates no restriction. */
  pad2: Pad4, /* Align to 64 bits. */
  cookie: UInt64, /* Require matching entries to contain this cookie value */
  cookie_mask: UInt64, /* Mask used to restrict the cookie bits that must match. A value of 0 indicates no restriction. */
  `match`: ofp_match /* Fields to match. Variable size. */
)

object ofp_aggregate_stats_request{
  private[sdriver] def build(table_id: UInt8,
                              out_port: OFP_PORT_NO,
                              out_group: UInt32,
                              cookie: UInt64,
                              cookie_mask: UInt64,
                              `match`: ofp_match): ofp_aggregate_stats_request = {
    ofp_aggregate_stats_request(table_id, Pad3(), out_port, out_group, Pad4(), cookie, cookie_mask, `match`)
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

/* Body of reply to OFPMP_AGGREGATE request. */
case class ofp_aggregate_stats(
  packet_count: UInt64, /* Number of packets in flows. */
  byte_count: UInt64, /* Number of bytes in flows. */
  flow_count: UInt32, /* Number of flows. */
  pad: Pad4)

case class ofp_multipart_aggregate_stats_request private [sdriver] (structure: ofp_aggregate_stats_request) extends MultipartRequest[ofp_aggregate_stats_request] {
  override def tp: OFP_MULTIPART_TYPE = ofp_multipart_type.OFPMP_AGGREGATE

  override def structures = Seq(structure)
}

object ofp_multipart_aggregate_stats_request {
  private [sdriver] def build(structure: ofp_aggregate_stats_request) = ofp_multipart_aggregate_stats_request(structure)
}

case class ofp_multipart_aggregate_stats_reply private [sdriver] (stats: ofp_aggregate_stats)