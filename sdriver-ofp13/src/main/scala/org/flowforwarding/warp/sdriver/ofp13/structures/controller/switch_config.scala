/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.sdriver.ofp13.structures

import com.gensler.scalavro.types.supply._
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_config_flags.CONFIG_FLAGS
import org.flowforwarding.warp.sdriver.dynamic.DynamicPath
import org.flowforwarding.warp.sdriver.ofp13.structures.ofp_length._

case class ofp_switch_config_request private[sdriver] (header: ofp_header)

object ofp_switch_config_request{

  private[sdriver] def build(@DynamicPath("header", "xid") xid: UInt32) =
    ofp_switch_config_request(ofp_header(OFPL_GET_CONFIG_REQUEST_LEN, xid))

  def apply(xid: Int): ofp_switch_config_request = build(UInt32.fromInt(xid))
}

/* Switch configuration. */
case class ofp_switch_config_reply(
  header: ofp_header,
  flags: CONFIG_FLAGS,  /* Bitmap of OFPC_* flags. */
  miss_send_len: UInt16)  /* Max bytes of packet that datapath should send to the controller. See ofp_controller_max_len for valid values. */


// same as previous, just another type
case class ofp_set_config(header: ofp_header,
                          flags: CONFIG_FLAGS,
                          miss_send_len: UInt16)

object ofp_config_flags extends WordBitmap {
  type CONFIG_FLAGS = Value
  /* Handling of IP fragments. */
  val OFPC_FRAG_NORMAL = ##(0)      /* No special handling for fragments. */
  val OFPC_FRAG_DROP   = ##(1 << 0) /* Drop fragments. */
  val OFPC_FRAG_REASM  = ##(1 << 1) /* Reassemble (only if OFPC_IP_REASM set). */
  val OFPC_FRAG_MASK   = ValuesSet(Set(OFPC_FRAG_DROP, OFPC_FRAG_REASM))
}