/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofmessages;

import java.util.HashMap;
import java.util.Map;

import org.flowforwarding.of.protocol.supply.OFMAddConfigFlag;

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
   
   protected OFMessageSwitchConfig() {
      configFlags = new HashMap<>();
   }
   
   public static class OFMessageSwitchConfigHandler extends OFMessageHandler <OFMessageSwitchConfig> {
      
      protected OFMAddConfigFlag addFlag;
      
      protected OFMessageSwitchConfigHandler() {
         message = new OFMessageSwitchConfig();
         addFlag = new OFMAddConfigFlag(message);
      }
      
      public static OFMessageSwitchConfigHandler create () {
         return new OFMessageSwitchConfigHandler();
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
   }
}
