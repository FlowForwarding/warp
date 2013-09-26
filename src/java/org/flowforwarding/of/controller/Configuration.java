/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */

package org.flowforwarding.of.controller;

/**
 * @author Infoblox Inc.
 * @doc Contains all configuration information about OpenFlow Controller
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
