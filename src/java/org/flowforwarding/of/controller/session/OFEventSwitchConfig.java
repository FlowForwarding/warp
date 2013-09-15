/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.controller.session;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigHandler;

/**
 * @author Infoblox Inc.
 *
 */
public class OFEventSwitchConfig extends OFEvent {

   protected SwitchHandler swHandler;
   protected OFMessageSwitchConfigHandler configHandler;
   
   /**
    * @param SwitchHandler swH
    */
   public OFEventSwitchConfig(SwitchHandler swH, OFMessageSwitchConfigHandler configH) {
      swHandler = swH;
      configHandler = configH;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.controller.session.OFEvent#getSwitchRef()
    */
   @Override
   public SwitchHandler getSwitchHandler() {
      // TODO Auto-generated method stub
      return swHandler;
   }
   
   public OFMessageSwitchConfigHandler getConfigHandler() {
      return configHandler;
   }

   public void setConfigHandler(OFMessageSwitchConfigHandler configHandler) {
      this.configHandler = configHandler;
   }

}