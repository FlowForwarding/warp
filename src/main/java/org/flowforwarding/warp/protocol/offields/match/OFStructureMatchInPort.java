/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.offields.match;

import org.flowforwarding.warp.util.Tuple;

/**
 * @author Infoblox Inc.
 *
 */
public class OFStructureMatchInPort extends OFStructureMatch<String, Integer>{
   
   protected Tuple<String, Integer> match;
   
   public OFStructureMatchInPort(Integer value) {
      match = new Tuple<String, Integer>("ingress_port", value);
   }
   
   @Override
   public Integer getMatch() {
      return match.getValue();
   }
   
   @Override
   public String getName() {
      return match.getName();
   }
}
