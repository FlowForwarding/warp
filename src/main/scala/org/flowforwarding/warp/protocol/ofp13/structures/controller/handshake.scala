package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_capabilities.OFP_CAPABILITIES
import org.flowforwarding.warp.protocol.dynamic.DynamicPath

case class ofp_switch_features_request private [protocol](header: ofp_header)

object ofp_switch_features_request{
  private [protocol] def build(@DynamicPath("header", "xid") xid: UInt32) = ofp_switch_features_request(ofp_header(ofp_length.OFPL_FEATURES_REQUEST_LEN, xid))
  def apply(xid: Int): ofp_switch_features_request = build(UInt32.fromInt(xid))
}

/* Switch features. */
case class ofp_switch_features_reply(
  header: ofp_header,
  datapathId: UInt64,            /* Datapath unique ID. The lower 48-bits are for a MAC address, while the upper 16-bits are implementer-defined. */
  nBuffers: UInt32,              /* Max packets buffered at once. */
  nTables: UInt8,                /* Number of tables supported by datapath. */
  auxiliaryId: UInt8,            /* Identify auxiliary connections */
  pad: Pad2,                     /* Align to 64-bits. */
  /* Features. */
  capabilities: UInt32/*OFP_CAPABILITIES*/, /* Bitmap of support "ofp_capabilities". */   // TODO: Implement union of enumeration values
  reserved: UInt32)


/* Capabilities supported by the datapath. */
object ofp_capabilities extends DWordEnum{
  type OFP_CAPABILITIES = Value
  val OFPC_FLOW_STATS   = ##(1 << 0) /* Flow statistics. */
  val OFPC_TABLE_STATS  = ##(1 << 1) /* Table statistics. */
  val OFPC_PORT_STATS   = ##(1 << 2) /* Port statistics. */
  val OFPC_GROUP_STATS  = ##(1 << 3) /* Group statistics. */
  val OFPC_IP_REASM     = ##(1 << 5) /* Can reassemble IP fragments. */
  val OFPC_QUEUE_STATS  = ##(1 << 6) /* Queue statistics. */
  val OFPC_PORT_BLOCKED = ##(1 << 8) /* Switch will block looping ports. */
};