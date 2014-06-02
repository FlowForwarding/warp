/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;
import com.typesafe.config.ConfigValue;

/**
 * @author Infoblox Inc.
 *
 */
public class Context {
   
   private final Map<String, Map<String, Object>> protocols = new HashMap<>();
   
   private Context() {
      Config warpConfig = ConfigFactory.load(ConfigFactory.load().getString("warp-conf"));
      
      ConfigList protocols = warpConfig.getList("protocols");
      
      for (ConfigValue protocol : protocols) {
         Config protocolProperties = warpConfig.getConfig(protocol.render());
         Map <String, Object> protocolPropsEntry = new HashMap<>();
         
         Set<Entry<String, ConfigValue>> set = protocolProperties.entrySet();
         for (Entry<String, ConfigValue> entry : set)
            protocolPropsEntry.put(entry.getKey(), entry.getValue().render());
         
         this.protocols.put(protocol.render(), protocolPropsEntry);
      }
   }
   
   public static Context getInstance () {
      return Holder.INSTANCE;
   }

   private static class Holder {
      private static final Context INSTANCE = new Context();
   }
   
   public Map <String, Object> protocolProperties (String protocol) {
      return protocols.get(protocol);
   }
   
   public Set<String> protocols () {
      return protocols.keySet();
   }
}
