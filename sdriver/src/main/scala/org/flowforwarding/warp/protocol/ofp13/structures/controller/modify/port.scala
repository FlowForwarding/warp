/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._

import org.flowforwarding.warp.protocol.ofp13.structures.ofp_port_no.OFP_PORT_NO
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_port_config.OFP_PORT_CONFIG
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_port_features.OFP_PORT_FEATURES
import org.flowforwarding.warp.protocol.dynamic.DynamicPath
import ofp_length._

/* Modify behavior of the physical port */
case class ofp_port_mod private [protocol] (
  header: ofp_header,
  port_no: OFP_PORT_NO,
  pad: Pad4,
  hw_addr: UInt48,              /* The hardware address is not configurable. This is used to sanity-check the request,
                                   so it must be the same as returned in an ofp_port struct. */
  pad2: Pad2,                   /* Pad to 64 bits. */
  config: OFP_PORT_CONFIG,      /* Bitmap of OFPPC_* flags. */
  mask: OFP_PORT_CONFIG,        /* Bitmap of OFPPC_* flags to be changed. */
  advertise: OFP_PORT_FEATURES, /* Bitmap of OFPPF_*. Zero all bits to prevent any action taking place. */
  pad3: Pad4)                   /* Pad to 64 bits. */

object ofp_port_mod{
  def build(@DynamicPath("header", "xid") xid: UInt32,
            port_no: OFP_PORT_NO,
            hw_addr: UInt48,                  /* The hardware address is not configurable. This is used to sanity-check the request,
                                                so it must be the same as returned in an ofp_port struct. */
            config: OFP_PORT_CONFIG,          /* Bitmap of OFPPC_* flags. */
            mask: OFP_PORT_CONFIG,            /* Bitmap of OFPPC_* flags to be changed. */
            advertise: OFP_PORT_FEATURES) = { /* Bitmap of OFPPF_*. Zero all bits to prevent any action taking place. */
    val header = ofp_header(OFPL_PORT_MOD_LEN, xid)
    ofp_port_mod(header, port_no, Pad4(), hw_addr, Pad2(), config, mask, advertise, Pad4())
  }
}

