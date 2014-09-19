package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply.{RawSeqFieldsInfo, RawSeq, UInt32, Pad4}
import org.flowforwarding.warp.protocol.dynamic.DynamicPath
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_length._
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_multipart_reply_flags.OFP_MULTIPART_REPLY_FLAGS
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_multipart_request_flags.OFP_MULTIPART_REQUEST_FLAGS

case class ofp_multipart_port_desc_reply private [protocol] (flags: OFP_MULTIPART_REPLY_FLAGS,
                                                             pad: Pad4,
                                                             body: RawSeq[ofp_port]) extends ofp_multipart_reply_type

object ofp_multipart_port_desc_reply extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = {
    case 2 => None
  }
}

case class ofp_multipart_port_desc_request private [protocol] (flags: OFP_MULTIPART_REQUEST_FLAGS,
                                                               pad: Pad4) extends ofp_multipart_request_type

object ofp_multipart_port_desc_request {
  private [protocol] def build(flags: OFP_MULTIPART_REQUEST_FLAGS) = ofp_multipart_port_desc_request(flags, Pad4())
}
