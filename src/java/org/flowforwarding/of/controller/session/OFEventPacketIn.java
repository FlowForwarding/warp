/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.controller.session;

import org.flowforwarding.of.ofswitch.SwitchState.SwitchRef;
import org.flowforwarding.of.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInRef;

/**
 * @author Infoblox Inc.
 * @doc Event: incoming OpenFlow Packet-In message!
 *
 */
public class OFEventPacketIn extends OFEvent {

   protected SwitchRef switchRef;
   protected OFMessagePacketInRef packetIn;
   
   /**
    * @param SwitchRef swRef
    * @param OFMessagePacketInRef pInRef
    */
   public OFEventPacketIn(SwitchRef swRef, OFMessagePacketInRef pInRef) {
      switchRef = swRef;
      packetIn = pInRef;
   }

   /**
    * @return SwitchRef switchRef
    * @see org.flowforwarding.of.controller.session.OFEvent#getSwitchRef()
    */
   @Override
   public SwitchRef getSwitchRef() {
      // TODO Auto-generated method stub
      return switchRef;
   }
   
   /**
    * @return OFMessagePacketInRef packetIn
    */
   public OFMessagePacketInRef getPacketIn() {
      return packetIn;
   }
}