package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_flow_removed_reason.OFP_FLOW_REMOVED_REASON

case class ofp_flow_removed private[protocol] (
  header:        ofp_header,
  cookie:        UInt64,                       /* Opaque controller-issued identifier. */
  priority:      UInt16,                       /* Priority level of flow entry. */
  reason:        OFP_FLOW_REMOVED_REASON,      /* One of OFPRR_*. */
  table_id:      UInt8,                        /* ID of the table */
  duration_sec:  UInt32,                       /* Time flow was alive in seconds. */
  duration_nsec: UInt32,                       /* Time flow was alive in nanoseconds beyond duration_sec. */
  idle_timeout:  UInt16,                       /* Idle timeout from original flow mod. */
  hard_timeout:  UInt16,                       /* Hard timeout from original flow mod. */
  packet_count:  UInt64,
  byte_count:    UInt64,
  mMatch:        ofp_match) /* Description of fields. Variable size. */

/* Why was this flow removed? */
object ofp_flow_removed_reason extends ByteEnum {
  type OFP_FLOW_REMOVED_REASON = Value

  val OFPRR_IDLE_TIMEOUT = ##(0)  /* Flow idle time exceeded idle_timeout. */
  val OFPRR_HARD_TIMEOUT = ##(1)  /* Time exceeded hard_timeout. */
  val OFPRR_DELETE       = ##(2)  /* Evicted by a DELETE flow mod. */
  val OFPRR_GROUP_DELETE = ##(3)  /* Group was removed. */
};