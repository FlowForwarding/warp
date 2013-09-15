package org.flowforwarding.of.demo;

import org.flowforwarding.of.controller.session.OFSessionHandler;
import org.flowforwarding.of.ofswitch.SwitchState.SwitchHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessagePacketIn;
import org.flowforwarding.of.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigHandler;

public class SimpleHandler extends OFSessionHandler {

   @Override
   protected void switchConfig(SwitchHandler swH, OFMessageSwitchConfigHandler configH) {
      super.switchConfig(swH, configH);
      
      System.out.print("[OF-INFO] DPID: " + Long.toHexString(swH.getDpid()) + " Configuration: ");

      if (configH.isFragDrop()) {
         System.out.println("Drop fragments");
      }
      
      if (configH.isFragMask()) {
         System.out.println("Mask");
      }
      
      if (configH.isFragNormal()) {
         System.out.println("Normal");
      }

      if (configH.isFragReasm()) {
         System.out.println("Reassemble");
      }      
      
   }

   @Override
   protected void handshaked(SwitchHandler swH) {
      super.handshaked(swH);
      System.out.println("[OF-INFO] HANDSHAKED " + Long.toHexString(swH.getDpid()));
      
      sendSwitchConfigRequest(swH);
   }
   
/*   @Override
   protected void packetIn(SwitchHandler swH, OFMessagePacketIn packetIn) {
      super.packetIn(swH);
      
      Long dpid = swH.getDpid();
      
   }*/

}
