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

case class ofp_queue_stats_request(
  port_no: OFP_PORT_NO, /* All ports if OFPP_ANY. */
  queue_id: UInt32 /* All queues if OFPQ_ALL. */
)

object ofp_queue_stats_request{
  private[sdriver] def build(port_no: OFP_PORT_NO, queue_id: UInt32) = ofp_queue_stats_request(port_no, queue_id)
}
  
case class ofp_queue_stats private[sdriver] (
  port_no: OFP_PORT_NO,
  queue_id: UInt32, /* Queue i.d */
  tx_bytes: UInt64, /* Number of transmitted bytes. */
  tx_packets: UInt64, /* Number of transmitted packets. */
  tx_errors: UInt64, /* Number of packets dropped due to overrun. */
  duration_sec: UInt32, /* Time queue has been alive in seconds. */
  duration_nsec: UInt32 /* Time queue has been alive in nanoseconds beyond duration_sec. */
)

//object ofp_queue_stats{
//  private[sdriver] def build(port_no: OFP_PORT_NO,
//            queue_id: UInt32,
//            tx_bytes: UInt64,
//            tx_packets: UInt64,
//            tx_errors: UInt64,
//            duration_sec: UInt32,
//            duration_nsec: UInt32) = ofp_queue_stats(port_no, queue_id, tx_bytes, tx_packets, tx_errors, duration_sec, duration_nsec)
//}

case class ofp_multipart_queue_stats_request private [sdriver] (structure: ofp_queue_stats_request) extends MultipartRequest[ofp_queue_stats_request] {
  override def tp: OFP_MULTIPART_TYPE = ofp_multipart_type.OFPMP_QUEUE

  override def structures = Seq(structure)
}

object ofp_multipart_queue_stats_request {
  private [sdriver] def build(structure: ofp_queue_stats_request) = ofp_multipart_queue_stats_request(structure)
}

case class ofp_multipart_queue_stats_reply private [sdriver] (stats: RawSeq[ofp_queue_stats])