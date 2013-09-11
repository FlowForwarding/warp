/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofstructures;

import org.flowforwarding.of.protocol.ofmessages.ActionSet;

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
      OFPIT_METER,
   }
   
   protected ActionSet actions;

   public ActionSet getActions() {
      return actions;
   }

   public void setActions(ActionSet actions) {
      this.actions = actions;
   }
   
   public void addAction(String name, String value) {
      this.actions.add(name, value);
   }
   
}
