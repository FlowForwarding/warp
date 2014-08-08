package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._

import org.flowforwarding.warp.protocol.dynamic.DynamicPath
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_length._
import spire.math.UInt

/* Query for port queue configuration. */
case class ofp_queue_get_config_request private[protocol] (header: ofp_header,
                                        /* Port to be queried. Should refer to a valid physical port
                                           (i.e. <= OFPP_MAX), or OFPP_ANY to request all configured queues.*/
                                        port: UInt32,
                                        pad: Pad4)

object ofp_queue_get_config_request{

  private[protocol] def build(@DynamicPath("header", "xid") xid: UInt32, port: UInt32) =
    ofp_queue_get_config_request(ofp_header(OFPL_QUEUE_GET_CONFIG_REQUEST_LEN, xid), port, Pad4())

  def apply(xid: Int, port: UInt): ofp_queue_get_config_request = build(UInt32.fromInt(xid), UInt32.fromInt(port.toInt))
}

/* Queue configuration for a given port. */
case class ofp_queue_get_config_reply private[protocol](
  header: ofp_header,
  port: UInt32,
  pad: Pad4,
  queues: RawSeq[ofp_packet_queue]) /* List of configured queues. */

object ofp_queue_get_config_reply extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = { case 3 => bodyLengthMinus(8) }
}
