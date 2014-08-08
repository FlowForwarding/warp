package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply.UInt32

import org.flowforwarding.warp.protocol.dynamic.DynamicPath
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_length._

case class ofp_barrier_request(header: ofp_header)

object ofp_barrier_request{

  private[protocol] def build(@DynamicPath("header", "xid") xid: UInt32) =
    ofp_barrier_request(ofp_header(OFPL_BARRIER_REQUEST_LEN, xid))

  def apply(xid: Int): ofp_barrier_request = build(UInt32.fromInt(xid))
}

case class ofp_barrier_reply(header: ofp_header)