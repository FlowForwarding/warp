package org.flowforwarding.warp.driver;

import org.flowforwarding.warp.driver.conf.DriverConfiguration;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
       System.out.println(DriverConfiguration.getInstance().version); 
    }
}
