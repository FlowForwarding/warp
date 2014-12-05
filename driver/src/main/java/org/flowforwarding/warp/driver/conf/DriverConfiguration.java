/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.driver.conf;
import java.io.File;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigParseOptions;
import com.typesafe.config.ConfigResolveOptions;

/**
 * @author Infoblox
 * 
 */
public class DriverConfiguration {
   
   private static Config driverConfig;
   private static Config warpConfig;
   
   private static int version;
   
   private DriverConfiguration () {
      driverConfig = ConfigFactory.load("driver");
      warpConfig = ConfigFactory.parseFile(new File(driverConfig.getString("warp-config")));
      version = warpConfig.getInt("version");
   }
   
   synchronized private static void reInitialize () {
      ConfigFactory.invalidateCaches();
      
      ConfigResolveOptions opts = ConfigResolveOptions.defaults();
      opts.setUseSystemEnvironment(false);
      driverConfig = ConfigFactory.load("driver.conf", ConfigParseOptions.defaults(), opts);
      
      warpConfig = ConfigFactory.parseFile(new File(driverConfig.getString("warp-config")));
      version = warpConfig.getInt("version");
   }
   
   public int version () {
      return version;
   }
   
   public static DriverConfiguration getInstance () {
      return Holder.INSTANCE;
   }
   
   public static void reload() {
      DriverConfiguration.reInitialize();
   }
   
   private static class Holder {
      private static final DriverConfiguration INSTANCE = new DriverConfiguration();
   }

}
