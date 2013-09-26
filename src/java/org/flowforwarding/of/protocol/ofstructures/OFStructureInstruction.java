/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofstructures;

import org.flowforwarding.of.protocol.ofmessages.ActionSet;
import org.flowforwarding.of.protocol.supply.OFMAddAction;
import org.flowforwarding.of.protocol.supply.OFSGetActions;

/**
 * @author Infoblox Inc.
 *
 */
public class OFStructureInstruction implements IOFStructure{
   
   protected enum InstructionTypes {
      GOTO_TABLE,
      WRITE_METADATA,
      WRITE_ACTIONS,
      APPLY_ACTIONS,
      CLEAR_ACTIONS,
      METER,
   }
   
   protected ActionSet actions;
   protected InstructionTypes type;

   public ActionSet getActions() {
      return actions;
   }

   public void setActions(ActionSet actions) {
      this.actions = actions;
   }
   
   public void addAction(String name, String value) {
      this.actions.add(name, value);
   }
   
   protected OFStructureInstruction(InstructionTypes instrType) {
      actions = new ActionSet();
      type = instrType;
   }
   
   public static class OFStructureInstructionRef extends OFStructureRef<OFStructureInstruction>{
      
      protected OFMAddAction addAction = null;
      protected OFSGetActions getActions = null;
      
      protected OFStructureInstructionRef(InstructionTypes instrType) {
         structure = new OFStructureInstruction(instrType);
         
         addAction = new OFMAddAction(structure);
         getActions = new OFSGetActions(structure);
      }
      
      protected OFStructureInstructionRef(OFStructureInstruction instruction) {
         structure = instruction;
      }
      
      public static OFStructureInstructionRef create(String type) {
         switch (type) {
         case "goto_table":
            return new OFStructureInstructionRef(InstructionTypes.GOTO_TABLE);
         case "write_metadata":
            return new OFStructureInstructionRef(InstructionTypes.WRITE_METADATA);
         case "write_actions":
            return new OFStructureInstructionRef(InstructionTypes.WRITE_ACTIONS);
         case "apply_actions":
            return new OFStructureInstructionRef(InstructionTypes.APPLY_ACTIONS);
         case "clear_actions":
            return new OFStructureInstructionRef(InstructionTypes.CLEAR_ACTIONS);
         case "meter":
            return new OFStructureInstructionRef(InstructionTypes.METER);            
            
         default: 
            return null;
            
         }
      }
      
      public void addActionOutput (String value) {
         addAction.add("output", value);
      }
      
      public ActionSet getActions() {
         return getActions.get();
      }

   }
}
