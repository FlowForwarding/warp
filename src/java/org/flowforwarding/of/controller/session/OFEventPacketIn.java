/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.controller.session;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInHandler;

/**
 * @author Infoblox Inc.
 *
 */
public class OFEventPacketIn extends OFEvent {

   protected SwitchHandler swHandler;
   protected OFMessagePacketInHandler packetIn;
   
   /**
    * @param SwitchHandler swH
    */
   public OFEventPacketIn(SwitchHandler swH, OFMessagePacketInHandler pIn) {
      swHandler = swH;
      packetIn = pIn;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.controller.session.OFEvent#getSwitchRef()
    */
   @Override
   public SwitchHandler getSwitchHandler() {
      // TODO Auto-generated method stub
      return swHandler;
   }
   
   public OFMessagePacketInHandler getPacketIn() {
      return packetIn;
   }
}