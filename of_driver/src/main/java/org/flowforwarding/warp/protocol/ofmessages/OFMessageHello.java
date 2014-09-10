/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.ofmessages;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessageHello extends OFMessage{
   
   protected OFMessageHello () {
   }
   
   public static class OFMessageHelloRef extends OFMessageRef<OFMessageHello> {
      
      protected OFMessageHelloRef () {
         message = new OFMessageHello();
      }
      
      public static OFMessageHelloRef create() {
         return new OFMessageHelloRef();
      }

      // TODO Improvs: rewrite with commands classes

   }
}
