/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.supply;

import org.flowforwarding.of.protocol.ofmessages.InstructionSet;
import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod;
import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction;

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
