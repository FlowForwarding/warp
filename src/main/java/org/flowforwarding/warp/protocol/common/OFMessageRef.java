/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.common;

import org.apache.avro.generic.GenericData.Fixed;
import org.flowforwarding.warp.protocol.internals.IProtocolAtom;
import org.flowforwarding.warp.protocol.internals.IProtocolContainer;
import org.flowforwarding.warp.protocol.internals.IProtocolStructure;
import org.flowforwarding.warp.protocol.internals.avro.AvroProtocol;
import org.flowforwarding.warp.protocol.internals.avro.AvroRecord;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessageRef {
   
   private String ofType; 
   private IProtocolStructure<?, ?> internal;
   
   private OFMessageRef(OFMessageBuilder builder) {
      if (builder.binValue == null) {
         internal = builder.container.getStructure(builder.msgType, builder.binValue);
      } else {
         internal = builder.container.getStructure("ofp_header", builder.binValue);
         IProtocolAtom<?, ?> atom = builder.container.getAtom("ofp_type", ((Fixed)internal.get("type")).bytes());
//         String type = builder.container.getAtom("ofp_type", ((Fixed)internal.get("type")).bytes()).getName();
         System.out.println("");
      }
   }
   
   public byte[] encode() {
      return internal.encode();
   }
   
   public String getName () {
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
