/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofmessages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowforwarding.of.protocol.offields.match.OFStructureMatch;
import org.flowforwarding.of.protocol.supply.OFMAdd;
import org.flowforwarding.of.protocol.supply.OFMGet;
//import org.flowforwarding.of.protocol.supply.OFMGetPacketInMatch;
/**
 * @author Infoblox Inc.
 *
 */
public class OFMessagePacketIn extends OFMessage {
   
   protected Map<String, OFStructureMatch> matches = null;
   
   protected OFMessagePacketIn () {
      matches = new HashMap<>();
   }
   
   public boolean existMatch (String name) {
      return matches.containsKey(name);
   }
   
   public OFStructureMatch getMatch(String name) {
      return matches.get(name);
   }
   
   public void addMatch (String name, OFStructureMatch value) {
      matches.put(name, value);
   }

   
   public static class OFMessagePacketInHandler extends OFMessageHandler <OFMessagePacketIn> {
      
      protected OFMGetMatch getMatch;
      protected OFMAddMatch addMatch;

      protected OFMessagePacketInHandler () {
         message = new OFMessagePacketIn();
         getMatch = new OFMGetMatch(message);
         addMatch = new OFMAddMatch(message);
      }
      
      /**
       * @return
       */
      public static OFMessagePacketInHandler create() {
         return new OFMessagePacketInHandler();
      }
      
      public OFStructureMatch getMatch(String name) {
         return getMatch.get(name);
      }
      
      // TODO Improvs: Instead of String parameter - implement named methods for every Match 
      public boolean existMatch (String name) {
         return getMatch.exist(name);
      }
      
      public OFStructureMatch<String, String> getMatchInPort () {
         return getMatch.get("ingress_port");
      }
      
      public OFStructureMatch<String, String> getMatchEthDst () {
         return getMatch.get("eth_dst");
      }
      
      public OFStructureMatch<String, String> getMatchEthSrc () {
         return getMatch.get("eth_src");
      }
      
      public boolean existMatchInPort () {
         return getMatch.exist("ingress_port");
      }
      
      public boolean existMatchEthDst () {
         return getMatch.exist("eth_dst");
      }
      
      public boolean existMatchEthSrc () {
         return getMatch.exist("eth_src");
      }
      
      public void addMatch (String name, String value) {
         
      }

      /*
       * Internal commands classes
       */
      public class OFMAddMatch extends OFMAdd<OFMessagePacketIn, String, OFStructureMatch>{
         public OFMAddMatch (OFMessagePacketIn pIn) {
            receiver = pIn;
         }
         @Override
         public void add (String name, OFStructureMatch match) {
            receiver.addMatch(name, match);
         }
      }
      
      public class OFMGetMatch extends OFMGet <OFMessagePacketIn, OFStructureMatch>{
         
         public OFMGetMatch (OFMessagePacketIn packetIn) {
            receiver = packetIn;
         }
         public OFStructureMatch get(String name) {
            return receiver.getMatch(name);
         }
         public boolean exist (String name) {
            return receiver.existMatch(name);
         }
         
      }

   }

}
