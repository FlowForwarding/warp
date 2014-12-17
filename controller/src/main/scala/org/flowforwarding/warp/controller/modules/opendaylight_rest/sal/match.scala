/*
* © 2013 FlowForwarding.Org
* All Rights Reserved.  Use is subject to license terms.
*
* @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
*/
package org.flowforwarding.warp.controller.modules.opendaylight_rest.sal

import java.net.InetAddress

import spire.math.{UByte, UShort}

import scala.util.Try

sealed trait MatchField{
  def `type`: String
  def stringValue: String
}

object MatchField{
  val IngressPortName =  "ingressPort"
  val VlanIdName =       "vlanId"
  val VlanPriorityName = "vlanPriority"
  val EtherTypeName =    "etherType"
  val DlSrcName =        "dlSrc"
  val DlDstName =        "dlDst"
  val ProtocolName =     "protocol"
  val TosBitsName =      "tosBits"
  val NwSrcName =        "nwSrc"
  val NwDstName =        "nwDst"
  val TpSrcName =        "tpSrc"
  val TpDstName =        "tpDst"

  val names = Array(IngressPortName,
                    VlanIdName,
                    VlanPriorityName,
                    EtherTypeName,
                    DlSrcName,
                    DlDstName,
                    ProtocolName,
                    TosBitsName,
                    NwSrcName,
                    NwDstName,
                    TpSrcName,
                    TpDstName)

  def apply(name: String, value: String): Try[MatchField] = Try {
    import java.lang.Byte.{decode => toByte}
    import java.lang.Integer.{decode => toInt}
    // TODO: check ranges
    name match {
      case `IngressPortName` =>  IngressPort (NodeConnector.parse(value).get)
      case `VlanIdName` =>       VlanId      (UShort(toInt(value)))
      case `VlanPriorityName` => VlanPriority(UByte(toByte(value)))
      case `EtherTypeName` =>    EtherType   (UShort(toInt(value)))
      case `DlSrcName` =>        DlSrc       (MAC.parse(value).get.value)
      case `DlDstName` =>        DlDst       (MAC.parse(value).get.value)
      case `ProtocolName` =>     Protocol    (UByte(toByte(value)))
      case `TosBitsName` =>      TosBits     (UByte(toByte(value)))
      case `NwSrcName` =>        NwSrc       (InetAddress getByName value)
      case `NwDstName` =>        NwDst       (InetAddress getByName value)
      case `TpSrcName` =>        TpSrc       (UShort(toInt(value)))
      case `TpDstName` =>        TpDst       (UShort(toInt(value)))
    }
  }
}

import org.flowforwarding.warp.controller.modules.opendaylight_rest.sal.MatchField._

case class IngressPort (value: NodeConnector[_, _ <: Node[_]]) extends MatchField { def `type` = IngressPortName;  def stringValue = value.idStr }
case class VlanId      (value: UShort)                         extends MatchField { def `type` = VlanIdName;       def stringValue = value.toString }
case class VlanPriority(value: UByte)                          extends MatchField { def `type` = VlanPriorityName; def stringValue = value.toString }
case class EtherType   (value: UShort)                         extends MatchField { def `type` = EtherTypeName;    def stringValue = value.toString }
case class DlSrc       (value: Array[Byte])                    extends MatchField { def `type` = DlSrcName;        def stringValue = value.toString }
case class DlDst       (value: Array[Byte])                    extends MatchField { def `type` = DlDstName;        def stringValue = value.toString }
case class Protocol    (value: UByte)                          extends MatchField { def `type` = ProtocolName;     def stringValue = value.toString }
case class TosBits     (value: UByte)                          extends MatchField { def `type` = TosBitsName;      def stringValue = value.toString }
case class NwSrc       (value: InetAddress)                    extends MatchField { def `type` = NwSrcName;        def stringValue = value.toString.stripPrefix("/") }
case class NwDst       (value: InetAddress)                    extends MatchField { def `type` = NwDstName;        def stringValue = value.toString.stripPrefix("/") }
case class TpSrc       (value: UShort)                         extends MatchField { def `type` = TpSrcName;        def stringValue = value.toString }
case class TpDst       (value: UShort)                         extends MatchField { def `type` = TpDstName;        def stringValue = value.toString }