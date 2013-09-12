/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofmessages;

import org.flowforwarding.of.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInHandler;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessagePacketIn extends OFMessage {
   
   public static class OFMessagePacketInHandler extends OFMessageHandler <OFMessagePacketIn> {

      /**
       * @return
       */
      public static OFMessagePacketInHandler create() {
         // TODO Auto-generated method stub
         return new OFMessagePacketInHandler();
      }
   }

}
