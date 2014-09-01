/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.context;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
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
   
   //private Map<String, Config> protocols = new HashMap<>();
   private Config warpConfig; 
   private ConfigList protocols;
   
   private Context() {
      Config conf = ConfigFactory.load();
      String configName = conf.getString("warp-conf-name");
      if (conf.hasPath("warp-conf-path"))
         warpConfig = ConfigFactory.load(ConfigFactory.load().getString("warp-conf-path").concat("/").concat(ConfigFactory.load().getString("warp-conf-name")));
      else 
         warpConfig = ConfigFactory.load(ConfigFactory.load().getString("warp-conf-name"));
      System.out.println("");
   }
   
   public static Context getInstance () {
      return Holder.INSTANCE;
   }
   
   public String value (String protocol, String property) {
      return warpConfig.getConfig(protocol).getString(property);
   }

   private static class Holder {
      private static final Context INSTANCE = new Context();
   }
   
   public Set<String> protocols () {
      return null;
   }
}
