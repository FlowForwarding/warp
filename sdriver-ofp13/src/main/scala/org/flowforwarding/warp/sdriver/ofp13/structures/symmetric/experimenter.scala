/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.sdriver.dynamic.DynamicPath
import ofp_length._

/* Experimenter extension. */
case class ofp_experimenter_header private[sdriver] (header: ofp_header,            /* Type OFPT_EXPERIMENTER. */
                                                      experimenter: UInt32,          /* Experimenter ID:
                                                                                      * - MSB 0: low-order bytes are IEEE OUI.
                                                                                        - MSB != 0: defined by ONF. */
                                                      exp_type: UInt32,              /* Experimenter defined. */
                                                      data: RawSeq[UInt8]) /* Experimenter-defined arbitrary additional data. */

object ofp_experimenter_header extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = { case 3 => bodyLength }

  private[sdriver] def build(@DynamicPath("header", "xid") xid: UInt32, experimenter: UInt32, exp_type: UInt32, data: RawSeq[UInt8] = RawSeq()) = {
    val header = ofp_header(OFPL_EXPERIMENTER_LEN + data.length, xid)
    ofp_experimenter_header(header, experimenter, exp_type, data)
  }

  def apply(xid: Int, experimenter: Int, experimenterType: Int, data: Array[Byte]): ofp_experimenter_header =
    build(UInt32.fromInt(xid), UInt32.fromInt(experimenter), UInt32.fromInt(experimenterType), RawSeq(data map UInt8.fromByte: _*))
}