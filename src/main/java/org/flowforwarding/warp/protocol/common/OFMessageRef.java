/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.common;

import org.apache.avro.generic.GenericContainer;
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
   
   private String ofType = "";
   private int version;
   private IProtocolStructure<String, GenericContainer> internal;
   
   private OFMessageRef(OFMessageBuilder builder) {
      if (builder.binValue == null) {
         internal = builder.container.structure(builder.msgType, builder.binValue);
      } else {
         internal = builder.container.structure("ofp_header", builder.binValue);
         ofType = builder.container.atom("ofp_type", internal.get("type")).name();
         
         switch (ofType) {
         case "OFPT_HELLO": 
            internal = builder.container.structure("ofp_hello", builder.binValue);
            break;
         case "OFPT_ERROR":
            internal = builder.container.structure("ofp_error", builder.binValue);
             break;
         case "OFPT_FEATURES_REQUEST":
            internal = builder.container.structure("ofp_switch_features_request", builder.binValue);
             break;
         case "OFPT_FEATURES_REPLY":
            internal = builder.container.structure("ofp_switch_features", builder.binValue);
             break;
         case "OFPT_GET_CONFIG_REPLY":
            internal = builder.container.structure("ofp_switch_config", builder.binValue);
            break;
         default:
            break;
         }
      }
   }
   
   public byte[] binary() {
      return internal.binary();
   }
   
   public byte[] field(String name) {
	   return internal.binary(name);
   }
   
   public String type() {
	   return ofType;
   }
   
   public static class OFMessageBuilder{
      
      private final IProtocolContainer<String, GenericContainer> container;
      private String msgType;
      private byte[] binValue;
      private final byte version;
      
      public OFMessageBuilder (String containerType, String src) {
         
         if (containerType.equalsIgnoreCase("avro")) {
            container = AvroProtocol.getInstance(src);
            version = container.version();
         } else {
            container = null;
            version = (byte) 0xff;
         }
      }
      
      public OFMessageBuilder (String containerType, byte[] in) {
         
         if (containerType.equalsIgnoreCase("avro")) {
            container = AvroProtocol.getInstance(in[0]);
            version = container.version();
         } else {
            container = null;
            version = (byte) 0xff;
         }
      }
      
      public OFMessageBuilder type(String type) {
         this.msgType = type;
         return this;
      }
      
      public OFMessageBuilder value(byte[] in) {
         this.binValue = in;
         return this;
      }
      
      public byte version() {
         return version;
      }

      public OFMessageRef build() {
         OFMessageRef ref = new OFMessageRef (this); 
         clean();
         return ref;
      }
      
      private void clean() {
         this.binValue = null;
         this.msgType = null;
      }
   }
}
