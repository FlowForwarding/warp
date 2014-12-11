package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply.{UInt8, RawSeqFieldsInfo, RawSeq}
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_multipart_type.OFP_MULTIPART_TYPE

case class ofp_desc(
  mfr_desc: RawSeq[UInt8],   /* Manufacturer description. */
  hw_desc: RawSeq[UInt8],    /* Hardware description. */
  sw_desc: RawSeq[UInt8],    /* Software description. */
  serial_num: RawSeq[UInt8], /* Serial number. */
  dp_desc: RawSeq[UInt8]     /* Human readable description of datapath. */
)

object ofp_desc extends RawSeqFieldsInfo {
  val rawFieldsLengthCalculator: LengthCalculator = {
    case 0 => Some { _ => 256 }
    case 1 => Some { _ => 256 }
    case 2 => Some { _ => 256 }
    case 3 => Some { _ => 32  }
    case 4 => Some { _ => 256 }
  }
}

case class ofp_multipart_desc_request private [protocol] () extends EmptyMultipartRequest {
  override def tp: OFP_MULTIPART_TYPE = ofp_multipart_type.OFPMP_DESC
}

object ofp_multipart_desc_request {
  private [protocol] def build() = ofp_multipart_desc_request()
}

case class ofp_multipart_desc_reply private [protocol] (desc: ofp_desc)