/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.supply;

import org.flowforwarding.of.protocol.ofmessages.ActionSet;
import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction;

/**
 * @author Infoblox Inc.
 *
 */
public class OFSGetActions extends OFMGet <OFStructureInstruction, ActionSet>{
   
   public OFSGetActions (OFStructureInstruction instructions) {
      receiver = instructions;
   }
   
   /**
    * @param args
    */
   @Override
   public ActionSet get () {
      return receiver.getActions();
   }

}
