/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_multipart_type._

/* Body of reply to OFPMP_TABLE request. */
case class ofp_table_stats private [sdriver](
  table_id: UInt8, /* Identifier of table. Lower numbered tables are consulted first. */
  pad: Pad3, /* Align to 32-bits. */
  active_count: UInt32, /* Number of active entries. */
  lookup_count: UInt64, /* Number of packets looked up in table. */
  matched_count: UInt64 /* Number of packets that hit table. */
)

//object ofp_table_stats{
//  def build(table_id: UInt8,
//            active_count: UInt32,
//            lookup_count: UInt64,
//            matched_count: UInt64) = {
//    ofp_table_stats(table_id, Pad3(), active_count, lookup_count, matched_count)
//  }
//}


case class ofp_multipart_table_stats_request private [sdriver] () extends EmptyMultipartRequest {
  override def tp: OFP_MULTIPART_TYPE = ofp_multipart_type.OFPMP_TABLE
}

object ofp_multipart_table_stats_request {
  private [sdriver] def build() = ofp_multipart_table_stats_request()
}

case class ofp_multipart_table_stats_reply private [sdriver] (stats: RawSeq[ofp_table_stats])