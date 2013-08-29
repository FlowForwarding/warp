package org.flowforwarding.of.controller.session;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchRef;

public class OFEventHandshaked implements OFEvent {
   
   protected SwitchRef switchRef;
   
   public SwitchRef getSwitchRef() {
      return switchRef;
   }

   public void setSwitchRef(SwitchRef swRef) {
      this.switchRef = swRef;
   }

   public OFEventHandshaked(SwitchRef swRef) {
      super();
      this.switchRef = swRef;
   }

}
