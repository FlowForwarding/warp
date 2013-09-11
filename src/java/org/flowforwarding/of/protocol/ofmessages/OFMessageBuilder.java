/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofmessages;

import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModHandler;

/**
 * @author Infoblox Inc.
 *
 */
public interface OFMessageBuilder {
   
   public IOFMessageHandler build (String msg);
   
   public OFMessageFlowModHandler buildFlowMod();
}
