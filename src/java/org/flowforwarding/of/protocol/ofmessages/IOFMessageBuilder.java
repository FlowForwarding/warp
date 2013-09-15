/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofmessages;

import org.flowforwarding.of.protocol.ofmessages.OFMessageError.OFMessageErrorHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigHandler;

/**
 * @author Infoblox Inc.
 *
 */
public interface IOFMessageBuilder {
   
   public OFMessageHandler build (String msg);
   
   public OFMessageFlowModHandler buildFlowMod();
   
   public OFMessageSwitchConfigHandler buildSwitchConfig ();
   
   public OFMessagePacketInHandler buildPacketIn ();
   
   public OFMessageErrorHandler buildError ();
}
