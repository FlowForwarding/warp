/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_meter_mod_command.OFP_METER_MOD_COMMAND
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_multipart_type._

/* Body of reply to OFPMP_METER_CONFIG request. Meter configuration. */
case class ofp_meter_config(
  length: UInt16, /* Length of this entry. */
  flags: OFP_METER_MOD_COMMAND, /* All OFPMC_* that apply. */
  meter_id: UInt32, /* Meter instance. */
  bands: RawSeq[ofp_meter_band_header] /* The bands length is inferred from the length field. */
)

object ofp_meter_config extends RawSeqFieldsInfo {
  override val rawFieldsLengthCalculator: ofp_meter_stats.LengthCalculator = {
    case 3 => Some { (s: Seq[Any]) => lenField(0)(s) - 8 }
  }
}

case class ofp_multipart_meter_config_request private [protocol] (structure: ofp_meter_multipart_request) extends MultipartRequest[ofp_meter_multipart_request] {
  override def tp: OFP_MULTIPART_TYPE = ofp_multipart_type.OFPMP_METER_CONFIG

  override def structures = Seq(structure)
}

object ofp_multipart_meter_config_request {
  private [protocol] def build(structure: ofp_meter_multipart_request) = ofp_multipart_meter_config_request(structure)
}

case class ofp_multipart_meter_config_reply private [protocol] (config: RawSeq[ofp_meter_config])