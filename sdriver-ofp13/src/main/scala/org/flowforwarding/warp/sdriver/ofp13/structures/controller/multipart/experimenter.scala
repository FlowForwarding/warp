/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_multipart_type._

/* Body for ofp_multipart_request/reply of type OFPMP_EXPERIMENTER. */
case class ofp_experimenter_multipart private[sdriver] (
  experimenter: UInt32, /* Experimenter ID which takes the same form as in struct ofp_experimenter_header. */
  exp_type: UInt32, /* Experimenter defined. */
  data: RawSeq[UInt8]
)

object ofp_experimenter_multipart extends RawSeqFieldsInfo{
  override val rawFieldsLengthCalculator: LengthCalculator = {  case 2 => None  }

  private[sdriver] def build(experimenter: UInt32,
                              exp_type: UInt32,
                              data: RawSeq[UInt8]) =
    ofp_experimenter_multipart(experimenter, exp_type, data)

  def apply(experimenter: UInt32, exp_type: UInt32, data: Array[Byte]): ofp_experimenter_multipart =
    build(experimenter, exp_type, RawSeq(data map UInt8.fromByte: _*))
}

case class ofp_multipart_experimenter_request private [sdriver] (structure: ofp_experimenter_multipart) extends MultipartRequest[ofp_experimenter_multipart] {
  override def tp: OFP_MULTIPART_TYPE = ofp_multipart_type.OFPMP_EXPERIMENTER

  override def structures = Seq(structure)
}

object ofp_multipart_experimenter_request {
  private [sdriver] def build(structure: ofp_experimenter_multipart) = ofp_multipart_experimenter_request(structure)
}

case class ofp_multipart_experimenter_reply private [sdriver] (data: ofp_experimenter_multipart)