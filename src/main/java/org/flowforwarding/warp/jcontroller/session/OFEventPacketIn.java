/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.jcontroller.session;

import org.flowforwarding.warp.ofswitch.SwitchState.SwitchRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInRef;

/**
 * @author Infoblox Inc.
 * @doc.desc Event: incoming OpenFlow Packet-In message!
 *
 */
public class OFEventPacketIn extends OFEvent {

   protected SwitchRef switchRef;
   protected OFMessagePacketInRef packetIn;
   
   /**
    * @param swRef
    * Reference to Switch
    * @param pInRef
    * Reference to Packet-In message
    */
   public OFEventPacketIn(SwitchRef swRef, OFMessagePacketInRef pInRef) {
      switchRef = swRef;
      packetIn = pInRef;
   }

   /**
    * @return SwitchRef switchRef
    * @see OFEvent#getSwitchRef()
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