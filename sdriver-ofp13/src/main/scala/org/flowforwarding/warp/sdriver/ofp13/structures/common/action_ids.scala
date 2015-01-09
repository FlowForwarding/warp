/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._
import com.gensler.scalavro.util.Union.union


import ofp_action_type._
import action_length._

object ofp_action_ids {
  type All = union [ofp_action_id_output]             #or // OFPAT_OUTPUT       = ##(0)       /* Output to switch port. */
                   [ofp_action_id_copy_ttl_out]       #or // OFPAT_COPY_TTL_OUT = ##(11)      /* Copy TTL "outwards" -- from next-to-outermost to outermost */
                   [ofp_action_id_copy_ttl_in]        #or // OFPAT_COPY_TTL_IN  = ##(12)      /* Copy TTL "inwards" -- from outermost to next-to-outermost */
                   [ofp_action_id_mpls_ttl]           #or // OFPAT_SET_MPLS_TTL = ##(15)      /* MPLS TTL */
                   [ofp_action_id_dec_mpls_ttl]       #or // OFPAT_DEC_MPLS_TTL = ##(16)      /* Decrement MPLS TTL */
                   [ofp_action_id_push_vlan]          #or // OFPAT_PUSH_VLAN    = ##(17)      /* Push a new VLAN tag */
                   [ofp_action_id_pop_vlan]           #or // OFPAT_POP_VLAN     = ##(18)      /* Pop the outer VLAN tag */
                   [ofp_action_id_push_mpls]          #or // OFPAT_PUSH_MPLS    = ##(19)      /* Push a new MPLS tag */
                   [ofp_action_id_pop_mpls]           #or // OFPAT_POP_MPLS     = ##(20)      /* Pop the outer MPLS tag */
                   [ofp_action_id_set_queue]          #or // OFPAT_SET_QUEUE    = ##(21)      /* Set queue id when outputting to a port */
                   [ofp_action_id_group]              #or // OFPAT_GROUP        = ##(22)      /* Apply group. */
                   [ofp_action_id_nw_ttl]             #or // OFPAT_SET_NW_TTL   = ##(23)      /* IP TTL. */
                   [ofp_action_id_dec_nw_ttl]         #or // OFPAT_DEC_NW_TTL   = ##(24)      /* Decrement IP TTL. */
                   [ofp_action_id_set_field]          #or // OFPAT_SET_FIELD    = ##(25)      /* Set a header field using OXM TLV format. */
                   [ofp_action_id_push_pbb]           #or // OFPAT_PUSH_PBB     = ##(26)      /* Push a new PBB service tag (I-TAG) */
                   [ofp_action_id_pop_pbb]            #or // OFPAT_POP_PBB      = ##(27)      /* Pop the outer PBB service tag (I-TAG) */
                   [ofp_action_id_experimenter]           // OFPAT_EXPERIMENTER = ##(0xffffL)
}

trait ofp_action_id extends WordTaggedUnion[ofp_action_ids.All, OFP_ACTION_TYPE] {
  def len: action_length.OFP_ACTION_LENGTH
}

/* Sorts of simple header with different tags */
case class ofp_action_id_copy_ttl_out private [sdriver] (len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_copy_ttl_out {
  private [sdriver] def build() = ofp_action_id_copy_ttl_out(AL_COPY_TTL_OUT)
}

case class ofp_action_id_copy_ttl_in private [sdriver] (len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_copy_ttl_in {
  private [sdriver] def build() = ofp_action_id_copy_ttl_in(AL_COPY_TTL_IN)
}

case class ofp_action_id_dec_mpls_ttl private [sdriver] (len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_dec_mpls_ttl {
  private [sdriver] def build() = ofp_action_id_dec_mpls_ttl(AL_DEC_MPLS_TTL)
}

case class ofp_action_id_pop_vlan private [sdriver] (len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_pop_vlan {
  private [sdriver] def build() = ofp_action_id_pop_vlan(AL_POP_VLAN)
}

case class ofp_action_id_dec_nw_ttl private [sdriver] (len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_dec_nw_ttl {
  private [sdriver] def build() = ofp_action_id_dec_nw_ttl(AL_DEC_NW_TTL)
}

case class ofp_action_id_pop_pbb private [sdriver] (len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_pop_pbb {
  private [sdriver] def build() = ofp_action_id_pop_pbb(AL_POP_PBB)
}

case class ofp_action_id_output private [sdriver] (len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_output {
  private [sdriver] def build() = ofp_action_id_output(AL_OUTPUT)
}

case class ofp_action_id_group private [sdriver] (len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_group {
  def build() = ofp_action_id_group(AL_GROUP)
}

case class ofp_action_id_set_queue private [sdriver] (len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_set_queue {
  def build() = ofp_action_id_set_queue(AL_SET_QUEUE)
}

case class ofp_action_id_mpls_ttl(len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_mpls_ttl {
  def build() = ofp_action_id_mpls_ttl(AL_SET_MPLS_TTL)
}

case class ofp_action_id_nw_ttl private [sdriver] (len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_nw_ttl {
  def build() = ofp_action_id_nw_ttl(AL_SET_NW_TTL)
}

// type ==  = OFPAT_PUSH_VLAN/MPLS/PBB, len too
case class ofp_action_id_push_vlan private [sdriver] (len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_push_vlan {
  def build() = ofp_action_id_push_vlan(AL_PUSH_VLAN) }

case class ofp_action_id_push_mpls private [sdriver] (len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_push_mpls {
  def build() = ofp_action_id_push_mpls(AL_PUSH_MPLS)
}

case class ofp_action_id_push_pbb private [sdriver] (len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_push_pbb {
  def build() = ofp_action_id_push_pbb(AL_PUSH_PBB)
}

case class ofp_action_id_pop_mpls private [sdriver] (len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_pop_mpls {
  def build() = ofp_action_id_pop_mpls(AL_POP_MPLS)
}

case class ofp_action_id_set_field private [sdriver] (len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_set_field{
  private [sdriver] def build() = ofp_action_id_set_field(AL_SET_FIELD) // TODO ??
}

case class ofp_action_id_experimenter private [sdriver] (len: OFP_ACTION_LENGTH) extends ofp_action_id
object ofp_action_id_experimenter_header {
  private [sdriver] def build() = ofp_action_id_experimenter(AL_EXPERIMENTER)
}





















