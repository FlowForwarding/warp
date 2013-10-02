/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.controller.session;


import java.util.HashMap;
import java.util.Map;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchRef;
import org.flowforwarding.of.protocol.ofmessages.OFMessageError;
import org.flowforwarding.of.protocol.ofmessages.OFMessageError.OFMessageErrorRef;
import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModRef;
import org.flowforwarding.of.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInRef;
import org.flowforwarding.of.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigRef;

import akka.actor.ActorRef;

/**
 * @author Infoblox Inc.
 * @doc.desc OpenFlow protocol Session handler base class
 *
 */
public abstract class OFSessionHandler extends OFActor{
   
   Map<SwitchRef, ActorRef> switches = new HashMap<> ();
   
   @Override
   public void onReceive(Object msg) throws Exception {
      
      // TODO Improvs: replace if-else with HashMap
      if (msg instanceof OFEventHandshaked) {
         
         SwitchRef swR = ((OFEventHandshaked) msg).getSwitchRef();
         switches.put(swR, getSender());
         
         handshaked(swR);         
      } else if (msg instanceof OFEventIncoming) {
         
      } else if (msg instanceof OFEventPacketIn) {
         SwitchRef swR = ((OFEventPacketIn) msg).getSwitchRef();
         OFMessagePacketInRef pIn = ((OFEventPacketIn) msg).getPacketIn();
         packetIn(swR, pIn);
         
      } else if (msg instanceof OFEventSwitchConfig) {
         SwitchRef swR = ((OFEventSwitchConfig) msg).getSwitchRef();
         OFMessageSwitchConfigRef configH = ((OFEventSwitchConfig) msg).getConfigRef();
         switchConfig(swR, configH);
         
 /*     } else if (msg instanceof EventGetSwitches) { */
         
      } else if (msg instanceof OFEventError) {
         error(((OFEventError)msg).getSwitchRef(), ((OFEventError)msg).getError());
      }
   }
   
   /*
    * User-defined Switch event handlers
    */
   protected void handshaked(SwitchRef swR) {}
   protected void connected(SwitchRef swR) {}
   protected void packetIn(SwitchRef swR, OFMessagePacketInRef pIn) {}
   protected void switchConfig(SwitchRef swR, OFMessageSwitchConfigRef configH) {}
   protected void error(SwitchRef swR, OFMessageErrorRef error) {}
   
   
   /**
    * 
    * @param swR
    * Reference to Switch
    */
   protected void sendSwitchConfigRequest (SwitchRef swR) {
      switches.get(swR).tell(new OFCommandSendSwConfigRequest(), getSelf());
   }
   
   /**
    * 
    * @param swR
    * Reference to switch
    * @param flowMod
    * Reference to OF Flow Mod 
    */
   protected void sendFlowModMessage (SwitchRef swR, OFMessageFlowModRef flowMod) {
      switches.get(swR).tell(new OFCommandSendSwConfigRequest(), getSelf());
   }
}
