/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._

import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_port_no.OFP_PORT_NO
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_port_config.OFP_PORT_CONFIG
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_port_state.OFP_PORT_STATE
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_port_features.OFP_PORT_FEATURES

object ofp_port_no extends DWordEnum with AllowUnspecifiedValues[Int]{ // undefined values accepted
  type OFP_PORT_NO = Value

  /* Maximum number of physical and logical switch ports. */
  val OFPP_MAX = ##(0xffffff00L)

  /* Reserved OpenFlow Port (fake output "ports"). */

  /* Send the packet out the input port. This
     reserved port must be explicitly used
     in order to send back out of the input port. */
  val OFPP_IN_PORT = ##(0xfffffff8L)

  /* Submit the packet to the first flow table
     NB: This destination port can only be used in packet-out messages. */
  val OFPP_TABLE = ##(0xfffffff9L)

  /* Process with normal L2/L3 switching. */
  val OFPP_NORMAL = ##(0xfffffffaL)

  /* All physical ports in VLAN, except input port and those blocked or link down. */
  val OFPP_FLOOD = ##(0xfffffffbL)

  /* All physical ports except input port. */
  val OFPP_ALL = ##(0xfffffffcL)

  /* Send to controller. */
  val OFPP_CONTROLLER = ##(0xfffffffdL)

  /* Local openflow "port". */
  val OFPP_LOCAL = ##(0xfffffffeL)

  /* Wildcard port used only for flow mod (delete) and flow stats requests.
     Selects all flows regardless of output port (including flows with no output port). */
  val OFPP_ANY = ##(0xffffffffL)
}

/* Description of a port */
case class ofp_port private [sdriver] (
  port_no: OFP_PORT_NO,    /* Predefined port or port number */
  pad: Pad4,      /* Align to 64 bits. */
  hw_addr: UInt48,         /* 8 * OFP_ETH_ALEN = 6 */
  pad2: Pad2,     /* Align to 64 bits. */
  name: RawSeq[UInt8],      /* 8 * OFP_MAX_PORT_NAME_LEN = 16 Null-terminated */

  config: OFP_PORT_CONFIG, /* Bitmap of OFPPC_* flags. */
  state:  OFP_PORT_STATE,  /* Bitmap of OFPPS_* flags. */

  /* Bitmaps of OFPPF_* that describe features. All bits zeroed if
   * unsupported or unavailable. */
  curr:       OFP_PORT_FEATURES, /* Current features. */
  advertised: OFP_PORT_FEATURES, /* Features being advertised by the port. */
  supported:  OFP_PORT_FEATURES, /* Features supported by the port. */
  peer:       OFP_PORT_FEATURES, /* Features advertised by peer. */

  curr_speed: UInt32, /* Current port bitrate in kbps. */
  max_speed:  UInt32  /* Max port bitrate in kbps */)

object ofp_port extends RawSeqFieldsInfo{
  val rawFieldsLengthCalculator: LengthCalculator = { case 4 => Some { _ => 16 } }
}

/* Flags to indicate behavior of the physical port. These flags are
 * used in ofp_port to describe the current configuration. They are
 * used in the ofp_port_mod message to configure the port's behavior.
 */
object ofp_port_config extends DWordBitmap {
  type OFP_PORT_CONFIG = Value
  val OFPPC_PORT_DOWN    = ##(1 << 0) /* Port is administratively down. */
  val OFPPC_NO_RECV      = ##(1 << 2) /* Drop all packets received by port. */
  val OFPPC_NO_FWD       = ##(1 << 5) /* Drop packets forwarded to port. */
  val OFPPC_NO_PACKET_IN = ##(1 << 6) /* Do not send packet-in msgs for port. */
}

/* Current state of the physical port. These are not configurable from
 * the controller.
 */
object ofp_port_state extends DWordBitmap{
  type OFP_PORT_STATE = Value
  val OFPPS_LINK_DOWN = ##(1 << 0) /* No physical link present. */
  val OFPPS_BLOCKED   = ##(1 << 1) /* Port is blocked */
  val OFPPS_LIVE      = ##(1 << 2) /* Live for Fast Failover Group. */
}

/* Features of ports available in a datapath. */
object ofp_port_features extends DWordBitmap {
  type OFP_PORT_FEATURES = Value

  val OFPPF_10MB_HD    = ##(1 << 0)  /* 10 Mb half-duplex rate support. */
  val OFPPF_10MB_FD    = ##(1 << 1)  /* 10 Mb full-duplex rate support. */
  val OFPPF_100MB_HD   = ##(1 << 2)  /* 100 Mb half-duplex rate support. */
  val OFPPF_100MB_FD   = ##(1 << 3)  /* 100 Mb full-duplex rate support. */
  val OFPPF_1GB_HD     = ##(1 << 4)  /* 1 Gb half-duplex rate support. */
  val OFPPF_1GB_FD     = ##(1 << 5)  /* 1 Gb full-duplex rate support. */
  val OFPPF_10GB_FD    = ##(1 << 6)  /* 10 Gb full-duplex rate support. */
  val OFPPF_40GB_FD    = ##(1 << 7)  /* 40 Gb full-duplex rate support. */
  val OFPPF_100GB_FD   = ##(1 << 8)  /* 100 Gb full-duplex rate support. */
  val OFPPF_1TB_FD     = ##(1 << 9)  /* 1 Tb full-duplex rate support. */
  val OFPPF_OTHER      = ##(1 << 10) /* Other rate, not in the list. */
  val OFPPF_COPPER     = ##(1 << 11) /* Copper medium. */
  val OFPPF_FIBER      = ##(1 << 12) /* Fiber medium. */
  val OFPPF_AUTONEG    = ##(1 << 13) /* Auto-negotiation. */
  val OFPPF_PAUSE      = ##(1 << 14) /* Pause. */
  val OFPPF_PAUSE_ASYM = ##(1 << 15) /* Asymmetric pause. */
}