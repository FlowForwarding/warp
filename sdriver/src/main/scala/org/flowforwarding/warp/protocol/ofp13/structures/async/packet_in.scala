/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_packet_in_reason.PACKET_IN_REASON
import com.gensler.scalavro.util.U16

/* Packet received on port (datapath -> controller). */
case class ofp_packet_in private[protocol] (
  header:    ofp_header,
  buffer_id: UInt32,           /* ID assigned by datapath. */
  total_len: UInt16,           /* Full length of frame. */
  reason:    PACKET_IN_REASON, /* Reason packet is being sent (one of OFPR_*) */
  table_id:  UInt8,            /* ID of the table that was looked up */
  cookie:    UInt64,           /* Cookie of the flow entry that was looked up. */
  pMatch:    ofp_match,        /* Packet metadata. Variable size. */
  /* The variable size and padded match is always followed by:
   * - Exactly 2 all-zero padding bytes, then
   * - An Ethernet frame whose length is inferred from header.length.
   * The padding bytes preceding the Ethernet frame ensure that the IP
   * header (if any) following the Ethernet header is 32-bit aligned.
   */
  pad: Pad2,                   /* Align to 64 bit + 16 bit */
  data: RawSeq[UInt8])         /* Ethernet frame */

object ofp_packet_in extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = { case 8 => { case Seq(_, _, total_len: UInt16, _*) => U16.f(UInt16.toShort(total_len)) } }
}

/* Why is this packet being sent to the controller? */
object ofp_packet_in_reason extends ByteEnum {
  type PACKET_IN_REASON = Value

  val OFPR_NO_MATCH    = ##(0) /* No matching flow (table-miss flow entry). */
  val OFPR_ACTION      = ##(1) /* Action explicitly output to controller. */
  val OFPR_INVALID_TTL = ##(2) /* Packet has invalid TTL */
};


