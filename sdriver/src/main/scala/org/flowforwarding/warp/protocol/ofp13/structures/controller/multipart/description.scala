package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply.{UInt32, Pad4, RawSeqFieldsInfo, RawSeq}
import org.flowforwarding.warp.protocol.dynamic.DynamicPath
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_multipart_reply_flags.OFP_MULTIPART_REPLY_FLAGS
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_multipart_request_flags.OFP_MULTIPART_REQUEST_FLAGS
import ofp_length._

case class ofp_desc(
  mfr_desc: RawSeq[Byte],   /* Manufacturer description. */
  hw_desc: RawSeq[Byte],    /* Hardware description. */
  sw_desc: RawSeq[Byte],    /* Software description. */
  serial_num: RawSeq[Byte], /* Serial number. */
  dp_desc: RawSeq[Byte]     /* Human readable description of datapath. */
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

case class ofp_multipart_desc_reply private [protocol] (flags: OFP_MULTIPART_REPLY_FLAGS,
                                                        pad: Pad4,
                                                        body: ofp_desc) extends ofp_multipart_reply_type

case class ofp_multipart_desc_request private [protocol] (flags: OFP_MULTIPART_REQUEST_FLAGS,
                                                          pad: Pad4) extends ofp_multipart_request_type

object ofp_multipart_desc_request {
  private [protocol] def build(flags: OFP_MULTIPART_REQUEST_FLAGS) = ofp_multipart_desc_request(flags, Pad4())
}