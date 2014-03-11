/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.jcontroller.supply;

/**
 * @author Infoblox Inc.
 *
 */
public abstract class OFCTell <Sender, Receiver, Message> {
   
   protected Sender sender = null;
   
   public void tell (Receiver receiver, Message msg) {
   }

}
