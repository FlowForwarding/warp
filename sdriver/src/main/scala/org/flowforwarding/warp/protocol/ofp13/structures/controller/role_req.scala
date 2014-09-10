/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._

import org.flowforwarding.warp.protocol.ofp13.structures.ofp_controller_role.OFP_CONTROLLER_ROLE
import org.flowforwarding.warp.protocol.dynamic.DynamicPath
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_length._
import spire.math.ULong

/* Role request and reply message. */

case class ofp_role_request private[protocol] (
  header: ofp_header,
  role: OFP_CONTROLLER_ROLE, /* One of OFPCR_ROLE_*. */
  pad: Pad4,                 /* Align to 64 bits. */
  generation_id: UInt64)     /* Master Election Generation Id */

object ofp_role_request{

  private[protocol] def build(@DynamicPath("header", "xid") xid: UInt32, role: OFP_CONTROLLER_ROLE, generation_id: UInt64) =
    ofp_role_request(ofp_header(OFPL_ROLE_REQUEST_LEN, xid), role, Pad4(), generation_id)

  def apply(xid: Int, role: OFP_CONTROLLER_ROLE, generation_id: ULong): ofp_role_request = build(UInt32.fromInt(xid), role, UInt64.fromLong(generation_id.toLong))
}

case class ofp_role_reply private[protocol] (header: ofp_header,
                          role: OFP_CONTROLLER_ROLE, /* One of OFPCR_ROLE_*. */
                          pad: Pad4,                 /* Align to 64 bits. */
                          generation_id: UInt64)     /* Master Election Generation Id */

/* Controller roles. */
object ofp_controller_role extends DWordEnum{
  type OFP_CONTROLLER_ROLE = Value

  val OFPCR_ROLE_NOCHANGE = ##(0) /* Don't change current role. */
  val OFPCR_ROLE_EQUAL    = ##(1) /* Default role, full access. */
  val OFPCR_ROLE_MASTER   = ##(2) /* Full access, at most one master. */
  val OFPCR_ROLE_SLAVE    = ##(3) /* Read-only access. */
}