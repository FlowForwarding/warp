/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._
import com.gensler.scalavro.util.Union.union

object ofp_action_type extends WordEnum {
  type OFP_ACTION_TYPE = Value

  val OFPAT_OUTPUT       = ##(0)  /* Output to switch port. */
  val OFPAT_COPY_TTL_OUT = ##(11) /* Copy TTL "outwards" -- from next-to-outermost to outermost */
  val OFPAT_COPY_TTL_IN  = ##(12) /* Copy TTL "inwards" -- from outermost to next-to-outermost */
  val OFPAT_SET_MPLS_TTL = ##(15) /* MPLS TTL */
  val OFPAT_DEC_MPLS_TTL = ##(16) /* Decrement MPLS TTL */
  val OFPAT_PUSH_VLAN    = ##(17) /* Push a new VLAN tag */
  val OFPAT_POP_VLAN     = ##(18) /* Pop the outer VLAN tag */
  val OFPAT_PUSH_MPLS    = ##(19) /* Push a new MPLS tag */
  val OFPAT_POP_MPLS     = ##(20) /* Pop the outer MPLS tag */
  val OFPAT_SET_QUEUE    = ##(21) /* Set queue id when outputting to a port */
  val OFPAT_GROUP        = ##(22) /* Apply group. */
  val OFPAT_SET_NW_TTL   = ##(23) /* IP TTL. */
  val OFPAT_DEC_NW_TTL   = ##(24) /* Decrement IP TTL. */
  val OFPAT_SET_FIELD    = ##(25) /* Set a header field using OXM TLV format. */
  val OFPAT_PUSH_PBB     = ##(26) /* Push a new PBB service tag (I-TAG) */
  val OFPAT_POP_PBB      = ##(27) /* Pop the outer PBB service tag (I-TAG) */
  val OFPAT_EXPERIMENTER = ##(0xffffL)
}

object action_length extends WordEnum with AllowUnspecifiedValues[Short]{
  type OFP_ACTION_LENGTH = Value

  val AL_OUTPUT       = ##(16)
  val AL_COPY_TTL_OUT = ##(8)
  val AL_COPY_TTL_IN  = ##(8)
  val AL_SET_MPLS_TTL = ##(8)
  val AL_DEC_MPLS_TTL = ##(8)
  val AL_PUSH_VLAN    = ##(8)
  val AL_POP_VLAN     = ##(8)
  val AL_PUSH_MPLS    = ##(8)
  val AL_POP_MPLS     = ##(8)
  val AL_SET_QUEUE    = ##(8)
  val AL_GROUP        = ##(8)
  val AL_SET_NW_TTL   = ##(8)
  val AL_DEC_NW_TTL   = ##(8)
  val AL_PUSH_PBB     = ##(8)
  val AL_POP_PBB      = ##(8)
  val AL_EXPERIMENTER = ##(8)
  def AL_SET_FIELD(oxmDataLength: Int) = Unspecified((8 + oxmDataLength).toShort)
}

import org.flowforwarding.warp.protocol.ofp13.structures.ofp_controller_max_len.OFP_CONTROLLER_MAX_LEN
import ofp_action_type._
import action_length._

object ofp_actions {
  type All = union [ofp_action_output]             #or // OFPAT_OUTPUT       = ##(0)       /* Output to switch port. */
                   [ofp_action_copy_ttl_out]       #or // OFPAT_COPY_TTL_OUT = ##(11)      /* Copy TTL "outwards" -- from next-to-outermost to outermost */
                   [ofp_action_copy_ttl_in]        #or // OFPAT_COPY_TTL_IN  = ##(12)      /* Copy TTL "inwards" -- from outermost to next-to-outermost */
                   [ofp_action_mpls_ttl]           #or // OFPAT_SET_MPLS_TTL = ##(15)      /* MPLS TTL */
                   [ofp_action_dec_mpls_ttl]       #or // OFPAT_DEC_MPLS_TTL = ##(16)      /* Decrement MPLS TTL */
                   [ofp_action_push_vlan]          #or // OFPAT_PUSH_VLAN    = ##(17)      /* Push a new VLAN tag */
                   [ofp_action_pop_vlan]           #or // OFPAT_POP_VLAN     = ##(18)      /* Pop the outer VLAN tag */
                   [ofp_action_push_mpls]          #or // OFPAT_PUSH_MPLS    = ##(19)      /* Push a new MPLS tag */
                   [ofp_action_pop_mpls]           #or // OFPAT_POP_MPLS     = ##(20)      /* Pop the outer MPLS tag */
                   [ofp_action_set_queue]          #or // OFPAT_SET_QUEUE    = ##(21)      /* Set queue id when outputting to a port */
                   [ofp_action_group]              #or // OFPAT_GROUP        = ##(22)      /* Apply group. */
                   [ofp_action_nw_ttl]             #or // OFPAT_SET_NW_TTL   = ##(23)      /* IP TTL. */
                   [ofp_action_dec_nw_ttl]         #or // OFPAT_DEC_NW_TTL   = ##(24)      /* Decrement IP TTL. */
                   [ofp_action_set_field]          #or // OFPAT_SET_FIELD    = ##(25)      /* Set a header field using OXM TLV format. */
                   [ofp_action_push_pbb]           #or // OFPAT_PUSH_PBB     = ##(26)      /* Push a new PBB service tag (I-TAG) */
                   [ofp_action_pop_pbb]            #or // OFPAT_POP_PBB      = ##(27)      /* Pop the outer PBB service tag (I-TAG) */
                   [ofp_action_experimenter_header]    // OFPAT_EXPERIMENTER = ##(0xffffL)

}

trait ofp_action extends WordTaggedUnion[ofp_actions.All, OFP_ACTION_TYPE] {
  def len: action_length.OFP_ACTION_LENGTH
}

/* Sorts of simple header with different tags */
case class ofp_action_copy_ttl_out private [protocol] (len: OFP_ACTION_LENGTH, pad: Pad4) extends ofp_action
object ofp_action_copy_ttl_out {
  private [protocol] def build = ofp_action_copy_ttl_out(AL_COPY_TTL_OUT, Pad4())
}

case class ofp_action_copy_ttl_in private [protocol] (len: OFP_ACTION_LENGTH, pad: Pad4) extends ofp_action
object ofp_action_copy_ttl_in {
  private [protocol] def build = ofp_action_copy_ttl_in(AL_COPY_TTL_IN, Pad4())
}

case class ofp_action_dec_mpls_ttl private [protocol] (len: OFP_ACTION_LENGTH, pad: Pad4) extends ofp_action
object ofp_action_dec_mpls_ttl {
  private [protocol] def build = ofp_action_dec_mpls_ttl(AL_DEC_MPLS_TTL, Pad4())
}

case class ofp_action_pop_vlan private [protocol] (len: OFP_ACTION_LENGTH, pad: Pad4) extends ofp_action
object ofp_action_pop_vlan {
  private [protocol] def build = ofp_action_pop_vlan(AL_POP_VLAN, Pad4())
}

case class ofp_action_dec_nw_ttl private [protocol] (len: OFP_ACTION_LENGTH, pad: Pad4) extends ofp_action
object ofp_action_dec_nw_ttl {
  private [protocol] def build = ofp_action_dec_nw_ttl(AL_DEC_NW_TTL, Pad4())
}

case class ofp_action_pop_pbb private [protocol] (len: OFP_ACTION_LENGTH, pad: Pad4) extends ofp_action
object ofp_action_pop_pbb {
  private [protocol] def build = ofp_action_pop_pbb(AL_POP_PBB, Pad4())
}

/* Action structure for OFPAT_OUTPUT, which sends packets out 'port'.
 * When the 'port' is the OFPP_CONTROLLER, 'max_len' indicates the max
 * number of bytes to send. A 'max_len' of zero means no bytes of the
 * packet should be sent. A 'max_len' of OFPCML_NO_BUFFER means that
 * the packet is not buffered and the complete packet is to be sent to
 * the controller. */
case class ofp_action_output private [protocol] (
  len: OFP_ACTION_LENGTH,
  port: UInt32,                    /* Output port. */
  max_len: OFP_CONTROLLER_MAX_LEN, /* Max length to send to controller. */
  pad: Pad6) extends ofp_action    /* Pad to 64 bits. */


object ofp_action_output {
  private [protocol] def build(port: UInt32, max_len: OFP_CONTROLLER_MAX_LEN) = ofp_action_output(AL_OUTPUT, port, max_len, Pad6())
}

object ofp_controller_max_len extends WordEnum with AllowUnspecifiedValues[Short] {
  type OFP_CONTROLLER_MAX_LEN = Value
  val OFPCML_MAX       = ##(0xffe5L)
  val OFPCML_NO_BUFFER = ##(0xffffL)
}

/* Action structure for OFPAT_GROUP. */
case class ofp_action_group private [protocol] (len: OFP_ACTION_LENGTH, groupId: UInt32) extends ofp_action
object ofp_action_group { def build(groupId: UInt32) = ofp_action_group(AL_GROUP, groupId) }

/* OFPAT_SET_QUEUE action struct: send packets to given queue on port. */
case class ofp_action_set_queue private [protocol] (len: OFP_ACTION_LENGTH, queueId: UInt32) extends ofp_action
object ofp_action_set_queue {
  def build(queueId: UInt32) = ofp_action_set_queue(AL_SET_QUEUE, queueId) }

/* Action structure for OFPAT_SET_MPLS_TTL. */
case class ofp_action_mpls_ttl(len: OFP_ACTION_LENGTH, mpls_ttl: UInt8, pad: Pad3) extends ofp_action
object ofp_action_mpls_ttl {
  def build(mpls_ttl: UInt8) = ofp_action_mpls_ttl(AL_SET_MPLS_TTL, mpls_ttl, Pad3()) }

/* Action structure for OFPAT_SET_NW_TTL. */
case class ofp_action_nw_ttl private [protocol] (len: OFP_ACTION_LENGTH, nw_ttl: UInt8, pad: Pad3) extends ofp_action
object ofp_action_nw_ttl {
  def build(nw_ttl: UInt8) = ofp_action_nw_ttl(AL_SET_NW_TTL, nw_ttl, Pad3()) }

// type ==  = OFPAT_PUSH_VLAN/MPLS/PBB, len too
case class ofp_action_push_vlan private [protocol] (len: OFP_ACTION_LENGTH, ethertype: UInt16, pad: Pad2) extends ofp_action
object ofp_action_push_vlan {
  def build(ethertype: UInt16) = ofp_action_push_vlan(AL_PUSH_VLAN, ethertype, Pad2()) }

case class ofp_action_push_mpls private [protocol] (len: OFP_ACTION_LENGTH, ethertype: UInt16, pad: Pad2) extends ofp_action
object ofp_action_push_mpls {
  def build(ethertype: UInt16) = ofp_action_push_mpls(AL_PUSH_MPLS, ethertype, Pad2()) }

case class ofp_action_push_pbb private [protocol] (len: OFP_ACTION_LENGTH, ethertype: UInt16, pad: Pad2) extends ofp_action
object ofp_action_push_pbb {
  def build(ethertype: UInt16) = ofp_action_push_pbb(AL_PUSH_PBB, ethertype, Pad2()) }

/* Action structure for OFPAT_POP_MPLS. */
case class ofp_action_pop_mpls private [protocol] (len: OFP_ACTION_LENGTH, ethertype: UInt16, pad: Pad2) extends ofp_action
object ofp_action_pop_mpls {
  def build(ethertype: UInt16) = ofp_action_pop_mpls(AL_POP_MPLS, ethertype, Pad2())}

// NB: other actions are represented just by ofp_action_header with corresponding parameters.

case class ofp_action_set_field private [protocol] (
  len: OFP_ACTION_LENGTH, /* Length is padded to 64 bits. */
  /* Followed by:
   * - Exactly (4 + oxm_length) bytes containing a single OXM TLV, then
   * - Exactly ((8 + oxm_length) + 7)/8*8 - (8 + oxm_length)
   * (between 0 and 7) bytes of all-zero bytes
   */
  field: oxm_tlv,
  closingPad: RawSeq[UInt8]) extends ofp_action

object ofp_action_set_field extends RawSeqFieldsInfo{
  def len(s: Seq[Any]) = s(0).asInstanceOf[OFP_ACTION_LENGTH].data

  val rawFieldsLengthCalculator: LengthCalculator = {
    case 2 => (s: Seq[Any]) => closingPadSize(len(s) - 8)
  }

  private def closingPadSize(oxmLength: Int) = (oxmLength + 7) / 8 * 8 - (8 + oxmLength)

  private [protocol] def build(field: oxm_tlv) = {
    val oxmLength = field.entry.length
    ofp_action_set_field(AL_SET_FIELD(oxmLength + closingPadSize(oxmLength)), field, RawSeq(Seq.fill(oxmLength)(UInt8()): _*))
  }
}

/* Experimenter ID which takes the same form as in struct ofp_experimenter_header. */
case class ofp_action_experimenter_header private [protocol] (len: OFP_ACTION_LENGTH, experimenter: UInt32) extends ofp_action
object ofp_action_experimenter_header {
  private [protocol] def build(experimenter: UInt32) = ofp_action_experimenter_header(AL_EXPERIMENTER, experimenter)
}





















