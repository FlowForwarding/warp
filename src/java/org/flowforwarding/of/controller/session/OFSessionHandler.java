package org.flowforwarding.of.controller.session;


import java.util.HashMap;
import java.util.Map;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInHandler;

import akka.actor.ActorRef;

public abstract class OFSessionHandler extends OFActor{
   
   Map<SwitchHandler, ActorRef> switches = new HashMap<> ();
   
   @Override
   public void onReceive(Object msg) throws Exception {
      
      // TODO Improvs: replace if-else with HashMap
      if (msg instanceof OFEventHandshaked) {
         
         SwitchHandler swH = ((OFEventHandshaked) msg).getSwitchHandler();
         switches.put(swH, getSender());
         
         handshaked(swH);         
      } else if (msg instanceof OFEventIncoming) {
         
      } else if (msg instanceof OFEventPacketIn) {
         SwitchHandler swH = ((OFEventPacketIn) msg).getSwitchHandler();
         OFMessagePacketInHandler pIn = ((OFEventPacketIn) msg).getPacketIn();
         packetIn(swH, pIn);
         
      } else if (msg instanceof OFEventSwitchConfig) {
         SwitchHandler swH = ((OFEventSwitchConfig) msg).getSwitchHandler();
         switchConfig(swH);
         
      } else if (msg instanceof EventGetSwitches) {
         
      } 
   }
   
   /*
    * User-defined Switch event handlers
    */
   protected void handshaked(SwitchHandler swH) {}
   protected void connected(SwitchHandler swH) {}
   protected void packetIn(SwitchHandler swH, OFMessagePacketInHandler pIn) {}
   protected void switchConfig(SwitchHandler swH) {}
   
   
   /*
    * User-defined Application event handlers 
    */
   protected void sendSwitchConfigRequest (SwitchHandler swH) {
      switches.get(swH).tell(new OFCommandSendSwConfigRequest(), getSelf());
   }
   
}
