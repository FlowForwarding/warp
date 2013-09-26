/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.controller.session;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchRef;
import org.flowforwarding.of.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigRef;

/**
 * @author Infoblox Inc.
 *
 */
public class OFEventSwitchConfig extends OFEvent {

   protected SwitchRef switchRef;
   protected OFMessageSwitchConfigRef configRef;
   
   /**
    * @param SwitchRef swH
    */
   public OFEventSwitchConfig(SwitchRef swR, OFMessageSwitchConfigRef configR) {
      switchRef = swR;
      configRef = configR;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.controller.session.OFEvent#getSwitchRef()
    */
   @Override
   public SwitchRef getSwitchRef() {
      // TODO Auto-generated method stub
      return switchRef;
   }
   
   public OFMessageSwitchConfigRef getConfigRef() {
      return configRef;
   }

   public void setConfigRef(OFMessageSwitchConfigRef configR) {
      this.configRef = configR;
   }

}