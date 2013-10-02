/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.supply;

import org.flowforwarding.of.protocol.ofmessages.OFMessage;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMAddField extends OFMAdd<OFMessage, String, String>{
   
   public OFMAddField (OFMessage msg) {
      receiver = msg;
   }
   
   @Override
   public void add (String name, String value) {
      receiver.add(name, value);
   }

}
