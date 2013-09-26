/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */

package org.flowforwarding.of.controller;

/**
 * @author Infoblox Inc.
 * @doc.desc Contains all configuration information about OpenFlow Controller
 */
public class Configuration {

   protected int tcpPort;
   
   /**
    * 
    * @return Configuration.tcpPort
    */
   public int getTcpPort() {
      return tcpPort;
   }

   /**
    * 
    * @param tcpPort
    * int value of Tcp port
    */
   public void setTcpPort(int tcpPort) {
      this.tcpPort = tcpPort;
   }

   /**
    * Tcp port default value is 6633
    */
   public Configuration() {
      tcpPort = 6633;
   }

}
