/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.ofmessages;

import org.flowforwarding.warp.protocol.ofmessages.OFMessageError.OFMessageErrorRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigRef;

/**
 * @author Infoblox Inc.
 *
 */
public interface IOFMessageBuilder {
   
   public OFMessageRef build (String msg);
   
   public OFMessageFlowModRef buildFlowMod();
   
   public OFMessageSwitchConfigRef buildSwitchConfig ();
   
   public OFMessagePacketInRef buildPacketIn ();
   
   public OFMessageErrorRef buildError ();
}
