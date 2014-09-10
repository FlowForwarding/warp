/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._

import org.flowforwarding.warp.protocol.ofp13.structures.ofp_group_mod_command.OFP_GROUP_COMMAND
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_group_type.OFP_GROUP_TYPE
import org.flowforwarding.warp.protocol.dynamic.DynamicPath
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_length._

/* Group setup and teardown (controller -> datapath). */
case class ofp_group_mod private[protocol] (
  header: ofp_header,
  command: OFP_GROUP_COMMAND,  /* One of OFPGC_*. */
  t: OFP_GROUP_TYPE,           /* One of OFPGT_*. */
  pad: Pad1,                   /* Pad to 64 bits. */
  group_id: UInt32,            /* Group identifier. */
  buckets: RawSeq[ofp_bucket]) /* The length of the bucket array is inferred from the length field in the header. */

object ofp_group_mod extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = { case 5 => ??? } // TODO: Implement

  private[protocol] def build(@DynamicPath("header", "xid") xid: UInt32,
                              command: OFP_GROUP_COMMAND,
                              t: OFP_GROUP_TYPE,
                              group_id: UInt32,
                              buckets: RawSeq[ofp_bucket]) = {
    val header = ofp_header(OFPL_GROUP_MOD_LEN + buckets.foldLeft(0) { (s, b) => s + UInt16.toShort(b.len) }, xid)
    ofp_group_mod(header, command, t, Pad1(), group_id, buckets)
  }
}

/* Group commands */
object ofp_group_mod_command extends WordEnum {
  type OFP_GROUP_COMMAND = Value

  val OFPGC_ADD    = ##(0) /* New group. */
  val OFPGC_MODIFY = ##(1) /* Modify all matching groups. */
  val OFPGC_DELETE = ##(2) /* Delete all matching groups. */
}

/* Group types. Values in the range [128, 255] are reserved for experimental
* use. */
object ofp_group_type extends ByteEnum {
  type OFP_GROUP_TYPE = Value

  val OFPGT_ALL      = ##(0) /* All (multicast/broadcast) group. */
  val OFPGT_SELECT   = ##(1) /* Select group. */
  val OFPGT_INDIRECT = ##(2) /* Indirect group. */
  val OFPGT_FF       = ##(3) /* Fast failover group. */
}

/* Group numbering. Groups can use any number up to OFPG_MAX. */
object ofp_group extends DWordEnum with AllowUnspecifiedValues[Int]{
  type OFP_GROUP = Value

  /* Last usable group number. */
  val OFPG_MAX = ##(0xffffff00L)
  /* Fake groups. */
  val OFPG_ALL = ##(0xfffffffcL) /* Represents all groups for group delete commands. */
  val OFPG_ANY = ##(0xffffffffL) /* Wildcard group used only for flow stats requests.
                                   Selects all flows regardless of group (including flows with no group). */
}

/* Bucket for use in groups. */
case class ofp_bucket private[protocol] (
  len: UInt16,         /* Length the bucket in bytes, including this header and
                          any padding to make it 64-bit aligned. */
  weight: UInt16,      /* Relative weight of bucket. Only defined for select groups. */
  watch_port: UInt32,  /* Port whose state affects whether this bucket is live.
                          Only required for fast failover groups. */
  watch_group: UInt32, /* Group whose state affects whether this bucket is live.
                          Only required for fast failover groups. */
  pad: Pad4,
  /* 0 or more actions associated with the bucket -
     The action list length is inferred from the length of the bucket. */
  actions: RawSeq[ofp_action])

object ofp_bucket extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = { case 5 => ??? } // TODO: Implement

  private[protocol] def build(weight: UInt16,
                              watch_port: UInt32,
                              watch_group: UInt32,
                              actions: RawSeq[ofp_action]) = {
    val actionsLen = actions.foldLeft(0) { _ + _.len.data }
    ofp_bucket(UInt16.fromShort((16 + actionsLen).toShort), weight, watch_port, watch_group, Pad4(), actions)
  }
}