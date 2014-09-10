/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.supply;

import org.flowforwarding.warp.protocol.ofmessages.OFMessageFlowMod;
import org.flowforwarding.warp.protocol.ofstructures.MatchSet;

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