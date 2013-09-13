/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofstructures;

/**
 * @author Infoblox Inc.
 *
 */
public abstract class OFStructureMatch <Name, Value> implements IOFStructure {

   /**
    * @return
    */
   public Name getName() {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @return
    */
   public Value getMatch() {
      // TODO Auto-generated method stub
      return null;
   }
   
   public class OFStructureMatchHandler extends OFStructureHandler<OFStructureMatch<Name, Value>> {
      
   }
}
