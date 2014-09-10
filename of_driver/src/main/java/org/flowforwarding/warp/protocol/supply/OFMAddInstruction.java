/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.supply;

import org.flowforwarding.warp.protocol.ofmessages.OFMessageFlowMod;
import org.flowforwarding.warp.protocol.ofstructures.OFStructureInstruction;
import org.flowforwarding.warp.protocol.ofstructures.OFStructureInstruction.OFStructureInstructionRef;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMAddInstruction extends OFMAdd<OFMessageFlowMod, String, OFStructureInstructionRef>{
   
   
   public OFMAddInstruction (OFMessageFlowMod flowMod) {
      receiver = flowMod;
   }
   
   @Override
   public void add (String name, OFStructureInstructionRef instruction) {
      receiver.addInstruction(name, instruction);
   }
}
