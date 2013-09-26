/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.supply;

import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod;
import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction;
import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction.OFStructureInstructionRef;

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
