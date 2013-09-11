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
public class OFStructureBuilder13 implements IOFStructureBuilder {

   /**
    * @param
    */
   @Override
   public OFStructureInstructionHandler buildInstruction (String type) {
      return OFStructureInstructionHandler.create(type);
   }
   
   @Override
   public OFStructureInstructionHandler buildInstructionGotoTable () {
      return OFStructureInstructionHandler.create("goto_table");
   }
   
   @Override
   public OFStructureInstructionHandler buildInstructionWriteMetadata () {
      return OFStructureInstructionHandler.create("write_metadata");
   }
   
   @Override
   public OFStructureInstructionHandler buildInstructionWriteActions () {
      return OFStructureInstructionHandler.create("write_actions");
   }
   
   @Override
   public OFStructureInstructionHandler buildInstructionApplyActions () {
      return OFStructureInstructionHandler.create("apply_actions");
   }
   
   @Override
   public OFStructureInstructionHandler buildInstructionClearActions () {
      return OFStructureInstructionHandler.create("clear_actions");
   }
   
   @Override
   public OFStructureInstructionHandler buildInstructionMeter () {
      return OFStructureInstructionHandler.create("meter");
   }
}
