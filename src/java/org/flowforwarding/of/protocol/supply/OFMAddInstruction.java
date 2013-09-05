/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.supply;

import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod;
import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMAddInstruction extends OFMAdd<OFMessageFlowMod, String, OFStructureInstruction>{
   
   
   public OFMAddInstruction (OFMessageFlowMod flowMod) {
      receiver = flowMod;
   }
   
   @Override
   public void add (String name, OFStructureInstruction instruction) {
      receiver.addInstruction(name, instruction);
   }
}
