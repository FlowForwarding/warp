/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.opendaylight_rest.sal

import spire.math.{UByte, UInt, ULong}

import scala.util.Try

trait Property[T]{
  def value: T
  def name: String
}

object Property{
  val DescriptionName = "description"
  val TablesName= "tables"
  val TimeStampName = "timestamp"
  val MacAddressName = "mac"
  val CapabilitiesName = "capabilities"
  val BuffersName = "buffers"
  val SupportedFlowActionName = "flow_actions"
  val AdvertisedBandwidthName = "advertised"
  val SupportedBandwidthName = "supported"
  val PeerBandwidthName = "peer"
  val NameName = "name"
  val ConfigName = "config"
  val StateName = "state"
  val MaxSpeedName = "max_speed"

  def apply(name: String, value: String): Try[Property[_]] = Try {
    name match {
      case `DescriptionName` =>           Description          (value)
      case `TablesName` =>                Tables               (UByte(value.toShort))
      case `TimeStampName` =>             TimeStamp            (value.toLong)
      case `MacAddressName` =>            MAC.parse(value).get
      case `CapabilitiesName` =>          Capabilities         (ULong(value))
      case `BuffersName` =>               Buffers              (UInt(value.toLong))
      case `SupportedFlowActionName` =>   SupportedFlowActions (ULong(value))

      case `AdvertisedBandwidthName` =>   AdvertisedBandwidth  (ULong(value))
      case `SupportedBandwidthName` =>    SupportedBandwidth   (ULong(value))
      case `PeerBandwidthName` =>         PeerBandwidth        (ULong(value))
      case `NameName` =>                  Name                 (value)
      case `ConfigName` =>                Config               (ULong(value))
      case `StateName` =>                 State                (ULong(value))
      case `MaxSpeedName` =>              MaxSpeed             (UInt(value.toLong))
    }
  }
}

import Property._

case class Description         (value: String)      extends Property[String]      { def name = DescriptionName }
case class Tables              (value: UByte)       extends Property[UByte]       { def name = TablesName }
case class TimeStamp           (value: Long)        extends Property[Long]        { def name = TimeStampName }
case class Capabilities        (value: ULong)       extends Property[ULong]       { def name = CapabilitiesName }
case class Buffers             (value: UInt)        extends Property[UInt]        { def name = BuffersName }
case class SupportedFlowActions(value: ULong)       extends Property[ULong]       { def name = SupportedFlowActionName }
case class MAC                 (value: Array[Byte]) extends Property[Array[Byte]] {
  def name = MacAddressName

  override def toString =
    value map { b =>
      val s = (b.toLong & 0xff).toHexString
      if(s.length == 2) s else '0' + s
    } mkString ":"
}

object MAC{
  def parse(s: String) = Try {
    val data = s split ":" map { _.toByte }
    if(data.length != 6) throw new Exception("Wrong MAC format")
    MAC(data.toArray)
  }
}

case class AdvertisedBandwidth(value: ULong)  extends Property[ULong]             { def name = AdvertisedBandwidthName }
case class SupportedBandwidth (value: ULong)  extends Property[ULong]             { def name = SupportedBandwidthName } 
case class PeerBandwidth      (value: ULong)  extends Property[ULong]             { def name = PeerBandwidthName }
case class Name               (value: String) extends Property[String]            { def name = NameName }
case class Config             (value: ULong)  extends Property[ULong]             { def name = ConfigName }
case class State              (value: ULong)  extends Property[ULong]             { def name = StateName }
case class MaxSpeed           (value: UInt)   extends Property[UInt]              { def name = MaxSpeedName }


  
