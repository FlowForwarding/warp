package org.flowforwarding.of.demo;

import org.flowforwarding.of.controller.session.OFSessionHandler;
import org.flowforwarding.of.ofswitch.SwitchState.SwitchHandler;

public class SimpleHandler extends OFSessionHandler {

   @Override
   protected void handshaked(SwitchHandler swH) {
      super.handshaked(swH);
      System.out.println("[OF-INFO] HANDSHAKED " + Long.toHexString(swH.getDpid()));
      
      sendSwitchConfigRequest(swH);
   }
   
/*   @Override
   protected void packetIn(SwitchHandler swH, OFMessag) {
      super.packetIn(swH);
      
      Long dpid = swH.getDpid();
      
   }*/

}
