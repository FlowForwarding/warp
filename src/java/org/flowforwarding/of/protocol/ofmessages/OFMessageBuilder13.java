/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofmessages;

import org.flowforwarding.of.protocol.ofmessages.IOFMessageBuilder;
import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigHandler;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessageBuilder13 implements IOFMessageBuilder {

   /* (non-Javadoc)
    * @see org.flowforwarding.of.controller.protocol.OFMessageBuilder#build(java.lang.String)
    */
   @Override
   public OFMessageHandler build(String msg) {

      return null;
   }
   
   @Override
   public OFMessageFlowModHandler buildFlowMod() {
      // TODO Improvs. To make bulder a REAL builder: FlowMod and Ref should be combined from parts.
      // TODO Improvs: OFMessageHandler?
      return OFMessageFlowModHandler.create();
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageBuilder#buildSwitchConfig()
    */
   @Override
   public OFMessageSwitchConfigHandler buildSwitchConfig() {
      // TODO Improvs. To make bulder a REAL builder: Config and Handler should be combined from parts.
      // TODO Improvs: OFMessageHandler?
      return OFMessageSwitchConfigHandler.create();
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageBuilder#buildPacketIn()
    */
   @Override
   public OFMessagePacketInHandler buildPacketIn() {
      // TODO Auto-generated method stub
      return OFMessagePacketInHandler.create();
   }

}
