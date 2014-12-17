/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_multipart_type._

case class ofp_group_stats_request private[sdriver] (
  group_id: UInt32, /* All groups if OFPG_ALL. */
  pad: Pad4)

object ofp_group_stats_request{
  def build(group_id: UInt32) = ofp_group_stats_request(group_id, Pad4())
}

case class ofp_group_stats(length: UInt16, /* Length of this entry. */
                           pad: Pad2, /* Align to 64 bits. */
                           group_id: UInt32, /* Group identifier. */
                           ref_count: UInt32, /* Number of flows or groups that directly forward to this group. */
                           pad2: Pad4, /* Align to 64 bits. */
                           packet_count: UInt64, /* Number of packets processed by group. */
                           byte_count: UInt64, /* Number of bytes processed by group. */
                           duration_sec: UInt32, /* Time queue has been alive in seconds. */
                           duration_nsec: UInt32, /* Time queue has been alive in nanoseconds beyond duration_sec. */
                           bucket_stats: RawSeq[ofp_bucket_counter] /* One counter set per bucket. */
)

object ofp_group_stats extends RawSeqFieldsInfo{
//  private[sdriver] def build(length: UInt16,
//                              group_id: UInt32,
//                              ref_count: UInt32,
//                              packet_count: UInt64,
//                              byte_count: UInt64,
//                              duration_sec: UInt32,
//                              duration_nsec: UInt32,
//                              bucket_stats: RawSeq[ofp_bucket_counter]) =
//    ofp_group_stats(length, Pad2(),
//                    group_id, ref_count, Pad4(),
//                    packet_count, byte_count,
//                    duration_sec, duration_nsec,
//                    bucket_stats)

  override val rawFieldsLengthCalculator: LengthCalculator = {
    case 9 => Some { (s: Seq[Any]) => lenField(0)(s) - 40 }
  }
}

/* Used in group stats replies. */
case class ofp_bucket_counter private[sdriver] (
  packet_count: UInt64, /* Number of packets processed by bucket. */
  byte_count: UInt64 /* Number of bytes processed by bucket. */
)

/* Used in group stats replies. */
//object ofp_bucket_counter {
//  private[sdriver] def build(packet_count: UInt64,
//                              byte_count: UInt64) = ofp_bucket_counter(packet_count, byte_count)
//}

case class ofp_multipart_group_stats_request private [sdriver] (structure: ofp_group_stats_request) extends MultipartRequest[ofp_group_stats_request] {
  override def tp: OFP_MULTIPART_TYPE =  ofp_multipart_type.OFPMP_GROUP

  override def structures = Seq(structure)
}

object ofp_multipart_group_stats_request {
  private [sdriver] def build(structure: ofp_group_stats_request) = ofp_multipart_group_stats_request(structure)
}

case class ofp_multipart_group_stats_reply private [sdriver] (stats: RawSeq[ofp_group_stats])