/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.offields.match;

import org.flowforwarding.of.protocol.ofstructures.IOFStructure;
import org.flowforwarding.of.protocol.ofstructures.OFStructureRef;

/**
 * @author Infoblox Inc.
 *
 */
public abstract class OFStructureMatch <Name, Value> implements IOFStructure {

   
   protected OFStructureMatch() {
      super();
   }

   protected Name name;
   protected Value value;
   
   
   /**
    * @return
    */
   public Name getName() {
      // TODO Auto-generated method stub
      return name;
   }

   /**
    * @return
    */
   public Value getMatch() {
      // TODO Auto-generated method stub
      return value;
   }
   
   public class OFStructureMatchRef extends OFStructureRef<OFStructureMatch<Name, Value>> {
      
      protected OFStructureMatchRef () {
      }
      
   }
}
