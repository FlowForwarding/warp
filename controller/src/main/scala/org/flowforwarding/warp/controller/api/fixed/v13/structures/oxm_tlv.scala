/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.structures.oxm_tlv

import spire.math._

import java.nio.ByteBuffer
import scala.util.Try

import org.flowforwarding.warp.controller.api.dynamic.{DynamicBuilderInput, DynamicStructure}
import org.flowforwarding.warp.controller.api.fixed.util._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.util.{IPv6Address, MacAddress, ISID, IPv4Address}
import org.flowforwarding.warp.controller.api.fixed.v13.structures.oxm_tlv.OxmMatchFields.OxmMatchFields
import org.flowforwarding.warp.controller.api.fixed.v13.structures.oxm_tlv.OxmClass.OxmClass
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

object OxmMatchFields extends Enumeration {
  type OxmMatchFields = Value
  val IN_PORT,
      IN_PHY_PORT,
      METADATA,
      ETH_DST,
      ETH_SRC,
      ETH_TYPE,
      VLAN_VID,
      VLAN_PCP,
      IP_DSCP,
      IP_ECN,
      IP_PROTO,
      IPV4_SRC,
      IPV4_DST,
      TCP_SRC,
      TCP_DST,
      UDP_SRC,
      UDP_DST,
      SCTP_SRC,
      SCTP_DST,
      ICMPV4_TYPE,
      ICMPV4_CODE,
      ARP_OP,
      ARP_SPA,
      ARP_TPA,
      ARP_SHA,
      ARP_THA,
      IPV6_SRC,
      IPV6_DST,
      IPV6_FLABEL,
      ICMPV6_TYPE,
      ICMPV6_CODE,
      IPV6_ND_TARGET,
      IPV6_ND_SLL,
      IPV6_ND_TLL,
      MPLS_LABEL,
      MPLS_TC,
      MPLS_BOS,
      PBB_ISID,
      TUNNEL_ID,
      IPV6_EXTHDR = Value
}
import OxmMatchFields._

object OxmClass extends Enumeration {
  type OxmClass = Value
  val NXM0 = Value(0) /* Backward compatibility with NXM */
  val NXM1 = Value(1) /* Backward compatibility with NXM */
  val OpenflowBasic = Value(0x8000) /* Basic class for OpenFlow */
  val Experimenter = Value(0xFFFF)
}

abstract class OxmTlv[T: HasSize] extends BuilderInput{
  protected val valueSize = implicitly[HasSize[T]].size
  val value: T

  /* Header */
  def oxmClass: OxmClass = OxmClass.OpenflowBasic
  def hasMask: Boolean = false
  def oxmField: OxmMatchFields
  def oxmLength: UByte = valueSize
  private[fixed] def header: UInt = OxmHeader(oxmClass, hasMask, oxmField, oxmLength)
  private[fixed] def data: Array[Byte] = implicitly[HasSize[T]].bytes(value)
}

object OxmHeader{
  type OxmData = (OxmClass, Boolean, OxmMatchFields, UByte)

  val deconstruct: UInt => OxmData = (i: UInt) => {
    val oxmClass = i >> 16
    val hm = (i >> 8) & UInt(1)
    val fields = (i >> 9) & UInt(0x7f)
    val length = i & UInt(0xff)
    (OxmClass(oxmClass.toInt), hm == UInt(1), OxmMatchFields(fields.toInt), UByte(length.toShort))
  }

  val construct: OxmData => UInt = (apply _).tupled

  def unapply(i: UInt): Option[(OxmClass, Boolean, OxmMatchFields, UByte)] = Some(deconstruct(i))

  def apply(oxmClass: OxmClass, hasMask: Boolean, oxmField: OxmMatchFields, length: UByte): UInt =
    UInt(oxmClass.id << 16 | oxmField.id << 9 | (if (hasMask) 1 << 8 else 0) | length.toInt)
}

private[fixed] abstract class MaskableTlv[T: HasSize] extends OxmTlv[T] {
  override def hasMask: Boolean = mask.isDefined
  override def oxmLength: UByte = if (mask.isDefined) valueSize * UByte(2) else valueSize
  override def data: Array[Byte] = super.data ++ (mask map implicitly[HasSize[T]].bytes).getOrElse(Array.empty)
  val mask: Option[T]
}

case class in_port(value: UInt) extends OxmTlv[UInt] { val oxmField = IN_PORT }// Switch input port
case class in_phy_port(value: UInt) extends OxmTlv[UInt] { val oxmField = IN_PHY_PORT }// Switch physical input port

case class metadata(value: ULong, mask: Option[ULong] = None) extends MaskableTlv[ULong] { val oxmField = METADATA } // Metadata passed between tables

case class eth_dst(value: MacAddress, mask: Option[MacAddress] = None) extends MaskableTlv[MacAddress] { val oxmField = ETH_DST } // Ethernet destination address
case class eth_src(value: MacAddress, mask: Option[MacAddress] = None) extends MaskableTlv[MacAddress] { val oxmField = ETH_SRC } // Ethernet source address
case class eth_type(value: UShort) extends OxmTlv[UShort] { val oxmField = ETH_TYPE } //	Ethernet frame type

case class vlan_vid(value: UShort, mask: Option[UShort] = None) extends MaskableTlv[UShort] { val oxmField = VLAN_VID } //	VLAN id
case class vlan_pcp(value: UByte) extends OxmTlv[UByte] { val oxmField = VLAN_PCP } // VLAN priority

case class ip_dscp(value: UByte) extends OxmTlv[UByte] { val oxmField = IP_DSCP } // IP DSCP (6 bits in ToS field)
case class ip_ecn(value: UByte) extends OxmTlv[UByte] { val oxmField = IP_ECN }// IP ECN (2 bits in ToS field)
case class ip_proto(value: UByte) extends OxmTlv[UByte] { val oxmField = IP_PROTO } // IP protocol

case class ipv4_src(value: IPv4Address, mask: Option[IPv4Address] = None) extends MaskableTlv[IPv4Address] { val oxmField = IPV4_SRC } // IPv4 source address
case class ipv4_dst(value: IPv4Address, mask: Option[IPv4Address] = None) extends MaskableTlv[IPv4Address] { val oxmField = IPV4_DST } // IPv4 destination address

case class tcp_src(value: UShort) extends OxmTlv[UShort] { val oxmField = TCP_SRC } // TCP source port
case class tcp_dst(value: UShort) extends OxmTlv[UShort] { val oxmField = TCP_DST } // TCP destination port

case class udp_src(value: UShort) extends OxmTlv[UShort] { val oxmField = UDP_SRC } // UDP source port
case class udp_dst(value: UShort) extends OxmTlv[UShort] { val oxmField = UDP_DST } // UDP destination port

case class sctp_src(value: UShort) extends OxmTlv[UShort] { val oxmField = SCTP_SRC } // SCTP source port
case class sctp_dst(value: UShort) extends OxmTlv[UShort] { val oxmField = SCTP_DST } // SCTP destination port

case class icmpv4_type(value: UByte) extends OxmTlv[UByte] { val oxmField = ICMPV4_TYPE } // ICMP type
case class icmpv4_code(value: UByte) extends OxmTlv[UByte] { val oxmField = ICMPV4_CODE } // ICMP code

case class arp_op(value: UShort) extends OxmTlv[UShort] { val oxmField = ARP_OP } // ARP opcode
case class arp_spa(value: IPv4Address, mask: Option[IPv4Address] = None) extends MaskableTlv[IPv4Address] { val oxmField = ARP_SPA } // ARP source IPv4 address
case class arp_tpa(value: IPv4Address, mask: Option[IPv4Address] = None) extends MaskableTlv[IPv4Address] { val oxmField = ARP_TPA } // ARP target IPv4 address
case class arp_sha(value: MacAddress, mask: Option[MacAddress] = None) extends MaskableTlv[MacAddress] { val oxmField = ARP_SHA } // ARP source hardware address
case class arp_tha(value: MacAddress, mask: Option[MacAddress] = None) extends MaskableTlv[MacAddress] { val oxmField = ARP_THA } // ARP target hardware address

case class ipv6_src(value: IPv6Address, mask: Option[IPv6Address] = None) extends MaskableTlv[IPv6Address] { val oxmField = IPV6_SRC } // IPv6 source address
case class ipv6_dst(value: IPv6Address, mask: Option[IPv6Address] = None) extends MaskableTlv[IPv6Address] { val oxmField = IPV6_DST } // IPv6 destination address
case class ipv6_flabel(value: UInt) extends OxmTlv[UInt] { val oxmField = IPV6_FLABEL } // IPv6 Flow Label

case class icmpv6_type(value: UByte) extends OxmTlv[UByte] { val oxmField = ICMPV6_TYPE } // ICMPv6 type
case class icmpv6_code(value: UByte) extends OxmTlv[UByte] { val oxmField = ICMPV6_CODE } // ICMPv6 code

case class ipv6_nd_target(value: IPv4Address) extends OxmTlv[IPv4Address] { val oxmField = IPV6_ND_TARGET } // Target address for ND
case class ipv6_nd_sll(value: MacAddress) extends OxmTlv[MacAddress] { val oxmField = IPV6_ND_SLL } // Source link-layer for ND
case class ipv6_nd_tll(value: MacAddress) extends OxmTlv[MacAddress] { val oxmField = IPV6_ND_TLL }// Target link-layer for ND

case class mpls_label(value: UInt) extends OxmTlv[UInt] { val oxmField = MPLS_LABEL } // MPLS label
case class mpls_tc(value: UByte) extends OxmTlv[UByte] { val oxmField = MPLS_TC } // MPLS TC
case class mpls_bos(value: UByte) extends OxmTlv[UByte] { val oxmField = MPLS_BOS } // MPLS BoS bit

case class pbb_isid(value: ISID, mask: Option[ISID] = None) extends MaskableTlv[ISID] { val oxmField = PBB_ISID } // PBB I-SID

case class tunnel_id(value: ULong, mask: Option[ULong] = None) extends MaskableTlv[ULong] { val oxmField = TUNNEL_ID } // Logical Port Metadata

case class ipv6_exthdr(value: UShort, mask: Option[UShort] = None) extends MaskableTlv[UShort] { val oxmField = IPV6_EXTHDR } // IPv6 Extension Header pseudo-field

private[fixed] trait Ofp13OxmTlvDescription extends StructureDescription{
  apiProvider: StructuresDescriptionHelper =>

  protected[fixed] implicit object OxmTlv extends FromDynamic[OxmTlv[_]] with ToDynamic[OxmTlv[_]]{
    def opt[T](f: => T) = Try(f).toOption

    private val b = new OxmTlvBuilder

    val fromDynamic: PartialFunction[DynamicStructure, OxmTlv[_]] = { case s =>
      val OxmHeader(_, _, oxmField, _) = UInt(s.primitiveField(b.mapFieldName("header")))
      val data = ByteBuffer.wrap(s.primitivesSequence(b.mapFieldName("data")) map { _.toByte })
      oxmField match {
        case IN_PORT        => in_port(data.getUInt)
        case IN_PHY_PORT    => in_phy_port(data.getUInt)

        case METADATA       => metadata(data.getULong, opt(data.getULong))

        case ETH_DST        => eth_dst(data.getMacAddress, opt(data.getMacAddress))
        case ETH_SRC        => eth_src(data.getMacAddress, opt(data.getMacAddress))
        case ETH_TYPE       => eth_type(data.getUShort)

        case VLAN_VID       => vlan_vid(data.getUShort, opt(data.getUShort))
        case VLAN_PCP       => vlan_pcp(data.getUByte)

        case IP_DSCP        => ip_dscp(data.getUByte)
        case IP_ECN         => ip_ecn(data.getUByte)
        case IP_PROTO       => ip_proto(data.getUByte)

        case IPV4_SRC       => ipv4_src(data.getIPv4Address, opt(data.getIPv4Address))
        case IPV4_DST       => ipv4_dst(data.getIPv4Address, opt(data.getIPv4Address))

        case TCP_SRC        => tcp_src(data.getUShort)
        case TCP_DST        => tcp_dst(data.getUShort)

        case UDP_SRC        => udp_src(data.getUShort)
        case UDP_DST        => udp_dst(data.getUShort)

        case SCTP_SRC       => sctp_src(data.getUShort)
        case SCTP_DST       => sctp_dst(data.getUShort)

        case ICMPV4_TYPE    => icmpv4_type(data.getUByte)
        case ICMPV4_CODE    => icmpv4_code(data.getUByte)

        case ARP_OP         => arp_op(data.getUShort)
        case ARP_SPA        => arp_spa(data.getIPv4Address, opt(data.getIPv4Address))
        case ARP_TPA        => arp_tpa(data.getIPv4Address, opt(data.getIPv4Address))
        case ARP_SHA        => arp_sha(data.getMacAddress, opt(data.getMacAddress))
        case ARP_THA        => arp_tha(data.getMacAddress, opt(data.getMacAddress))

        case IPV6_SRC       => ipv6_src(data.getIPv6Address, opt(data.getIPv6Address))
        case IPV6_DST       => ipv6_dst(data.getIPv6Address, opt(data.getIPv6Address))
        case IPV6_FLABEL    => ipv6_flabel(data.getUInt)

        case ICMPV6_TYPE    => icmpv6_type(data.getUByte)
        case ICMPV6_CODE    => icmpv6_code(data.getUByte)

        case IPV6_ND_TARGET => ipv6_nd_target(data.getIPv4Address)
        case IPV6_ND_SLL    => ipv6_nd_sll(data.getMacAddress)
        case IPV6_ND_TLL    => ipv6_nd_tll(data.getMacAddress)

        case MPLS_LABEL     => mpls_label(data.getUInt)
        case MPLS_TC        => mpls_tc(data.getUByte)
        case MPLS_BOS       => mpls_bos(data.getUByte)

        case PBB_ISID       => pbb_isid(data.getISID, opt(data.getISID))

        case TUNNEL_ID      => tunnel_id(data.getULong, opt(data.getULong))

        case IPV6_EXTHDR    => ipv6_exthdr(data.getUShort, opt(data.getUShort))
      }
    }
    
    val toDynamic: PartialFunction[OxmTlv[_], DynamicBuilderInput] = { case h => new OxmTlvBuilder toDynamicInput h }
  }

  private class OxmTlvBuilder extends OfpStructureBuilder[OxmTlv[_]]{
    protected def applyInput(input: OxmTlv[_]): Unit = {
      setMember("header", input.header)
      setMember("data", input.data)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): OxmTlv[_] = string("field") match {
      case "in_port"        => in_port("value")
      case "in_phy_port"    => in_phy_port("value")

      case "metadata"       => metadata("value", "mask")

      case "eth_dst"        => eth_dst("value", "mask")
      case "eth_src"        => eth_src("value", "mask")
      case "eth_type"       => eth_type("value")

      case "vlan_vid"       => vlan_vid("value", "mask")
      case "vlan_pcp"       => vlan_pcp("value")

      case "ip_dscp"        => ip_dscp("value")
      case "ip_ecn"         => ip_ecn("value")
      case "ip_proto"       => ip_proto("value")

      case "ipv4_src"       => ipv4_src("value", "mask")
      case "ipv4_dst"       => ipv4_dst("value", "mask")

      case "tcp_src"        => tcp_src("value")
      case "tcp_dst"        => tcp_dst("value")

      case "udp_src"        => udp_src("value")
      case "udp_dst"        => udp_dst("value")

      case "sctp_src"       => sctp_src("value")
      case "sctp_dst"       => sctp_dst("value")

      case "icmpv4_type"    => icmpv4_type("value")
      case "icmpv4_code"    => icmpv4_code("value")

      case "arp_op"         => arp_op("value")
      case "arp_spa"        => arp_spa("value", "mask")
      case "arp_tpa"        => arp_tpa("value", "mask")
      case "arp_sha"        => arp_sha("value", "mask")
      case "arp_tha"        => arp_tha("value", "mask")

      case "ipv6_src"       => ipv6_src("value", "mask")
      case "ipv6_dst"       => ipv6_dst("value", "mask")
      case "ipv6_flabel"    => ipv6_flabel("value")

      case "icmpv6_type"    => icmpv6_type("value")
      case "icmpv6_code"    => icmpv6_code("value")

      case "ipv6_nd_target" => ipv6_nd_target("value")
      case "ipv6_nd_sll"    => ipv6_nd_sll("value")
      case "ipv6_nd_tll"    => ipv6_nd_tll("value")

      case "mpls_label"     => mpls_label("value")
      case "mpls_tc"        => mpls_tc("value")
      case "mpls_bos"       => mpls_bos("value")

      case "pbb_isid"       => pbb_isid("value", "mask")

      case "tunnel_id"      => tunnel_id("value", "mask")

      case "ipv6_exthdr"    => ipv6_exthdr("value", "mask")
    }
  }

  protected abstract override def builderClasses = classOf[OxmTlvBuilder] :: super.builderClasses
}


