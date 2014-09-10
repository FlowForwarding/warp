/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.ofmessages;

import java.util.HashMap;
import java.util.Map;

import org.flowforwarding.warp.protocol.supply.OFMAddConfigFlag;
import org.flowforwarding.warp.protocol.supply.OFMGet;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessageSwitchConfig  extends OFMessage{
  
   // TODO Improvs: think how to hide it!
   public enum ConfigFlag{
      FRAG_NORMAL,
      FRAG_DROP,
      FRAG_REASM,
      FRAG_MASK
   }
   
   protected Map<ConfigFlag, Boolean> configFlags;

   public void setFlag (ConfigFlag flag) {
      configFlags.put(flag, true);
   }
   
   public void setFlag (ConfigFlag flag, Boolean value) {
      configFlags.put(flag, value.booleanValue());
   }
   
   // TODO Improvs: does Boolean configFlags.get(flag) means something? Or containsKey(flag) is enough?
   public Boolean getFlag (ConfigFlag flag) {
      return configFlags.containsKey(flag);
   }
   
   
   protected OFMessageSwitchConfig() {
      configFlags = new HashMap<>();
   }
   
   public static class OFMessageSwitchConfigRef extends OFMessageRef <OFMessageSwitchConfig> {
      
      protected OFMAddConfigFlag addFlag;
      protected OFMGetConfigFlag getFlag;
      
      protected OFMessageSwitchConfigRef() {
         message = new OFMessageSwitchConfig();
         addFlag = new OFMAddConfigFlag(message);
         getFlag = new OFMGetConfigFlag(message);
      }
      
      public static OFMessageSwitchConfigRef create () {
         return new OFMessageSwitchConfigRef();
      }
      
      public void setConfigFlagFragNormal () {
         addFlag.add(ConfigFlag.FRAG_NORMAL, true);
      }
      
      public void setConfigFlagFragDrop () {
         addFlag.add(ConfigFlag.FRAG_DROP, true);
      }
      
      public void setConfigFlagFragReasm () {
         addFlag.add(ConfigFlag.FRAG_REASM, true);
      }
      
      public void setConfigFlagFragMask () {
         addFlag.add(ConfigFlag.FRAG_MASK, true);
      }
      
      public Boolean isFragNormal() {
         return getFlag.get(ConfigFlag.FRAG_NORMAL);
      }
      
      public Boolean isFragDrop() {
         return getFlag.get(ConfigFlag.FRAG_DROP);
      }

      public Boolean isFragReasm() {
         return getFlag.get(ConfigFlag.FRAG_REASM);
      }

      public Boolean isFragMask() {
         return getFlag.get(ConfigFlag.FRAG_MASK);
      }
      
      public class OFMGetConfigFlag extends OFMGet<OFMessageSwitchConfig, ConfigFlag> {
         public OFMGetConfigFlag(OFMessageSwitchConfig swConfig) {
            receiver = swConfig;
         }
         
         // TODO Improvs: add another get to OFMGet
         public Boolean get (ConfigFlag flag) {
            return receiver.getFlag(flag);
         }
      }
   }
}
