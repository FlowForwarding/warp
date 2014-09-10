/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_multipart_type.OFP_MULTIPART_TYPE
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_multipart_request_flags.OFP_MILTIPART_REQUEST_FLAGS
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_multipart_reply_flags.OFP_MILTIPART_REPLY_FLAGS

case class ofp_multipart_request(header: ofp_header,
                                 t: OFP_MULTIPART_TYPE,              /* One of the OFPMP_* constants. */    // TODO: union of multipart messages
                                 flags: OFP_MILTIPART_REQUEST_FLAGS, /* OFPMPF_REQ_* flags. */
                                 pad: Pad4 = Pad4(),
                                 body: RawSeq[UInt8] = RawSeq())     /* Body of the request. 0 or more bytes. */

object ofp_multipart_request extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = { case 4 => ??? } // TODO: Implement
}

case class ofp_multipart_reply(header: ofp_header,
                               t: OFP_MULTIPART_TYPE,            /* One of the OFPMP_* constants. */   // TODO: union of multipart messages
                               flags: OFP_MILTIPART_REPLY_FLAGS, /* OFPMPF_REPLY_* flags. */
                               pad: Pad4 = Pad4(),
                               body: RawSeq[UInt8] = RawSeq())   /* Body of the reply. 0 or more bytes. */

object ofp_multipart_reply extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = { case 4 => ??? } // TODO: Implement
}

object ofp_multipart_request_flags extends WordBitmap{
  type OFP_MILTIPART_REQUEST_FLAGS = Value
  val OFPMPF_REQ_MORE = ##(1 << 0) /* More requests to follow. */
}

object ofp_multipart_reply_flags extends WordBitmap{
  type OFP_MILTIPART_REPLY_FLAGS = Value
  val OFPMPF_REPLY_MORE = ##(1 << 0) /* More replies to follow. */
}

// TODO: deal with types
object ofp_multipart_type extends WordEnum{
  type OFP_MULTIPART_TYPE = Value

  /* Description of this OpenFlow switch.
   * The request body is empty.
   * The reply body is struct ofp_desc. */
  val OFPMP_DESC           = ##(0)
  /* Individual flow statistics.
   * The request body is struct ofp_flow_stats_request.
   * The reply body is an array of struct ofp_flow_stats. */
  val OFPMP_FLOW           = ##(1)
  /* Aggregate flow statistics.
    * The request body is struct ofp_aggregate_stats_request.
    * The reply body is struct ofp_aggregate_stats_reply. */
  val OFPMP_AGGREGATE      = ##(2)
  /* Flow table statistics.
    * The request body is empty.
    * The reply body is an array of struct ofp_table_stats. */
  val OFPMP_TABLE          = ##(3)
  /* Port statistics.
   * The request body is struct ofp_port_stats_request.
    * The reply body is an array of struct ofp_port_stats. */
  val OFPMP_PORT_STATS     = ##(4)
  /* Queue statistics for a port
   * The request body is struct ofp_queue_stats_request.
   * The reply body is an array of struct ofp_queue_stats */
  val OFPMP_QUEUE          = ##(5)
  /* Group counter statistics.
   * The request body is struct ofp_group_stats_request.
   * The reply is an array of struct ofp_group_stats. */
  val OFPMP_GROUP          = ##(6)
  /* Group description.
   * The request body is empty.
   * The reply body is an array of struct ofp_group_desc. */
  val OFPMP_GROUP_DESC     = ##(7)
  /* Group features.
   * The request body is empty.
   * The reply body is struct ofp_group_features. */
  val OFPMP_GROUP_FEATURES = ##(8)
  /* Meter statistics.
   * The request body is struct ofp_meter_multipart_requests.
   * The reply body is an array of struct ofp_meter_stats. */
  val OFPMP_METER          = ##(9)
  /* Meter configuration.
   * The request body is struct ofp_meter_multipart_requests.
   * The reply body is an array of struct ofp_meter_config. */
  val OFPMP_METER_CONFIG   = ##(10)
  /* Meter features.
   * The request body is empty.
   * The reply body is struct ofp_meter_features. */
  val OFPMP_METER_FEATURES = ##(11)
  /* Table features.
   * The request body is either empty or contains an array of
   * struct ofp_table_features containing the controller's
   * desired view of the switch. If the switch is unable to
   * set the specified view an error is returned.
   * The reply body is an array of struct ofp_table_features. */
  val OFPMP_TABLE_FEATURES = ##(12)
  /* Port description.
   * The request body is empty.
   * The reply body is an array of struct ofp_port. */
  val OFPMP_PORT_DESC      = ##(13)
  /* Experimenter extension.
   * The request and reply bodies begin with
   * struct ofp_experimenter_multipart_header.
   * The request and reply bodies are otherwise experimenter-defined. */
  val OFPMP_EXPERIMENTER   = ##(0xffffL)
}