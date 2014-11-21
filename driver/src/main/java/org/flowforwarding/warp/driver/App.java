package org.flowforwarding.warp.driver;

import org.flowforwarding.warp.driver.conf.DriverConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Hello world!
 *
 */
public class App
{
   private static final Logger LOG = LoggerFactory.getLogger(App.class);
    public static void main( String[] args )
    {
       LOG.info("Version: " + DriverConfiguration.getInstance().version);
    }
}
