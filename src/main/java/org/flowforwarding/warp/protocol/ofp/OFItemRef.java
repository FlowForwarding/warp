/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.ofp;

import org.flowforwarding.warp.protocol.internals.avro.AvroItem;
import org.flowforwarding.warp.protocol.internals.avro.AvroProtocol;
import org.flowforwarding.warp.util.Convert;

/**
 * @author Infoblox Inc.
 *
 */
public class OFItemRef {
   
   private AvroItem internal;
   private int version;
   
   private OFItemRef(OFItemBuilder builder) {
      internal = (AvroItem) builder.container.atom(builder.msgType, builder.binValue);
   }
   
   protected OFItemRef(AvroItem i) {
      internal = i;
   }
   
   public void add(OFItemRef value) {
      internal.add(Internal.get(value));
   }
   
   public void add(String name, OFItemRef value) {
      internal.add(name, Internal.get(value));      
   }
   
   public void set(String name, String value) {
      internal.set(name, Convert.toArray(value));
   }
   
   protected static class Internal {
      public static AvroItem get(OFItemRef ref) {
         return ref.internal;
      }
   }
   
   public static class OFItemBuilder {
      private final AvroProtocol container;
      private int version;
      private String msgType;
      private byte[] binValue;
      
      public OFItemBuilder (String containerType, String src) {
         
         if (containerType.equalsIgnoreCase("avro")) {
            container = AvroProtocol.getInstance(src);
            version = container.version();
         } else {
            container = null;
            version = (byte) 0xff;
         }
      }
      
      public OFItemBuilder (String containerType, byte[] in) {
         
         if (containerType.equalsIgnoreCase("avro")) {
            container = AvroProtocol.getInstance(in[0]);
            version = container.version();
         } else {
            container = null;
            version = (byte) 0xff;
         }
      }
      
      public OFItemBuilder type(String type) {
         this.msgType = type;
         return this;
      }
      
      public OFItemRef build() {
         OFItemRef ref = new OFItemRef (this); 
         clean();
         return ref;
      }
      
      private void clean() {
         this.binValue = null;
         this.msgType = null;
      }


   }
}
