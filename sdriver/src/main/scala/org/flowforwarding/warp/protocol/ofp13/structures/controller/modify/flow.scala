/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_flow_mod_command.OFP_FLOW_MOD_COMMAND
import org.flowforwarding.warp.protocol.ofp13.structures.BufferId.OFP_BUFFER_ID
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_port_no.OFP_PORT_NO
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_flow_mod_flags.OFP_FLOW_MOD_FLAGS
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_group.OFP_GROUP
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_length._
import org.flowforwarding.warp.protocol.dynamic.DynamicPath

/* Flow setup and teardown (controller -> datapath). */
case class ofp_flow_mod(header: ofp_header,
                        cookie: UInt64,                /* Opaque controller-issued identifier. */

                        cookie_mask: UInt64,           /* Mask used to restrict the cookie bits that must match when the command is
                                                          OFPFC_MODIFY* or OFPFC_DELETE*. A value of 0 indicates no restriction. */
                        /* Flow actions. */
                        table_id: UInt8,               /* ID of the table to put the flow in. For OFPFC_DELETE_* commands, OFPTT_ALL
                                                          can also be used to delete matching flows from all tables. */
                        command: OFP_FLOW_MOD_COMMAND, /* One of OFPFC_*. */
                        idle_timeout: UInt16,          /* Idle time before discarding (seconds). */
                        hard_timeout: UInt16,          /* Max time before discarding (seconds). */
                        priority: UInt16,              /* Priority level of flow entry. */
                        buffer_id: OFP_BUFFER_ID,      /* Buffered packet to apply to, or OFP_NO_BUFFER. Not meaningful for OFPFC_DELETE*. */
                        out_port: OFP_PORT_NO,         /* For OFPFC_DELETE* commands, require matching entries to include this as an
                                                          output port. A value of OFPP_ANY indicates no restriction. */


                        out_group: OFP_GROUP,          /* For OFPFC_DELETE* commands, require matching entries to include this as an output group.
                                                          A value of OFPG_ANY indicates no restriction. */
                        flags: OFP_FLOW_MOD_FLAGS,     /* Bitmap of OFPFF_* flags. */
                        pad: Pad2,
                        m: ofp_match,                  /* Fields to match. Variable size. */
                                                       /* The variable size and padded match is always followed by instructions. */
                        /* Instruction set - 0 or more. The length of the instruction  set is inferred from the length field in the header. */
                        instructions: RawSeq[ofp_instruction])

object ofp_flow_mod extends RawSeqFieldsInfo{
  private[protocol] def build(@DynamicPath("header", "xid") xid: UInt32,
                              cookie: UInt64,
                              cookie_mask: UInt64,
                              table_id: UInt8,
                              command: OFP_FLOW_MOD_COMMAND,
                              idle_timeout: UInt16,
                              hard_timeout: UInt16,
                              priority: UInt16,
                              buffer_id: OFP_BUFFER_ID,
                              out_port: OFP_PORT_NO,
                              out_group: OFP_GROUP,
                              flags: OFP_FLOW_MOD_FLAGS,
                              m: ofp_match,
                              instructions: RawSeq[ofp_instruction]= RawSeq()) = {
    val header = ofp_header(OFPL_FLOW_MOD_LEN + (-8) // length of match is calculated manually
                                              + ofp_match.totalLength(m)
                                              + instructions.foldLeft(0) { (s, b) => s + b.length.data }, xid)
    ofp_flow_mod(header,
                 cookie,
                 cookie_mask,
                 table_id,
                 command,
                 idle_timeout,
                 hard_timeout,
                 priority,
                 buffer_id,
                 out_port,
                 out_group,
                 flags,
                 Pad2(),
                 m,
                 instructions)
  }

  val rawFieldsLengthCalculator: LengthCalculator = { case 14 => ??? } // TODO: Implement

}

object ofp_flow_mod_command extends ByteEnum{
  type OFP_FLOW_MOD_COMMAND = Value

  val OFPFC_ADD           = ##(0) /* New flow. */
  val OFPFC_MODIFY        = ##(1) /* Modify all matching flows. */
  val OFPFC_MODIFY_STRICT = ##(2) /* Modify entry strictly matching wildcards and priority. */
  val OFPFC_DELETE        = ##(3) /* Delete all matching flows. */
  val OFPFC_DELETE_STRICT = ##(4) /* Delete entry strictly matching wildcards and priority. */
}

object ofp_flow_mod_flags extends WordBitmap{
  type OFP_FLOW_MOD_FLAGS = Value

  val OFPFF_SEND_FLOW_REM = ##(1 << 0) /* Send flow removed message when flow expires or is deleted. */
  val OFPFF_CHECK_OVERLAP = ##(1 << 1) /* Check for overlapping entries first. */
  val OFPFF_RESET_COUNTS  = ##(1 << 2) /* Reset flow packet and byte counts. */
  val OFPFF_NO_PKT_COUNTS = ##(1 << 3) /* Don't keep track of packet count. */
  val OFPFF_NO_BYT_COUNTS = ##(1 << 4) /* Don't keep track of byte count. */
}




