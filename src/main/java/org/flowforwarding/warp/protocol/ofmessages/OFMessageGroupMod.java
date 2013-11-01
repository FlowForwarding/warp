/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.ofmessages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.flowforwarding.warp.protocol.ofstructures.OFStructureInstruction.OFStructureInstructionRef;
import org.flowforwarding.warp.protocol.ofstructures.Tuple;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessageGroupMod extends OFMessage{

   protected List<Tuple<String, String>> parms = new ArrayList<>();
   protected Iterator<Tuple<String, String>> iter = parms.iterator();
   
   protected OFMessageGroupMod() {
      // TODO Improvs: create() instead of Constructor?
   }
   
   public List<Tuple<String, String>> getParms() {
      return parms;
   }

   public void setParms(List<Tuple<String, String>> parms) {
      this.parms = parms;
   }

   public Iterator<Tuple<String, String>> getIterator () {
      return iter;
   }
   
   public void add (String name, String value) {
      parms.add(new Tuple<String, String>(name, value));
   }

   public static class OFMessageGroupModRef extends OFMessageRef <OFMessageGroupMod> {
      
      protected OFMessageGroupMod groupMod = null;
      
     
      protected OFMessageGroupModRef () {
         groupMod = new OFMessageGroupMod();
      }
      
      protected OFMessageGroupModRef (OFMessageGroupMod fm) {
         groupMod = fm;
      }
      
      public static OFMessageGroupModRef create() {
         
         return new OFMessageGroupModRef();
      }
   }
}
