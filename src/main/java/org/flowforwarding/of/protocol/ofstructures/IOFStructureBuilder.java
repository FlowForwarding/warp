/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofstructures;

import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction.OFStructureInstructionRef;

/**
 * @author Infoblox Inc.
 *
 */
public interface IOFStructureBuilder {
   
   OFStructureInstructionRef buildInstruction (String type);
   
   OFStructureInstructionRef buildInstructionGotoTable ();
   
   OFStructureInstructionRef buildInstructionWriteMetadata ();
   
   OFStructureInstructionRef buildInstructionWriteActions ();
   
   OFStructureInstructionRef buildInstructionApplyActions ();
   
   OFStructureInstructionRef buildInstructionClearActions ();
   
   OFStructureInstructionRef buildInstructionMeter ();

}
