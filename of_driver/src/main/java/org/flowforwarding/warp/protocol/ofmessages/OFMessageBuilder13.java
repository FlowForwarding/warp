/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.ofmessages;

import org.flowforwarding.warp.protocol.ofmessages.IOFMessageBuilder;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageError.OFMessageErrorRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigRef;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessageBuilder13 implements IOFMessageBuilder {

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.controller.protocol.OFMessageBuilder#build(java.lang.String)
    */
   @Override
   public OFMessageRef build(String msg) {

      return null;
   }
   
   @Override
   public OFMessageFlowModRef buildFlowMod() {
      // TODO Improvs. To make bulder a REAL builder: FlowMod and Ref should be combined from parts.
      // TODO Improvs: OFMessageRef?
      return OFMessageFlowModRef.create();
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageBuilder#buildSwitchConfig()
    */
   @Override
   public OFMessageSwitchConfigRef buildSwitchConfig() {
      // TODO Improvs. To make bulder a REAL builder: Config and Handler should be combined from parts.
      // TODO Improvs: OFMessageRef?
      return OFMessageSwitchConfigRef.create();
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageBuilder#buildPacketIn()
    */
   @Override
   public OFMessagePacketInRef buildPacketIn() {
      // TODO Auto-generated method stub
      return OFMessagePacketInRef.create();
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageBuilder#buildError()
    */
   @Override
   public OFMessageErrorRef buildError() {
      // TODO Auto-generated method stub
      return OFMessageErrorRef.create();
   }

}
