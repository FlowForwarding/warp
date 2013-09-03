package org.flowforwarding.of.controller.session;


import java.util.HashMap;
import java.util.Map;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchRef;

import akka.actor.UntypedActor;
import akka.actor.ActorRef;

public abstract class OFSessionHandler extends OFActor{
   
   Map<SwitchRef, ActorRef> switches = new HashMap<> ();
   
   @Override
   public void onReceive(Object msg) throws Exception {
      if (msg instanceof OFEventHandshaked) {
         
         SwitchRef swState = ((OFEventHandshaked) msg).getSwitchRef();
         switches.put(swState, getSender());
         
         handshaked(swState);         
      } else if (msg instanceof EventGetSwitches) {
         System.out.println("*****");
      }
      

   }
   
   /*
    * User-defined Switch event handlers
    */
   protected void handshaked (SwitchRef swRef) {
   }
   
   protected void connected (SwitchRef swRef) {
      
   }
   
   protected void packetIn (SwitchRef swRef) {
      
   }
   
   /*
    * User-defined Application event handlers 
    */
   protected void sendSwitchConfigRequest (SwitchRef swRef) {
      switches.get(swRef).tell(new OFCommandSendSwConfigRequest(), getSelf());
   }
   
}
