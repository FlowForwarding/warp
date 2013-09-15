/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.supply;

import org.flowforwarding.of.protocol.ofmessages.OFMessagePacketIn;
import org.flowforwarding.of.protocol.ofstructures.OFStructureMatch;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMGetPacketInMatch extends OFMGet <OFMessagePacketIn, OFStructureMatch>{
   
   public OFMGetPacketInMatch (OFMessagePacketIn packetIn) {
      receiver = packetIn;
   }
   
   /**
    *
    */
   public OFStructureMatch get(String name) {
      return receiver.getMatch(name);
   }
   
   /**
    * 
    */
   public boolean exist (String name) {
      return receiver.existMatch(name);
   }
   
}
