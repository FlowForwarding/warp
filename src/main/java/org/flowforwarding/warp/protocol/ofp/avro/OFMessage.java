/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.ofp.avro;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.generic.GenericContainer;
import org.flowforwarding.warp.protocol.container.INamedValue;
import org.flowforwarding.warp.protocol.container.avro.AvroContainer;
import org.flowforwarding.warp.protocol.container.avro.AvroItem;
import org.flowforwarding.warp.protocol.ofp.IMessage;
import org.flowforwarding.warp.protocol.ofp.IMessageBuilder;
import org.flowforwarding.warp.util.Tuple;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessage implements IMessage<AvroItem> {
   
   private AvroItem internal;
   private String ofType = "";
   private Ref ref;
   
   private OFMessage(OFMessageBuilder builder) {
      if (builder.binValue == null) {
         internal = builder.container.structure(builder.msgType, builder.binValue);
      } else {
         internal = builder.container.structure("ofp_header", builder.binValue);
         ofType = builder.container.atom("ofp_type", internal.binary("type")).name();
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
      for (Tuple <String, String>t : builder.items) {
         this.set(t.getName(), t.getValue());
      }
      ref = new Ref (internal);
      builder.items.clear();
   }
   
   private void set(String name, String value) {
   }
   
   //TODO I: should we put the declaration into Interface?
   public String type() {
      return ofType;
   }

   //TODO I: should we put the declaration into Interface?   
   public byte[] binary() {
      return internal.binary();
   }

   //TODO I: should we put the declaration into Interface?
   public byte[] field(String name) {
      return internal.binary(name);
   }
   
   public Ref get(String name) {
      ref.set((AvroItem) internal.field(name));
      return ref;
   }
   
   public class Ref {
      private AvroItem internal;
      private Ref(AvroItem i) {internal = i;}

      public void set(AvroItem i) {internal = i;}
      public void set(byte[] value) {internal.set(value);}
      
      public Ref get(String name) {
         //TODO I: Class-cast
         this.internal = (AvroItem) internal.field(name);
         return this;
      }
      
   }

   public static class OFMessageBuilder implements IMessageBuilder<AvroItem>{
      private final AvroContainer container;
      private final byte version;      
      private String msgType;
      private byte[] binValue;
      private List<Tuple<String, String>> items = new ArrayList<>();

      
      public OFMessageBuilder (String src) {
         //TODO I: place for error-handling
         container = AvroContainer.getInstance(src);
         version = container.version();
      }
      
      public OFMessageBuilder (byte[] in) {
         //TODO I: place for error-handling         
         container = AvroContainer.getInstance(in[0]);
         version = container.version();
      }
      
      public OFMessageBuilder type(String type) {
         this.msgType = type;
         return this;
      }
      
      public OFMessageBuilder value(byte[] in) {
         this.binValue = in;
         return this;
      }
      
      public OFMessageBuilder set(String name, String value) {
         items.add(new Tuple<>(name, value));         
         return this;
      }
      
      public byte version() {
         return version;
      }

      public OFMessage build() {
         OFMessage msg = new OFMessage (this); 
         clean();
         return msg;
      }
      
      private void clean() {
         this.binValue = null;
         this.msgType = null;
      }
   }
}
