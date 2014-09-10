/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.offields.match;

import org.flowforwarding.warp.protocol.ofstructures.IOFStructure;
import org.flowforwarding.warp.protocol.ofstructures.OFStructureRef;

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
    * @return Match name
    */
   public Name getName() {
      // TODO Auto-generated method stub
      return name;
   }

   /**
    * @return Match value
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
