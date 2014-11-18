/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.driver.conf;
import java.io.File;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * @author Infoblox
 * 
 */
public class DriverConfiguration {
   
   private Config driverConfig;
   private Config warpConfig;
   
   public int version;
   
   private DriverConfiguration () {
      driverConfig = ConfigFactory.load("driver");
      warpConfig = ConfigFactory.parseFile(new File(driverConfig.getString("warp-config")));
      
      version = warpConfig.getInt("version");
   }
   
   public static DriverConfiguration getInstance () {
      return Holder.INSTANCE;
   }
   
   private static class Holder {
      private static final DriverConfiguration INSTANCE = new DriverConfiguration();
   }

}
