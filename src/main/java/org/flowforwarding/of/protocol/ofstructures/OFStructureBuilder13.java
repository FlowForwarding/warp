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
public class OFStructureBuilder13 implements IOFStructureBuilder {

   /**
    * @param
    */
   @Override
   public OFStructureInstructionRef buildInstruction (String type) {
      return OFStructureInstructionRef.create(type);
   }
   
   @Override
   public OFStructureInstructionRef buildInstructionGotoTable () {
      return OFStructureInstructionRef.create("goto_table");
   }
   
   @Override
   public OFStructureInstructionRef buildInstructionWriteMetadata () {
      return OFStructureInstructionRef.create("write_metadata");
   }
   
   @Override
   public OFStructureInstructionRef buildInstructionWriteActions () {
      return OFStructureInstructionRef.create("write_actions");
   }
   
   @Override
   public OFStructureInstructionRef buildInstructionApplyActions () {
      return OFStructureInstructionRef.create("apply_actions");
   }
   
   @Override
   public OFStructureInstructionRef buildInstructionClearActions () {
      return OFStructureInstructionRef.create("clear_actions");
   }
   
   @Override
   public OFStructureInstructionRef buildInstructionMeter () {
      return OFStructureInstructionRef.create("meter");
   }
}
