/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.supply;

import org.flowforwarding.of.protocol.ofmessages.OFMessageSwitchConfig;
import org.flowforwarding.of.protocol.ofmessages.OFMessageSwitchConfig.ConfigFlag;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMAddConfigFlag extends OFMAdd<OFMessageSwitchConfig, ConfigFlag, Boolean>{
   
   public OFMAddConfigFlag(OFMessageSwitchConfig swConf) {
      receiver = swConf;
   }
   
   @Override
   public void add (ConfigFlag flag, Boolean value) {
      receiver.setFlag(flag, value);
   }

}
