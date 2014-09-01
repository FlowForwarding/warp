/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.supply;

import org.flowforwarding.warp.protocol.ofmessages.InstructionSet;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageFlowMod;
import org.flowforwarding.warp.protocol.ofstructures.OFStructureInstruction;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMGetInstructions extends OFMGet<OFMessageFlowMod, InstructionSet>{

   public OFMGetInstructions (OFMessageFlowMod flowMod) {
      receiver = flowMod;
   }
   
   /**
    * @param args
    */
   @Override
   public InstructionSet get () {
      return receiver.getInstructions();
   }


}
