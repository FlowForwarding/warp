/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._
import com.gensler.scalavro.util.Union._
import org.flowforwarding.warp.protocol.dynamic.DynamicPath
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_multipart_type.OFP_MULTIPART_TYPE
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_multipart_request_flags.OFP_MULTIPART_REQUEST_FLAGS
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_multipart_reply_flags.OFP_MULTIPART_REPLY_FLAGS
import ofp_length._

object ofp_multipart_request{
  type All = union [ofp_multipart_desc_request] #or
                   [ofp_multipart_flow_request] #or
                   [ofp_multipart_aggregate_request] #or
                   [ofp_multipart_table_request] #or
                   [ofp_multipart_port_stats_request] #or
                   [ofp_multipart_queue_request] #or
                   [ofp_multipart_group_request] #or
                   [ofp_multipart_group_desc_request] #or
                   [ofp_multipart_group_features_request] #or
                   [ofp_multipart_meter_request] #or
                   [ofp_multipart_meter_config_request] #or
                   [ofp_multipart_meter_features_request] #or
                   [ofp_multipart_table_features_request] #or
                   [ofp_multipart_port_desc_request] #or
                   [ofp_multipart_experimenter_request]

  // TODO: think about construction through direct passing of type, flags and body params
  // The current implementation breaks protocol: added new structure layer "data"
  private[protocol] def build(@DynamicPath("header", "xid") xid: UInt32, data: ofp_multipart_request_type) = {
    val header = ofp_header(OFPL_MULTIPART_REQUEST_LEN, xid)
    ofp_multipart_request(header, data)
  }
}

object ofp_multipart_reply{
  type All = union [ofp_multipart_desc_reply] #or
                   [ofp_multipart_flow_reply] #or
                   [ofp_multipart_aggregate_reply] #or
                   [ofp_multipart_table_reply] #or
                   [ofp_multipart_port_stats_reply] #or
                   [ofp_multipart_queue_reply] #or
                   [ofp_multipart_group_reply] #or
                   [ofp_multipart_group_desc_reply] #or
                   [ofp_multipart_group_features_reply] #or
                   [ofp_multipart_meter_reply] #or
                   [ofp_multipart_meter_config_reply] #or
                   [ofp_multipart_meter_features_reply] #or
                   [ofp_multipart_table_features_reply] #or
                   [ofp_multipart_port_desc_reply] #or
                   [ofp_multipart_experimenter_reply]
}

trait ofp_multipart_reply_type extends WordTaggedUnion[ofp_multipart_reply.All, OFP_MULTIPART_TYPE]{
  def flags: OFP_MULTIPART_REPLY_FLAGS
}
trait ofp_multipart_request_type extends WordTaggedUnion[ofp_multipart_request.All, OFP_MULTIPART_TYPE]

case class ofp_multipart_reply private[protocol] (header: ofp_header, data: ofp_multipart_reply_type)
case class ofp_multipart_request private[protocol] (header: ofp_header, data: ofp_multipart_request_type)

object ofp_multipart_request_flags extends WordBitmap{
  type OFP_MULTIPART_REQUEST_FLAGS = Value
  val OFPMPF_REQ_MORE = ##(1 << 0) /* More requests to follow. */
}

object ofp_multipart_reply_flags extends WordBitmap{
  type OFP_MULTIPART_REPLY_FLAGS = Value
  val OFPMPF_REPLY_MORE = ##(1 << 0) /* More replies to follow. */
}

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