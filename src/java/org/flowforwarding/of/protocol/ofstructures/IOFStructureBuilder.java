/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofstructures;

import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction.OFStructureInstructionHandler;

/**
 * @author Infoblox Inc.
 *
 */
public interface IOFStructureBuilder {
   
   OFStructureInstructionHandler buildInstruction (String type);
   
   OFStructureInstructionHandler buildInstructionGotoTable ();
   
   OFStructureInstructionHandler buildInstructionWriteMetadata ();
   
   OFStructureInstructionHandler buildInstructionWriteActions ();
   
   OFStructureInstructionHandler buildInstructionApplyActions ();
   
   OFStructureInstructionHandler buildInstructionClearActions ();
   
   OFStructureInstructionHandler buildInstructionMeter ();

}
