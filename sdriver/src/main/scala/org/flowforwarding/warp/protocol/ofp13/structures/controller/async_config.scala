/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import spire.math.ULong

import com.gensler.scalavro.types.supply.{UInt32, UInt64}

import org.flowforwarding.warp.protocol.dynamic.DynamicPath
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_length._

case class ofp_get_async_request private[protocol] (header: ofp_header)

object ofp_get_async_request{

  private[protocol] def build(@DynamicPath("header", "xid") xid: UInt32) =
    ofp_get_async_request(ofp_header(OFPL_GET_ASYNC_REQUEST_LEN, xid))

  def apply(xid: Int): ofp_get_async_request = build(UInt32.fromInt(xid))
}

/* Asynchronous message configuration. */
case class ofp_get_async_reply private[protocol] (
  header: ofp_header,
  packet_in_mask:    UInt64, /* Bitmasks of OFPR_* values. */
  port_status_mask:  UInt64, /* Bitmasks of OFPPR_* values. */
  flow_removed_mask: UInt64) /* Bitmasks of OFPRR_* values. */

case class ofp_set_async private[protocol] (
   header: ofp_header,
   packet_in_mask:    UInt64, /* Bitmasks of OFPR_* values. */
   port_status_mask:  UInt64, /* Bitmasks of OFPPR_* values. */
   flow_removed_mask: UInt64) /* Bitmasks of OFPRR_* values. */

object ofp_set_async{

  private[protocol] def build(@DynamicPath("header", "xid") xid: UInt32,
                              packet_in_mask:    UInt64,
                              port_status_mask:  UInt64,
                              flow_removed_mask: UInt64) =
    ofp_set_async(ofp_header(OFPL_SET_ASYNC_LEN, xid), packet_in_mask, port_status_mask, flow_removed_mask)

  def apply(xid: Int, packet_in_mask: ULong, port_status_mask: ULong, flow_removed_mask: ULong): ofp_set_async =
    build(UInt32.fromInt(xid), UInt64.fromLong(packet_in_mask.toLong), UInt64.fromLong(port_status_mask.toLong), UInt64.fromLong(flow_removed_mask.toLong))
}