/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.common;

import org.flowforwarding.warp.protocol.internals.IProtocolContainer;
import org.flowforwarding.warp.protocol.internals.IProtocolStructure;
import org.flowforwarding.warp.protocol.internals.avro.AvroProtocol;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessageRef {
   
   private IProtocolStructure<?, ?> internal;
   private OFMessageRef(OFMessageBuilder builder) {
      this.internal = builder.container.getStructure(builder.msgType, builder.binValue);
   }
   
   public byte[] encode() {
      return internal.encode();
   }
   
   public String getType () {
      return internal.getName();
   }
   
   public static class OFMessageBuilder{
      
      private final String source;
      private final IProtocolContainer<?, ?> container;
      private String msgType;
      private byte[] binValue;
      
      public OFMessageBuilder (String containerType, String src) {
         
         if (containerType.equalsIgnoreCase("avro")) {
            container = AvroProtocol.getInstance(src);
         } else {
            container = null;
         }
         this.source = src;
      }
      
      public OFMessageBuilder Type(String type) {
         this.msgType = type;
         return this;
      }
      
      public OFMessageBuilder Value(byte[] in) {
         this.binValue = in;
         return this;
      }
      
      
      public OFMessageRef build() {
         return new OFMessageRef (this);
      }
   }
}
