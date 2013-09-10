/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofmessages;

import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModRef;

/**
 * @author Infoblox Inc.
 *
 */
public interface OFMessageBuilder {
   
   public IOFMessageRef build (String msg);
   
   public OFMessageFlowModRef buildFlowMod();
}
