/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.controller.session;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchRef;

/**
 * @author Infoblox Inc.
 * @doc.desc Issued after Controller and Switch perform Handshake procedure 
 */
public class OFEventHandshaked extends OFEvent {
   
   protected SwitchRef switchRef;
   
   /**
    * @return SwitchRef switchRef
    */
   public SwitchRef getSwitchRef() {
      return switchRef;
   }

   /**
    * @param swRef
    * Reference to Switch
    */
   public void setSwitchRef(SwitchRef swRef) {
      this.switchRef = swRef;
   }
   
   /**
    * @param swRef
    * Reference to Switch 
    */
   public OFEventHandshaked(SwitchRef swRef) {
      super();
      this.switchRef = swRef;
   }

}
