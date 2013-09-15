package org.flowforwarding.of.controller.session;


import java.util.HashMap;
import java.util.Map;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessageError;
import org.flowforwarding.of.protocol.ofmessages.OFMessageError.OFMessageErrorHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigHandler;

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
         OFMessageSwitchConfigHandler configH = ((OFEventSwitchConfig) msg).getConfigHandler();
         switchConfig(swH, configH);
         
      } else if (msg instanceof EventGetSwitches) {
         
      } else if (msg instanceof OFEventError) {
         error(((OFEventError)msg).getSwitchHandler(), ((OFEventError)msg).getError());
      }
   }
   
   /*
    * User-defined Switch event handlers
    */
   protected void handshaked(SwitchHandler swH) {}
   protected void connected(SwitchHandler swH) {}
   protected void packetIn(SwitchHandler swH, OFMessagePacketInHandler pIn) {}
   protected void switchConfig(SwitchHandler swH, OFMessageSwitchConfigHandler configH) {}
   protected void error(SwitchHandler swH, OFMessageErrorHandler error) {}
   
   
   /*
    * User-defined Application event handlers 
    */
   protected void sendSwitchConfigRequest (SwitchHandler swH) {
      switches.get(swH).tell(new OFCommandSendSwConfigRequest(), getSelf());
   }
   
}
