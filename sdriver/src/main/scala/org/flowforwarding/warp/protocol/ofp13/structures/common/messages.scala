/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._
import com.gensler.scalavro.types.supply.{WordEnum, ByteEnum}

import org.flowforwarding.warp.protocol.ofp13.structures.ofp_length.OFP_LENGTH
import com.gensler.scalavro.util.Union._
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_type.OFP_TYPE

object ofp_type extends ByteEnum {
  type OFP_TYPE = ofp_type.Value

  val OFPT_HELLO                    = ##(0)
  val OFPT_ERROR                    = ##(1)
  val OFPT_ECHO_REQUEST             = ##(2)
  val OFPT_ECHO_REPLY               = ##(3)
  val OFPT_EXPERIMENTER             = ##(4)

  val OFPT_FEATURES_REQUEST         = ##(5)
  val OFPT_FEATURES_REPLY           = ##(6)
  val OFPT_GET_CONFIG_REQUEST       = ##(7)
  val OFPT_GET_CONFIG_REPLY         = ##(8)
  val OFPT_SET_CONFIG               = ##(9)

  val OFPT_PACKET_IN                = ##(10)
  val OFPT_FLOW_REMOVED             = ##(11)
  val OFPT_PORT_STATUS              = ##(12)

  val OFPT_PACKET_OUT               = ##(13)
  val OFPT_FLOW_MOD                 = ##(14)
  val OFPT_GROUP_MOD                = ##(15)
  val OFPT_PORT_MOD                 = ##(16)
  val OFPT_TABLE_MOD                = ##(17)

  val OFPT_MULTIPART_REQUEST        = ##(18)
  val OFPT_MULTIPART_REPLY          = ##(19)

  val OFPT_BARIER_REQUEST           = ##(20)
  val OFPT_BARIER_REPLY             = ##(21)

  val OFPT_QUEUE_GET_CONFIG_REQUEST = ##(22)
  val OFPT_QUEUE_GET_CONFIG_REPLY   = ##(23)

  val OFPT_ROLE_REQUEST             = ##(24)
  val OFPT_ROLE_REPLY               = ##(25)

  val OFPT_GET_ASYNC_REQUEST        = ##(26)
  val OFPT_GET_ASYNC_REPLY          = ##(27)
  val OFPT_SET_ASYNC                = ##(28)

  val OFPT_METER_MOD                = ##(29)
}

object ofp_length extends WordEnum with AllowUnspecifiedValues[Short] {
  type OFP_LENGTH = ofp_length.Value

  implicit class ofpLengthExt(len: OFP_LENGTH){
    def +(extraLen: Int) = Unspecified((len.data + extraLen).toShort)
  }

  val OFPL_HELLO_LEN                    = ##(8)
  val OFPL_ERROR_LEN                    = ##(12)
  val OFPL_ECHO_REQUEST_LEN             = ##(8)
  val OFPL_ECHO_REPLY_LEN               = ##(8)
  val OFPL_EXPERIMENTER_LEN             = ##(16)

  val OFPL_FEATURES_REQUEST_LEN         = ##(8)
  val OFPL_FEATURES_REPLY_LEN           = ##(32)
  val OFPL_GET_CONFIG_REQUEST_LEN       = ##(8)
  val OFPL_GET_CONFIG_REPLY_LEN         = ##(12)
  val OFPL_SET_CONFIG_LEN               = ##(12)

  val OFPL_PACKET_IN_LEN                = ##(32)
  val OFPL_FLOW_REMOVED_LEN             = ##(56)
  val OFPL_PORT_STATUS_LEN              = ##(80)

  val OFPL_PACKET_OUT_LEN               = ##(24)
  val OFPL_FLOW_MOD_LEN                 = ##(56)
  val OFPL_GROUP_MOD_LEN                = ##(16)
  val OFPL_PORT_MOD_LEN                 = ##(40)
  val OFPL_TABLE_MOD_LEN                = ##(16)

  val OFPL_MULTIPART_REQUEST_LEN        = ##(16)
  val OFPL_MULTIPART_REPLY_LEN          = ##(16)

  val OFPL_BARRIER_REQUEST_LEN          = ##(8)
  val OFPL_BARRIER_REPLY_LEN            = ##(8)

  val OFPL_QUEUE_GET_CONFIG_REQUEST_LEN = ##(16)
  val OFPL_QUEUE_GET_CONFIG_REPLY_LEN   = ##(16)

  val OFPL_ROLE_REQUEST_LEN             = ##(24)
  val OFPL_ROLE_REPLY_LEN               = ##(24)

  val OFPL_GET_ASYNC_REQUEST_LEN        = ##(8)
  val OFPL_GET_ASYNC_REPLY_LEN          = ##(32)
  val OFPL_SET_ASYNC_LEN                = ##(32)

  val OFPL_METER_MOD_LEN                = ##(16)
}

object ofp13_messages {
  type All = union [ofp_hello]                    #or
                   [ofp_error_msg]                #or
                   [echo_request]                 #or
                   [echo_reply]                   #or
                   [ofp_experimenter_header]      #or

                   [ofp_switch_features_request]  #or
                   [ofp_switch_features_reply]    #or
                   [ofp_switch_config_request]    #or
                   [ofp_switch_config_reply]      #or
                   [ofp_set_config]               #or

                   [ofp_packet_in]                #or
                   [ofp_flow_removed]             #or
                   [ofp_port_status]              #or

                   [ofp_packet_out]               #or
                   [ofp_flow_mod]                 #or
                   [ofp_group_mod]                #or
                   [ofp_port_mod]                 #or
                   [ofp_table_mod]                #or

                   [ofp_multipart_request]        #or
                   [ofp_multipart_reply]          #or

                   [ofp_barrier_request]          #or
                   [ofp_barrier_reply]            #or

                   [ofp_queue_get_config_request] #or
                   [ofp_queue_get_config_reply]   #or

                   [ofp_role_request]             #or
                   [ofp_role_reply]               #or

                   [ofp_get_async_request]        #or
                   [ofp_get_async_reply]          #or
                   [ofp_set_async]                #or

                   [ofp_meter_mod]

  type Ofp13Msg = org.flowforwarding.warp.protocol.OfpMsg[All, _]
  type TaggedOfp13Message = ByteTaggedUnion[All, OFP_TYPE]
}


case class ofp_header private[protocol](length: OFP_LENGTH, xid: UInt32)
