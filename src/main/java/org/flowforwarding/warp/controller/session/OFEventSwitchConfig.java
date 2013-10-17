/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.controller.session;

import org.flowforwarding.warp.ofswitch.SwitchState.SwitchRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigRef;

/**
 * @author Infoblox Inc.
 *
 */
public class OFEventSwitchConfig extends OFEvent {

   protected SwitchRef switchRef;
   protected OFMessageSwitchConfigRef configRef;
   
   /**
    * @param switchRef
    * Reference to Switch
    * @param configRef
    * Reference to OpenFlow Switch Config
    */
   public OFEventSwitchConfig(SwitchRef switchRef, OFMessageSwitchConfigRef configRef) {
      this.switchRef = switchRef;
      this.configRef = configRef;
   }

   /**
    * @see org.flowforwarding.warp.controller.session.OFEvent#getSwitchRef()
    */
   @Override
   public SwitchRef getSwitchRef() {
      // TODO Auto-generated method stub
      return switchRef;
   }
   
  /**
   * 
   * @return OFMessageSwitchConfigRef configRef
   */
   public OFMessageSwitchConfigRef getConfigRef() {
      return configRef;
   }

   /**
    * 
    * @param configRef
    * Reference to OpenFlowSwitch Config
    */
   public void setConfigRef(OFMessageSwitchConfigRef configRef) {
      this.configRef = configRef;
   }

}