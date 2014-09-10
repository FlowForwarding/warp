/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.supply;

/**
 * @author Infoblox Inc.
 *
 */
public abstract class OFMAdd <Receiver, Name, Value> implements IOFModify {

   protected Receiver receiver = null;
   
   public void add (Name name, Value value) {
   }

}
