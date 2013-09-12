package org.flowforwarding.of.controller.session;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchHandler;

public class OFEventHandshaked extends OFEvent {
   
   protected SwitchHandler swHandler;
   
   public SwitchHandler getSwitchHandler() {
      return swHandler;
   }

   public void setSwitchHandler(SwitchHandler swRef) {
      this.swHandler = swRef;
   }

   public OFEventHandshaked(SwitchHandler swRef) {
      super();
      this.swHandler = swRef;
   }

}
