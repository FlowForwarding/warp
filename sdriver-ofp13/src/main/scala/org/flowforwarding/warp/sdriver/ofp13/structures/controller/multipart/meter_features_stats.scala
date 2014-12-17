/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_multipart_type._

/* Body of reply to OFPMP_METER_FEATURES request. Meter features. */
case class ofp_meter_features(
  max_meter: UInt32, /* Maximum number of meters. */
  band_types: UInt32, /* Bitmaps of (1 << OFPMBT_*) values supported. */
  capabilities: UInt32, /* Bitmaps of "ofp_meter_flags". */
  max_bands: UInt8, /* Maximum bands per meters */
  max_color: UInt8, /* Maximum color value */
  pad: Pad2)

case class ofp_multipart_meter_features_request private [sdriver] () extends EmptyMultipartRequest {
  override def tp: OFP_MULTIPART_TYPE = ofp_multipart_type.OFPMP_METER_FEATURES
}

object ofp_multipart_meter_features_request {
  private [sdriver] def build() = ofp_multipart_meter_features_request()
}

case class ofp_multipart_meter_features_reply private [sdriver] (features: ofp_meter_features)