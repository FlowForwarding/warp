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
public class OFStructureInstruction implements OFStructure{
   
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
