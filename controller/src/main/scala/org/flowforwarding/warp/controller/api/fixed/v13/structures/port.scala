/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.structures

import spire.math._

import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.dynamic.DynamicStructure
import org.flowforwarding.warp.controller.api.fixed.util._
import org.flowforwarding.warp.controller.api.fixed.util.MacAddress

case class PortNumber(number: UInt)

/** Reserved OpenFlow Port (fake output "ports"). */
object PortNumber{
  /** Send the packet out the input port.
    * This reserved port must be explicitly used in order to send back out of the input port. */
  val InPort = PortNumber(UInt(0xfffffff8L))
  
  /** Submit the packet to the first flow table.
    * NB: This destination port can only be used in packet-out messages. */
  val TablePort = PortNumber(UInt(0xfffffff9L))
  
  /** Process with normal L2/L3 switching. */
  val NormalPort = PortNumber(UInt(0xfffffffaL))
  
  /** All physical ports in VLAN, except input port and those blocked or link down. */
  val FloodPort = PortNumber(UInt(0xfffffffbL))
  
  /** All physical ports except input port. */
  val AllPorts = PortNumber(UInt(0xfffffffcL))
  
  /** Send to controller. */
  val ControllerPort = PortNumber(UInt(0xfffffffdL))
  
  /** Local openflow "port". */
  val LocalPort = PortNumber(UInt(0xfffffffeL))
  
  /** Wildcard port used only for flow mod (delete) and flow stats requests.
    * Selects all flows regardless of output port (including flows with no output port). */
  val AnyPort = PortNumber(UInt(0xffffffffL))
}

case class PortFeatures(
   _10MB_HD:  Boolean = false,     /* 10 Mb half-duplex rate support. */
   _10MB_FD:  Boolean = false,     /* 10 Mb full-duplex rate support. */
   _100MB_HD: Boolean = false,     /* 100 Mb half-duplex rate support. */
   _100MB_FD: Boolean = false,     /* 100 Mb full-duplex rate support. */
   _1GB_HD:   Boolean = false,     /* 1 Gb half-duplex rate support. */
   _1GB_FD:   Boolean = false,     /* 1 Gb full-duplex rate support. */
   _10GB_FD:  Boolean = false,     /* 10 Gb full-duplex rate support. */
   _40GB_FD:  Boolean = false,     /* 40 Gb full-duplex rate support. */
   _100GB_FD: Boolean = false,     /* 100 Gb full-duplex rate support. */
   _1TB_FD:   Boolean = false,     /* 1 Tb full-duplex rate support. */
   other:     Boolean = false,     /* Other rate, not in the list. */
   copper:    Boolean = false,     /* Copper medium. */
   fiber:     Boolean = false,     /* Fiber medium. */
   autoneg:   Boolean = false,     /* Auto-negotiation. */
   pause:     Boolean = false,     /* Pause. */
   pauseAsym: Boolean = false)     /* Asymmetric pause. */  extends Bitmap

case class PortConfig(portDown:    Boolean = false,
                      noReceived:  Boolean = false,
                      noForwarded: Boolean = false,
                      noPacketIn:  Boolean = false) extends Bitmap{
  override def bits = Array(0, 2, 5, 6)
}

case class PortState(linkDown: Boolean = false, /* No physical link present. */
                     blocked:  Boolean = false, /* Port is blocked */
                     live:     Boolean = false) /* Live for Fast Failover Group. */ extends Bitmap {
  override def bits = Array(0, 1, 2)
}

trait Port {
  val number:     PortNumber
  val hwAddress:  MacAddress
  val name:       String
  val config:     PortConfig
  val state:      PortState
  /* Bitmaps of OFPPF_* that describe features. All bits zeroed if unsupported or unavailable. */
  val curr:       PortFeatures  /* Current features. */
  val advertised: PortFeatures  /* Features being advertised by the port. */
  val supported:  PortFeatures  /* Features supported by the port. */
  val peer:       PortFeatures  /* Features advertised by peer. */
  val currSpeed:  UInt          /* Current port bitrate in kbps. */
  val maxSpeed:   UInt          /* Max port bitrate in kbps */
}

private[fixed] trait Ofp13PortDescription extends StructureDescription{
  apiProvider: StructuresDescriptionHelper =>

  protected[fixed] implicit object Port extends FromDynamic[Port]{

    val fromDynamic: PartialFunction[DynamicStructure, Port] = {
      case s if s.ofType[Port] => new OfpStructure[Port](s) with Port{
        val number:     PortNumber   = PortNumber(primitiveField[UInt]("port_no"))
        val hwAddress:  MacAddress   = MacAddress(primitiveField[ULong]("hw_addr"))
        val name:       String       = stringField("name")
        val config:     PortConfig   = bitmapField[PortConfig]("config", Array(UInt(0), UInt(2), UInt(5), UInt(6)))
        val state:      PortState    = bitmapField[PortState]("state")
        val curr:       PortFeatures = bitmapField[PortFeatures]("curr")
        val advertised: PortFeatures = bitmapField[PortFeatures]("advertised")
        val supported:  PortFeatures = bitmapField[PortFeatures]("supported")
        val peer:       PortFeatures = bitmapField[PortFeatures]("peer")
        val currSpeed:  UInt         = primitiveField[UInt]("curr_speed")
        val maxSpeed:   UInt         = primitiveField[UInt]("max_speed")
      }
    }
  }
}
