/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.ofmessages;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessageFeatureReply extends OFMessage{
   
   protected OFMessageFeatureReply () {
   }
   
   public static class OFMessageFeatureReplyRef extends OFMessageRef<OFMessageFeatureReply> {
      
      protected OFMessageFeatureReplyRef () {
         message = new OFMessageFeatureReply();
      }
      
      public static OFMessageFeatureReplyRef create() {
         return new OFMessageFeatureReplyRef();
      }

      // TODO Improvs: rewrite with commands classes

   }
}
