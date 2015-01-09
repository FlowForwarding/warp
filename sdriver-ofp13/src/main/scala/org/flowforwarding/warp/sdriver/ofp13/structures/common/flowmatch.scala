/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_match_type.OFP_MATCH_TYPE
import spire.math.{UByte, UShort}
import org.flowforwarding.warp.sdriver.ofp13.structures.oxm_ofb_match_field.OXM_OFB_MATCH_FIELD

/* Fields to match against flows */
case class ofp_match private[sdriver] (
  mType: OFP_MATCH_TYPE, /* One of OFPMT_* */
  length: UInt16,        /* Length of ofp_match (excluding padding) */
  /* Followed by:
  * - Exactly (length - 4) (possibly 0) bytes containing OXM TLVs, then
  * - Exactly ((length + 7)/8*8 - length) (between 0 and 7) bytes of
  * all-zero bytes
  * In summary, ofp_match is padded as needed, to make its overall size
  * a multiple of 8, to preserve alignement in structures using it.
  */
  fields: RawSeq[oxm_tlv],      /* 0 or more OXM match fields */
  closingPad: RawSeq[UInt8])    /* Zero bytes - see above for sizing */

object ofp_match extends RawSeqFieldsInfo{
  private[sdriver] def build(mType: OFP_MATCH_TYPE, fields: RawSeq[oxm_tlv]) ={
    val matchLength = fields.foldLeft(4) { (s, oxm) =>
      val OxmTlvHeader(_, _, _, length) = oxm.header
      s + 4 + length.toInt
    }
    val closingPadBytes = Seq.fill(closingPadLength(matchLength)) { UInt8() }
    ofp_match(mType, UInt16.fromShort(matchLength.toShort), fields, RawSeq(closingPadBytes: _*))
  }

  private def closingPadLength(matchLength: Int) = (matchLength + 7) / 8 * 8 - matchLength

  private[sdriver] def totalLength(m: ofp_match) = {
    val mLength = UInt16.toShort(m.length)
    mLength + closingPadLength(mLength)
  }

  val rawFieldsLengthCalculator: LengthCalculator = {
    case 2 => Some { (s: Seq[Any]) => lenField(1)(s) - 4 }
    case 3 => Some { (s: Seq[Any]) => closingPadLength(lenField(1)(s)) }
  }
}

object ofp_match_type extends WordEnum with AllowUnspecifiedValues[Short]{
  type OFP_MATCH_TYPE = Value
  val OFPMT_STANDARD = ##(0) /* Deprecated. */
  val OFPMT_OXM      = ##(1) /* OpenFlow Extensible Match */
}

/* OXM Flow match field types for OpenFlow basic class. */
object oxm_ofb_match_field extends ByteEnum {
  type OXM_OFB_MATCH_FIELD = Value

  val OFPXMT_OFB_IN_PORT        = ##(0)  /* Switch input port. */
  val OFPXMT_OFB_IN_PHY_PORT    = ##(1)  /* Switch physical input port. */
  val OFPXMT_OFB_METADATA       = ##(2)  /* Metadata passed between tables. */
  val OFPXMT_OFB_ETH_DST        = ##(3)  /* Ethernet destination address. */
  val OFPXMT_OFB_ETH_SRC        = ##(4)  /* Ethernet source address. */
  val OFPXMT_OFB_ETH_TYPE       = ##(5)  /* Ethernet frame type. */
  val OFPXMT_OFB_VLAN_VID       = ##(6)  /* VLAN id. */
  val OFPXMT_OFB_VLAN_PCP       = ##(7)  /* VLAN priority. */
  val OFPXMT_OFB_IP_DSCP        = ##(8)  /* IP DSCP (6 bits in ToS field). */
  val OFPXMT_OFB_IP_ECN         = ##(9)  /* IP ECN (2 bits in ToS field). */
  val OFPXMT_OFB_IP_PROTO       = ##(10) /* IP protocol. */
  val OFPXMT_OFB_IPV4_SRC       = ##(11) /* IPv4 source address. */
  val OFPXMT_OFB_IPV4_DST       = ##(12) /* IPv4 destination address. */
  val OFPXMT_OFB_TCP_SRC        = ##(13) /* TCP source port. */
  val OFPXMT_OFB_TCP_DST        = ##(14) /* TCP destination port. */
  val OFPXMT_OFB_UDP_SRC        = ##(15) /* UDP source port. */
  val OFPXMT_OFB_UDP_DST        = ##(16) /* UDP destination port. */
  val OFPXMT_OFB_SCTP_SRC       = ##(17) /* SCTP source port. */
  val OFPXMT_OFB_SCTP_DST       = ##(18) /* SCTP destination port. */
  val OFPXMT_OFB_ICMPV4_TYPE    = ##(19) /* ICMP type. */
  val OFPXMT_OFB_ICMPV4_CODE    = ##(20) /* ICMP code. */
  val OFPXMT_OFB_ARP_OP         = ##(21) /* ARP opcode. */
  val OFPXMT_OFB_ARP_SPA        = ##(22) /* ARP source IPv4 address. */
  val OFPXMT_OFB_ARP_TPA        = ##(23) /* ARP target IPv4 address. */
  val OFPXMT_OFB_ARP_SHA        = ##(24) /* ARP source hardware address. */
  val OFPXMT_OFB_ARP_THA        = ##(25) /* ARP target hardware address. */
  val OFPXMT_OFB_IPV6_SRC       = ##(26) /* IPv6 source address. */
  val OFPXMT_OFB_IPV6_DST       = ##(27) /* IPv6 destination address. */
  val OFPXMT_OFB_IPV6_FLABEL    = ##(28) /* IPv6 Flow Label */
  val OFPXMT_OFB_ICMPV6_TYPE    = ##(29) /* ICMPv6 type. */
  val OFPXMT_OFB_ICMPV6_CODE    = ##(30) /* ICMPv6 code. */
  val OFPXMT_OFB_IPV6_ND_TARGET = ##(31) /* Target address for ND. */
  val OFPXMT_OFB_IPV6_ND_SLL    = ##(32) /* Source link-layer for ND. */
  val OFPXMT_OFB_IPV6_ND_TLL    = ##(33) /* Target link-layer for ND. */
  val OFPXMT_OFB_MPLS_LABEL     = ##(34) /* MPLS label. */
  val OFPXMT_OFB_MPLS_TC        = ##(35) /* MPLS TC. */
  val OFPXMT_OFP_MPLS_BOS       = ##(36) /* MPLS BoS bit. */
  val OFPXMT_OFB_PBB_ISID       = ##(37) /* PBB I-SID. */
  val OFPXMT_OFB_TUNNEL_ID      = ##(38) /* Logical Port Metadata. */
  val OFPXMT_OFB_IPV6_EXTHDR    = ##(39) /* IPv6 Extension Header pseudo-field */
}

// and methods for generation of instances
// the two first form oxm_type, bits order: 31..0
case class oxm_tlv private[sdriver] (header: UInt32, entry: RawSeq[UInt8])
/* len(entry) == header.oxm_length */
/* masked_header.oxm_length = 2 * not_masked_header.oxm_length */

object oxm_tlv extends RawSeqFieldsInfo{
  private[sdriver] def build(header: UInt32, entry: RawSeq[UInt8] = RawSeq()) = oxm_tlv(header, entry)

  val rawFieldsLengthCalculator: LengthCalculator = { case 1 => Some { case Seq(OxmTlvHeader(_, _, _, length)) => length.toInt} }
}

object OxmTlvHeader{
  def apply(): UInt32 = ???
  def unapply(header: UInt32): Option[(UShort, OXM_OFB_MATCH_FIELD, Boolean, UByte)] = {
    val h = UInt32.toInt(header)
    val oxmClass = (h >> 16) & 0xffff
    val hm = (h >> 8) & 1
    val fields = (h >> 9) & 0x7f
    val length = h & 0xff
    Some(UShort(oxmClass),  oxm_ofb_match_field.valueFromLong(fields), hm == 1, UByte(length))
  }
}

/* OXM Class IDs.
* The high order bit differentiate reserved classes from member classes.
* Classes 0x0000 to 0x7FFF are member classes, allocated by ONF.
* Classes 0x8000 to 0xFFFE are reserved classes, reserved for standardisation.
*/
object ofp_oxm_class extends WordEnum { // undefined values accepted
  type OFP_OXM_CLASS = Value
  val OFPXMC_NXM_0          = ##(0x0000L) /* Backward compatibility with NXM */
  val OFPXMC_NXM_1          = ##(0x0001L) /* Backward compatibility with NXM */
  val OFPXMC_OPENFLOW_BASIC = ##(0x8000L) /* Basic class for OpenFlow */
  val OFPXMC_EXPERIMENTER   = ##(0xffL)   /* Experimenter class */
}
