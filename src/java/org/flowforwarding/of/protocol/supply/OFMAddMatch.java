/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.supply;

import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMAddMatch extends OFMAdd<OFMessageFlowMod, String, String>{
   
   public OFMAddMatch (OFMessageFlowMod flowMod) {
      receiver = flowMod;
   }

   
   @Override
   public void add (String name, String match) {
      receiver.addMatch(name, match);
   }

}
