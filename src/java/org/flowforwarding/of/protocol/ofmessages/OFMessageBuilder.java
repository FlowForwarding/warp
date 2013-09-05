/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofmessages;

/**
 * @author Infoblox Inc.
 *
 */
public interface OFMessageBuilder {
   
   public IOFMessageRef build (String msg);
   
   public IOFMessageRef buildFlowMod();
}
