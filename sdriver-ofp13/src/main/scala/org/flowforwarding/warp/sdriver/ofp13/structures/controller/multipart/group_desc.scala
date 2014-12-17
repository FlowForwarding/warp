/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_multipart_type._

/* Body of reply to OFPMP_GROUP_DESC request. */
case class ofp_group_desc private[sdriver] (
  length: UInt16, /* Length of this entry. */
  `type`: UInt8, /* One of OFPGT_*. */
  pad: Pad1, /* Pad to 64 bits. */
  group_id: UInt32, /* Group identifier. */
  buckets: RawSeq[ofp_bucket] /* List of buckets - 0 or more. */
)

object ofp_group_desc extends RawSeqFieldsInfo{
  override val rawFieldsLengthCalculator: LengthCalculator = {
    case 4 => Some { (s: Seq[Any]) => lenField(0)(s) - 8 }
  }
}

case class ofp_multipart_group_desc_request private [sdriver] () extends EmptyMultipartRequest {
  override def tp: OFP_MULTIPART_TYPE = ofp_multipart_type.OFPMP_GROUP_DESC
}

object ofp_multipart_group_desc_request {
  private [sdriver] def build() = ofp_multipart_group_desc_request()
}

case class ofp_multipart_group_desc_reply private [sdriver] (desc: RawSeq[ofp_group_desc])