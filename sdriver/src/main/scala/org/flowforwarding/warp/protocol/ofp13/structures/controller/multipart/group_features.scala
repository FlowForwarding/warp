/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_group_capabilities.OFP_GROUP_CAPABILITIES
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_multipart_type._

/* Body of reply to OFPMP_GROUP_FEATURES request. Group features. */
case class ofp_group_features(
  types: UInt32, /* Bitmap of (1 << OFPGT_*) values supported. */
  capabilities: OFP_GROUP_CAPABILITIES, /* Bitmap of OFPGFC_* capability supported. */
  max_groups: RawSeq[UInt32], /* Maximum number of groups for each type. */
  actions: RawSeq[UInt32] /* Bitmaps of (1 << OFPAT_*) values supported. */
)

object ofp_group_features extends RawSeqFieldsInfo{
  override val rawFieldsLengthCalculator: LengthCalculator = {
    case 2 => Some { (s: Seq[Any]) => 4 * 4 }
    case 3 => Some { (s: Seq[Any]) => 4 * 4 }
  }
}

/* Group configuration flags */
object ofp_group_capabilities extends DWordBitmap{
  type OFP_GROUP_CAPABILITIES = Value
  val OFPGFC_SELECT_WEIGHT   = ##(1 << 0) /* Support weight for select groups */
  val OFPGFC_SELECT_LIVENESS = ##(1 << 1) /* Support liveness for select groups */
  val OFPGFC_CHAINING        = ##(1 << 2) /* Support chaining groups */
  val OFPGFC_CHAINING_CHECKS = ##(1 << 3) /* Check chaining for loops and delete */
}

case class ofp_multipart_group_features_request private [protocol] () extends EmptyMultipartRequest {
  override def tp: OFP_MULTIPART_TYPE = ofp_multipart_type.OFPMP_GROUP_FEATURES
}

object ofp_multipart_group_features_request {
  private [protocol] def build() = ofp_multipart_group_features_request()
}

case class ofp_multipart_group_features_reply private [protocol] (features: ofp_group_features)