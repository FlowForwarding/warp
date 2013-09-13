/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofmessages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowforwarding.of.protocol.ofstructures.OFStructureMatch;
//import org.flowforwarding.of.protocol.supply.OFMGetPacketInMatch;
/**
 * @author Infoblox Inc.
 *
 */
public class OFMessagePacketIn extends OFMessage {
   
   protected Map<String, OFStructureMatch> matches = null;
   
   protected OFMessagePacketIn (byte [] packetIn, IOFMessageProvider provider) {
      matches = new HashMap<>();
   }
   
   public boolean existMatch (String name) {
      return matches.containsKey(name);
   }
   
   public OFStructureMatch getMatch(String name) {
      return matches.get(name);
   }

   
   public static class OFMessagePacketInHandler extends OFMessageHandler <OFMessagePacketIn> {
      
      //protected OFMGetPacketInMatch getMatch; 

      protected OFMessagePacketInHandler (byte [] packetIn, IOFMessageProvider provider) {
         message = new OFMessagePacketIn(packetIn, provider);
        // getMatch = new OFMGetPacketInMatch(message);
      }
      
      /**
       * @return
       */
      public static OFMessagePacketInHandler create(byte [] packetIn, IOFMessageProvider provider) {
         // TODO Auto-generated method stub
         return new OFMessagePacketInHandler(packetIn, provider);
      }
      
      public OFStructureMatch getMatch(String name) {
         return null;// getMatch.get(name);
      }
      
      public boolean existMatch (String name) {
         return false; // getMatch.exist(name);
      }
   }

}
