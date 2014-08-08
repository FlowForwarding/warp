package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._

import org.flowforwarding.warp.protocol.ofp13.structures.ofp_table.OFP_TABLE
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_table_config.OFP_TABLE_CONFIG

import org.flowforwarding.warp.protocol.dynamic.DynamicPath
import ofp_length._

/* Table numbering. Tables can use any number up to OFPT_MAX. */
object ofp_table extends ByteEnum with AllowUnspecifiedValues[Byte]{
  type OFP_TABLE = Value
  /* Last usable table number. */
  val OFPTT_MAX = ##(0xfe)
  /* Fake tables. */
  val OFPTT_ALL = ##(0xff) /* Wildcard table used for table config, flow stats and flow deletes. */
}

/* Configure/Modify behavior of a flow table */
case class ofp_table_mod private[protocol] (
  header: ofp_header,
  table_id: OFP_TABLE,      /* ID of the table, OFPTT_ALL indicates all tables */
  pad: Pad3,                /* Pad to 32 bits */
  config: OFP_TABLE_CONFIG) /* Bitmap of OFPTC_* flags */

object ofp_table_mod{

  private[protocol] def build(@DynamicPath("header", "xid") xid: UInt32, table_id: OFP_TABLE, config: OFP_TABLE_CONFIG) =
    ofp_table_mod(ofp_header(OFPL_TABLE_MOD_LEN, xid), table_id, Pad3(), config)

  def apply(xid: Int, table_id: OFP_TABLE, config: OFP_TABLE_CONFIG): ofp_table_mod = build(UInt32.fromInt(xid), table_id, config)
}

/* Flags to configure the table. Reserved for future use. */
object ofp_table_config extends DWordEnum { // DWordBitmap?
  type OFP_TABLE_CONFIG = Value
  val OFPTC_DEPRECATED_MASK = ##(3) /* Deprecated bits */
}

