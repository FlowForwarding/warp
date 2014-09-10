/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import org.flowforwarding.warp.protocol.ofp13.structures.ofp_error_type.OFP_ERROR_TYPE
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_error_code.OFP_ERROR_CODE
import com.gensler.scalavro.types.supply._

/* Values for 'type' in ofp_error_message. These values are immutable: they
 * will not change in future versions of the protocol (although new values may
 * be added). */
object ofp_error_type extends WordEnum{
  type OFP_ERROR_TYPE = Value

  val OFPET_HELLO_FAILED          = ##(0)             /* Hello protocol failed. */
  val OFPET_BAD_REQUEST           = ##(1)             /* Request was not understood. */
  val OFPET_BAD_ACTION            = ##(2)             /* Error in action description. */
  val OFPET_BAD_INSTRUCTION       = ##(3)             /* Error in instruction list. */
  val OFPET_BAD_MATCH             = ##(4)             /* Error in match. */
  val OFPET_FLOW_MOD_FAILED       = ##(5)             /* Problem modifying flow entry. */
  val OFPET_GROUP_MOD_FAILED      = ##(6)             /* Problem modifying group entry. */
  val OFPET_PORT_MOD_FAILED       = ##(7)             /* Port mod request failed. */
  val OFPET_TABLE_MOD_FAILED      = ##(8)             /* Table mod request failed. */
  val OFPET_QUEUE_OP_FAILED       = ##(9)             /* Queue operation failed. */
  val OFPET_SWITCH_CONFIG_FAILED  = ##(10)            /* Switch config request failed. */
  val OFPET_ROLE_REQUEST_FAILED   = ##(11)            /* Controller Role request failed. */
  val OFPET_METER_MOD_FAILED      = ##(12)            /* Error in meter. */
  val OFPET_TABLE_FEATURES_FAILED = ##(13)            /* Setting table features failed. */
  val OFPET_EXPERIMENTER          = ##(0xffff) /* Experimenter error messages. */
}

object ofp_error_code extends WordEnum{
  type OFP_ERROR_CODE = Value
/* ofp_error_msg 'code' values for OFPET_HELLO_FAILED. 'data' contains an
 * ASCII text string that may give failure details. */
//object ofp_hello_failed_code extends WordEnum{
  val OFPHFC_INCOMPATIBLE = ##(0) /* No compatible version. */
  val OFPHFC_EPERM        = ##(1) /* Permissions error. */
//}

/* ofp_error_msg 'code' values for OFPET_BAD_REQUEST. 'data' contains at least
 * the first 64 bytes of the failed request. */
//object ofp_bad_request_code extends WordEnum{
  val OFPBRC_BAD_VERSION               = ##(0) /* ofp_header.version not supported. */
  val OFPBRC_BAD_TYPE                  = ##(1) /* ofp_header.type not supported. */
  val OFPBRC_BAD_MULTIPART             = ##(2) /* ofp_multipart_request.type not supported. */
  val OFPBRC_BAD_EXPERIMENTER          = ##(3) /* Experimenter id not supported
                                                * (in ofp_experimenter_header or
                                                * ofp_multipart_request or
                                                * ofp_multipart_reply}. */
  val OFPBRC_BAD_EXP_TYPE              = ##(4) /* Experimenter type not supported. */
  val OFPBRC_EPERM                     = ##(5) /* Permissions error. */
  val OFPBRC_BAD_LEN                   = ##(6) /* Wrong request length for type. */
  val OFPBRC_BUFFER_EMPTY              = ##(7) /* Specified buffer has already been used. */
  val OFPBRC_BUFFER_UNKNOWN            = ##(8) /* Specified buffer does not exist. */
  val OFPBRC_BAD_TABLE_ID              = ##(9) /* Specified table-id invalid or does not exist. */
  val OFPBRC_IS_SLAVE                  = ##(10) /* Denied because controller is slave. */
  val OFPBRC_BAD_PORT                  = ##(11) /* Invalid port. */
  val OFPBRC_BAD_PACKET                = ##(12) /* Invalid packet in packet-out. */
  val OFPBRC_MULTIPART_BUFFER_OVERFLOW = ##(13) /* ofp_multipart_request overflowed the assigned buffer. */
//}

/* ofp_error_msg 'code' values for OFPET_BAD_ACTION. 'data' contains at least
 * the first 64 bytes of the failed request. */
//object ofp_bad_action_code extends WordEnum{
  val OFPBAC_BAD_TYPE           = ##(0)  /* Unknown or unsupported action type. */
  val OFPBAC_BAD_LEN            = ##(1)  /* Length problem in actions. */
  val OFPBAC_BAD_EXPERIMENTER   = ##(2)  /* Unknown experimenter id specified. */
  val OFPBAC_BAD_EXP_TYPE       = ##(3)  /* Unknown action for experimenter id. */
  val OFPBAC_BAD_OUT_PORT       = ##(4)  /* Problem validating output port. */
  val OFPBAC_BAD_ARGUMENT       = ##(5)  /* Bad action argument. */
  val OFPBAC_EPERM              = ##(6)  /* Permissions error. */
  val OFPBAC_TOO_MANY           = ##(7)  /* Can't handle this many actions. */
  val OFPBAC_BAD_QUEUE          = ##(8)  /* Problem validating output queue. */
  val OFPBAC_BAD_OUT_GROUP      = ##(9)  /* Invalid group id in forward action. */
  val OFPBAC_MATCH_INCONSISTENT = ##(10) /* Action can't apply for this match) or Set-Field missing prerequisite. */
  val OFPBAC_UNSUPPORTED_ORDER  = ##(11) /* Action order is unsupported for the action list in an Apply-Actions instruction */
  val OFPBAC_BAD_TAG            = ##(12) /* Actions uses an unsupported tag/encap. */
  val OFPBAC_BAD_SET_TYPE       = ##(13) /* Unsupported type in SET_FIELD action. */
  val OFPBAC_BAD_SET_LEN        = ##(14) /* Length problem in SET_FIELD action. */
  val OFPBAC_BAD_SET_ARGUMENT   = ##(15) /* Bad argument in SET_FIELD action. */
//}

/* ofp_error_msg 'code' values for OFPET_BAD_INSTRUCTION. 'data' contains at least
 * the first 64 bytes of the failed request. */
//object ofp_bad_instruction_code extends WordEnum{
  val OFPBIC_UNKNOWN_INST        = ##(0) /* Unknown instruction. */
  val OFPBIC_UNSUP_INST          = ##(1) /* Switch or table does not support the instruction. */
  val OFPBIC_BAD_TABLE_ID        = ##(2) /* Invalid Table-ID specified. */
  val OFPBIC_UNSUP_METADATA      = ##(3) /* Metadata value unsupported by datapath. */
  val OFPBIC_UNSUP_METADATA_MASK = ##(4) /* Metadata mask value unsupported by datapath. */
  val OFPBIC_BAD_EXPERIMENTER    = ##(5) /* Unknown experimenter id specified. */
  val OFPBIC_BAD_EXP_TYPE        = ##(6) /* Unknown instruction for experimenter id. */
  val OFPBIC_BAD_LEN             = ##(7) /* Length problem in instructions. */
  val OFPBIC_EPERM               = ##(8) /* Permissions error. */
//}

/* ofp_error_msg 'code' values for OFPET_BAD_MATCH. 'data' contains at least
 * the first 64 bytes of the failed request. */
//object ofp_bad_match_code extends WordEnum{
  val OFPBMC_BAD_TYPE         = ##(0)  /* Unsupported match type specified by the match */
  val OFPBMC_BAD_LEN          = ##(1)  /* Length problem in match. */
  val OFPBMC_BAD_TAG          = ##(2)  /* Match uses an unsupported tag/encap. */
  val OFPBMC_BAD_DL_ADDR_MASK = ##(3)  /* Unsupported datalink addr mask - switch does not support arbitrary datalink address mask. */
  val OFPBMC_BAD_NW_ADDR_MASK = ##(4)  /* Unsupported network addr mask - switch does not support arbitrary network address mask. */
  val OFPBMC_BAD_WILDCARDS    = ##(5)  /* Unsupported combination of fields masked or omitted in the match. */
  val OFPBMC_BAD_FIELD        = ##(6)  /* Unsupported field type in the match. */
  val OFPBMC_BAD_VALUE        = ##(7)  /* Unsupported value in a match field. */
  val OFPBMC_BAD_MASK         = ##(8)  /* Unsupported mask specified in the match) field is not dl-address or nw-address. */
  val OFPBMC_BAD_PREREQ       = ##(9)  /* A prerequisite was not met. */
  val OFPBMC_DUP_FIELD        = ##(10) /* A field type was duplicated. */
  val OFPBMC_EPERM            = ##(11) /* Permissions error. */
//}

/* ofp_error_msg 'code' values for OFPET_FLOW_MOD_FAILED. 'data' contains
* at least the first 64 bytes of the failed request. */
//object ofp_flow_mod_failed_code extends WordEnum{
  val OFPFMFC_UNKNOWN      = ##(0) /* Unspecified error. */
  val OFPFMFC_TABLE_FULL   = ##(1) /* Flow not added because table was full. */
  val OFPFMFC_BAD_TABLE_ID = ##(2) /* Table does not exist */
  val OFPFMFC_OVERLAP      = ##(3) /* Attempted to add overlapping flow with CHECK_OVERLAP flag set. */
  val OFPFMFC_EPERM        = ##(4) /* Permissions error. */
  val OFPFMFC_BAD_TIMEOUT  = ##(5) /* Flow not added because of unsupported idle/hard timeout. */
  val OFPFMFC_BAD_COMMAND  = ##(6) /* Unsupported or unknown command. */
  val OFPFMFC_BAD_FLAGS    = ##(7) /* Unsupported or unknown flags. */
//}

/* ofp_error_msg 'code' values for OFPET_GROUP_MOD_FAILED. 'data' contains
* at least the first 64 bytes of the failed request. */
//object ofp_group_mod_failed_code extends WordEnum{
  val OFPGMFC_GROUP_EXISTS         = ##(0)  /* Group not added because a group ADD attempted to replace an already-present group. */
  val OFPGMFC_INVALID_GROUP        = ##(1)  /* Group not added because Group specified is invalid. */
  val OFPGMFC_WEIGHT_UNSUPPORTED   = ##(2)  /* Switch does not support unequal load sharing with select groups. */
  val OFPGMFC_OUT_OF_GROUPS        = ##(3)  /* The group table is full. */
  val OFPGMFC_OUT_OF_BUCKETS       = ##(4)  /* The maximum number of action buckets for a group has been exceeded. */
  val OFPGMFC_CHAINING_UNSUPPORTED = ##(5)  /* Switch does not support groups that forward to groups. */
  val OFPGMFC_WATCH_UNSUPPORTED    = ##(6)  /* This group cannot watch the watch_port or watch_group specified. */
  val OFPGMFC_LOOP                 = ##(7)  /* Group entry would cause a loop. */
  val OFPGMFC_UNKNOWN_GROUP        = ##(8)  /* Group not modified because a group MODIFY attempted to modify a non-existent group. */
  val OFPGMFC_CHAINED_GROUP        = ##(9)  /* Group not deleted because another group is forwarding to it. */
  val OFPGMFC_BAD_TYPE             = ##(10) /* Unsupported or unknown group type. */
  val OFPGMFC_BAD_COMMAND          = ##(11) /* Unsupported or unknown command. */
  val OFPGMFC_BAD_BUCKET           = ##(12) /* Error in bucket. */
  val OFPGMFC_BAD_WATCH            = ##(13) /* Error in watch port/group. */
  val OFPGMFC_EPERM                = ##(14) /* Permissions error. */
//}

/* ofp_error_msg 'code' values for OFPET_PORT_MOD_FAILED. 'data' contains
* at least the first 64 bytes of the failed request. */
//object ofp_port_mod_failed_code extends WordEnum{
  val OFPPMFC_BAD_PORT      = ##(0) /* Specified port number does not exist. */
  val OFPPMFC_BAD_HW_ADDR   = ##(1) /* Specified hardware address does not match the port number. */
  val OFPPMFC_BAD_CONFIG    = ##(2) /* Specified config is invalid. */
  val OFPPMFC_BAD_ADVERTISE = ##(3) /* Specified advertise is invalid. */
  val OFPPMFC_EPERM         = ##(4) /* Permissions error. */
//}

/* ofp_error_msg 'code' values for OFPET_TABLE_MOD_FAILED. 'data' contains
* at least the first 64 bytes of the failed request. */
//object ofp_table_mod_failed_code extends WordEnum{
  val OFPTMFC_BAD_TABLE  = ##(0) /* Specified table does not exist. */
  val OFPTMFC_BAD_CONFIG = ##(1) /* Specified config is invalid. */
  val OFPTMFC_EPERM      = ##(2) /* Permissions error. */
//}

/* ofp_error msg 'code' values for OFPET_QUEUE_OP_FAILED. 'data' contains
* at least the first 64 bytes of the failed request */
//object ofp_queue_op_failed_code extends WordEnum{
  val OFPQOFC_BAD_PORT  = ##(0) /* Invalid port (or port does not exist}. */
  val OFPQOFC_BAD_QUEUE = ##(1) /* Queue does not exist. */
  val OFPQOFC_EPERM     = ##(2) /* Permissions error. */
//}
/* ofp_error_msg 'code' values for OFPET_SWITCH_CONFIG_FAILED. 'data' contains
* at least the first 64 bytes of the failed request. */
//object ofp_switch_config_failed_code extends WordEnum{
  val OFPSCFC_BAD_FLAGS = ##(0) /* Specified flags is invalid. */
  val OFPSCFC_BAD_LEN   = ##(1) /* Specified len is invalid. */
  val OFPSCFC_EPERM     = ##(2) /* Permissions error. */
//}

/* ofp_error_msg 'code' values for OFPET_ROLE_REQUEST_FAILED. 'data' contains
* at least the first 64 bytes of the failed request. */
//object ofp_role_request_failed_code extends WordEnum{
  val OFPRRFC_STALE    = ##(0) /* Stale Message: old generation_id. */
  val OFPRRFC_UNSUP    = ##(1) /* Controller role change unsupported. */
  val OFPRRFC_BAD_ROLE = ##(2) /* Invalid role. */
//}

/* ofp_error_msg 'code' values for OFPET_METER_MOD_FAILED. 'data' contains
* at least the first 64 bytes of the failed request. */
//object ofp_meter_mod_failed_code extends WordEnum{
  val OFPMMFC_UNKNOWN        = ##(0)  /* Unspecified error. */
  val OFPMMFC_METER_EXISTS   = ##(1)  /* Meter not added because a Meter ADD attempted to replace an existing Meter. */
  val OFPMMFC_INVALID_METER  = ##(2)  /* Meter not added because Meter specified is invalid. */
  val OFPMMFC_UNKNOWN_METER  = ##(3)  /* Meter not modified because a Meter MODIFY attempted to modify a non-existent Meter. */
  val OFPMMFC_BAD_COMMAND    = ##(4)  /* Unsupported or unknown command. */
  val OFPMMFC_BAD_FLAGS      = ##(5)  /* Flag configuration unsupported. */
  val OFPMMFC_BAD_RATE       = ##(6)  /* Rate unsupported. */
  val OFPMMFC_BAD_BURST      = ##(7)  /* Burst size unsupported. */
  val OFPMMFC_BAD_BAND       = ##(8)  /* Band unsupported. */
  val OFPMMFC_BAD_BAND_VALUE = ##(9)  /* Band value unsupported. */
  val OFPMMFC_OUT_OF_METERS  = ##(10) /* No more meters available. */
  val OFPMMFC_OUT_OF_BANDS   = ##(11) /* The maximum number of properties for a meter has been exceeded. */
//}

/* ofp_error_msg 'code' values for OFPET_TABLE_FEATURES_FAILED. 'data' contains
* at least the first 64 bytes of the failed request. */
//object ofp_table_features_failed_code extends WordEnum{
  val OFPTFFC_BAD_TABLE    = ##(0) /* Specified table does not exist. */
  val OFPTFFC_BAD_METADATA = ##(1) /* Invalid metadata mask. */
  val OFPTFFC_BAD_TYPE     = ##(2) /* Unknown property type. */
  val OFPTFFC_BAD_LEN      = ##(3) /* Length problem in properties. */
  val OFPTFFC_BAD_ARGUMENT = ##(4) /* Unsupported property value. */
  val OFPTFFC_EPERM        = ##(5) /* Permissions error. */
//}
}

/*   val OFPT_ERROR: Error message (datapath -> controller}. */
case class ofp_error_msg private[protocol] (
  header: ofp_header,
  eType: OFP_ERROR_TYPE,
  eCode: OFP_ERROR_CODE, //interpretation depends on error type
  data: RawSeq[UInt8])   /* Variable-length data. Interpreted based on the type and code. No padding. */

object ofp_error_msg extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = { case 3 =>  bodyLengthMinus(4) }
}

/*   val OFPET_EXPERIMENTER: Error message (datapath -> controller}. */
case class ofp_error_experimenter_msg private[protocol] (
  header: ofp_header,
  eType: OFP_ERROR_TYPE,
  exp_type: UInt16,       /* Experimenter defined. */
  experimenter: UInt32,   /* Experimenter ID which takes the same form as in struct ofp_experimenter_header. */
  data: RawSeq[UInt8])    /* Variable-length data. Interpreted based on the type and code. No padding. */

object ofp_error_experimenter_msg extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = { case 3 => ??? } // TODO: Implement
}






