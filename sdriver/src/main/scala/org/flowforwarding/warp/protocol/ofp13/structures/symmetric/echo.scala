/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply.{UInt32, UInt8, RawSeqFieldsInfo, RawSeq}
import org.flowforwarding.warp.protocol.dynamic.DynamicPath

import ofp_length._

case class echo_request private[protocol] (header: ofp_header, elements: RawSeq[UInt8] = RawSeq())/* Arbitrary-length data field */

object echo_request extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = { case 1 => bodyLength }

  private[protocol] def build(@DynamicPath("header", "xid") xid: UInt32, elements: RawSeq[UInt8] = RawSeq()) = {
    val header = ofp_header(OFPL_ECHO_REQUEST_LEN + elements.length, xid)
    echo_request(header, elements)
  }

  def apply(xid: Int, elements: Array[Byte]): echo_request = build(UInt32.fromInt(xid), RawSeq(elements map UInt8.fromByte: _*))
}

case class echo_reply private[protocol] (header: ofp_header, elements: RawSeq[UInt8])

object echo_reply extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = { case 1 => bodyLength }

  private[protocol] def build(@DynamicPath("header", "xid") xid: UInt32, elements: RawSeq[UInt8] = RawSeq()) = {
    val header = ofp_header(OFPL_ECHO_REPLY_LEN + elements.length, xid)
    echo_reply(header, elements)
  }

  def apply(xid: Int, elements: Array[Byte]): echo_reply = build(UInt32.fromInt(xid), RawSeq(elements map UInt8.fromByte: _*))
}