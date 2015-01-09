/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_port_reason.OFP_PORT_REASON

/* A physical port has changed in the datapath */
case class ofp_port_status private[sdriver] (
  header: ofp_header,
  reason: OFP_PORT_REASON, /* One of OFPPR_*. */
  pad: Pad7,               /* Align to 64-bits. */
  desc: ofp_port)

/* What changed about the physical port */
object ofp_port_reason extends ByteEnum {
  type OFP_PORT_REASON = Value

  val OFPPR_ADD    = ##(0) /* The port was added. */
  val OFPPR_DELETE = ##(1) /* The port was removed. */
  val OFPPR_MODIFY = ##(2) /* Some attribute of the port has changed. */
}