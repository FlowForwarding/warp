/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_multipart_type._
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_port_no._

/* Body for ofp_multipart_request of type OFPMP_PORT. */
case class ofp_port_stats_request private[sdriver] (
  /* OFPMP_PORT message must request statistics either for a single port (specified in  port_no) or for all ports (if port_no == OFPP_ANY). */
  port_no: OFP_PORT_NO,
  pad: Pad4)

object ofp_port_stats_request{
  private[sdriver] def build(port_no: OFP_PORT_NO) = ofp_port_stats_request(port_no, Pad4())

  def apply(port_no: OFP_PORT_NO): ofp_port_stats_request = build(port_no)
}

/* Body of reply to OFPMP_PORT request. If a counter is unsupported, set
 * the field to all ones. */
case class ofp_port_stats private[sdriver] (
  port_no: OFP_PORT_NO,
  pad: Pad4, /* Align to 64-bits. */
  rx_packets: UInt64, /* Number of received packets. */
  tx_packets: UInt64, /* Number of transmitted packets. */
  rx_bytes: UInt64, /* Number of received bytes. */
  tx_bytes: UInt64, /* Number of transmitted bytes. */
  rx_dropped: UInt64, /* Number of packets dropped by RX. */
  tx_dropped: UInt64, /* Number of packets dropped by TX. */
  rx_errors: UInt64, /* Number of receive errors. This is a super-set of more specific receive errors and should be
                        greater than or equal to the sum of all rx_*_err values. */
  tx_errors: UInt64, /* Number of transmit errors. This is a super-set of more specific transmit errors and should be
                        greater than or equal to the sum of all tx_*_err values (none currently defined.) */
  rx_frame_err: UInt64, /* Number of frame alignment errors. */
  rx_over_err: UInt64, /* Number of packets with RX overrun. */
  rx_crc_err: UInt64, /* Number of CRC errors. */
  collisions: UInt64, /* Number of collisions. */
  duration_sec: UInt32, /* Time port has been alive in seconds. */
  duration_nsec: UInt32 /* Time port has been alive in nanoseconds beyond duration_sec. */
)

//object ofp_port_stats {
//  def build(port_no: OFP_PORT_NO,
//            rx_packets: UInt64,
//            tx_packets: UInt64,
//            rx_bytes: UInt64,
//            tx_bytes: UInt64,
//            rx_dropped: UInt64,
//            tx_dropped: UInt64,
//            rx_errors: UInt64,
//            tx_errors: UInt64,
//            rx_frame_err: UInt64,
//            rx_over_err: UInt64,
//            rx_crc_err: UInt64,
//            collisions: UInt64,
//            duration_sec: UInt32,
//            duration_nsec: UInt32) =
//    ofp_port_stats(port_no, Pad4(),
//                   rx_packets, tx_packets,
//                   rx_bytes, tx_bytes,
//                   rx_dropped, tx_dropped,
//                   rx_errors, tx_errors,
//                   rx_frame_err, rx_over_err, rx_crc_err,
//                   collisions,
//                   duration_sec, duration_nsec)
//}

case class ofp_multipart_port_stats_request private [sdriver] (structure: ofp_port_stats_request) extends MultipartRequest[ofp_port_stats_request] {
  override def tp: OFP_MULTIPART_TYPE = ofp_multipart_type.OFPMP_PORT_STATS

  override def structures = Seq(structure)
}

object ofp_multipart_port_stats_request {
  private [sdriver] def build(structure: ofp_port_stats_request) = ofp_multipart_port_stats_request(structure)
}

case class ofp_multipart_port_stats_reply private [sdriver] (stats: ofp_port_stats)