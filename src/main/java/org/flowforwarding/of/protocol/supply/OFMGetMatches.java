/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.supply;

import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod;
import org.flowforwarding.of.protocol.ofstructures.MatchSet;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMGetMatches extends OFMGet<OFMessageFlowMod, MatchSet>{

   public OFMGetMatches (OFMessageFlowMod flowMod) {
      receiver = flowMod;
   }
   
   /**
    * @param args
    */
   @Override
   public MatchSet get () {
      return receiver.getMatches();
   }
}