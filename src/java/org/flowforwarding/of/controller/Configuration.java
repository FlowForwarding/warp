/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */

package org.flowforwarding.of.controller;

/**
 * Class Configuration
 * @author Infoblox Inc.
 * 
 *  Stores all configuration information about the Controller:
 *  <li> Tcp port number
 * 
 */
public class Configuration {

   public int getTcpPort() {
      return tcpPort;
   }

   public void setTcpPort(int tcpPort) {
      this.tcpPort = tcpPort;
   }

   protected int tcpPort;
   
   public Configuration() {
      tcpPort = 6633;
   }

}
