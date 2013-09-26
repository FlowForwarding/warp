/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofmessages;

import org.flowforwarding.of.protocol.ofmessages.OFMessageError.OFMessageErrorRef;
import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModRef;
import org.flowforwarding.of.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInRef;
import org.flowforwarding.of.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigRef;

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
