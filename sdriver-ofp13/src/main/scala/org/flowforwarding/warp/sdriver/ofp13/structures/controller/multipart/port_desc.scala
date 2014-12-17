/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_multipart_type._

case class ofp_multipart_port_desc_request private [sdriver] () extends EmptyMultipartRequest {
  override def tp: OFP_MULTIPART_TYPE = ofp_multipart_type.OFPMP_PORT_DESC
}

object ofp_multipart_port_desc_request {
  private [sdriver] def build() = ofp_multipart_port_desc_request()
}

case class ofp_multipart_port_desc_reply private [sdriver] (desc: Seq[ofp_port])