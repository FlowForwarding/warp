/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_multipart_type._

/* Body of OFPMP_METER and OFPMP_METER_CONFIG requests. */
case class ofp_meter_multipart_request private[sdriver] (meter_id: UInt32, /* Meter instance, or OFPM_ALL. */
                                                          pad: Pad4) /* Align to 64 bits. */

object ofp_meter_multipart_request {
  private[sdriver] def build(meter_id: UInt32) = ofp_meter_multipart_request(meter_id, Pad4())

  def apply(meter_id: UInt32): ofp_meter_multipart_request = build(meter_id)
}

/* Body of reply to OFPMP_METER request. Meter statistics. */
case class ofp_meter_stats(
  meter_id: UInt32, /* Meter instance. */
  len: UInt16, /* Length in bytes of this stats. */
  pad: Pad6,
  flow_count: UInt32, /* Number of flows bound to meter. */
  packet_in_count: UInt64, /* Number of packets in input. */
  byte_in_count: UInt64, /* Number of bytes in input. */
  duration_sec: UInt32, /* Time meter has been alive in seconds. */
  duration_nsec: UInt32, /* Time meter has been alive in nanoseconds beyond duration_sec. */
  band_stats: RawSeq[ofp_meter_band_stats] /* The band_stats length is inferred from the length field. */
)

object ofp_meter_stats extends RawSeqFieldsInfo {

  private [sdriver] def build(structure: ofp_meter_multipart_request) = ofp_multipart_meter_stats_request(structure)

  override val rawFieldsLengthCalculator: ofp_meter_stats.LengthCalculator = {
    case 8 => Some { (s: Seq[Any]) => lenField(1)(s) - 40 }
  }
}

/* Statistics for each meter band */
case class ofp_meter_band_stats(packet_band_count: UInt64, /* Number of packets in band. */
                                byte_band_count: UInt64) /* Number of bytes in band. */

case class ofp_multipart_meter_stats_request private [sdriver] (structure: ofp_meter_multipart_request) extends MultipartRequest[ofp_meter_multipart_request] {
  override def tp: OFP_MULTIPART_TYPE =  ofp_multipart_type.OFPMP_METER

  override def structures = Seq(structure)
}

object ofp_multipart_meter_stats_request {
  private [sdriver] def build(structure: ofp_meter_multipart_request) = ofp_multipart_meter_stats_request(structure)
}

case class ofp_multipart_meter_stats_reply private [sdriver] (stats: RawSeq[ofp_meter_stats])