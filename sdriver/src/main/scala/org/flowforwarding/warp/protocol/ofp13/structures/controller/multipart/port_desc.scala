/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.protocol.ofp13.structures.ofp_multipart_type._

case class ofp_multipart_port_desc_request private [protocol] () extends EmptyMultipartRequest {
  override def tp: OFP_MULTIPART_TYPE = ofp_multipart_type.OFPMP_PORT_DESC
}

object ofp_multipart_port_desc_request {
  private [protocol] def build() = ofp_multipart_port_desc_request()
}

case class ofp_multipart_port_desc_reply private [protocol] (desc: Seq[ofp_port])