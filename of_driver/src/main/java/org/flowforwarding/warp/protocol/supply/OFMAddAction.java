/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.supply;

import org.flowforwarding.warp.protocol.ofstructures.OFStructureInstruction;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMAddAction extends OFMAdd<OFStructureInstruction, String, String>{
   
   public OFMAddAction (OFStructureInstruction instruction) {
      receiver = instruction;
   }
   
   @Override
   public void add (String name, String value) {
      receiver.addAction(name, value);
   }

}
