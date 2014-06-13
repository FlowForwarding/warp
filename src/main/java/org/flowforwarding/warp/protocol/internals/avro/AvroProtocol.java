/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.internals.avro;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericData.Fixed;
import org.apache.avro.generic.GenericRecord;
import org.flowforwarding.warp.context.Context;
import org.flowforwarding.warp.protocol.internals.IProtocolAtom;
import org.flowforwarding.warp.protocol.internals.IProtocolContainer;
import org.flowforwarding.warp.protocol.internals.avro.AvroEnum.*;
import org.flowforwarding.warp.protocol.internals.avro.AvroRecord.*;
import org.flowforwarding.warp.protocol.internals.avro.AvroFixedField.*;
import org.flowforwarding.warp.protocol.internals.avro.AvroUnionField.*;
import org.flowforwarding.warp.protocol.internals.avro.AvroArray.*;

/**
 * @author Infoblox Inc.
 *
 */
public class AvroProtocol implements IProtocolContainer<String, GenericContainer> {

   private String avprSrc;
   private byte version;
   Protocol protocol;
   protected Map<String, AvroItemBuilder> builders = new HashMap<>();
   
   private AvroProtocol (String src) {
      this.avprSrc = src;
   }
   
   @Override
   public void init() {
      InputStream str = Thread.currentThread().getContextClassLoader().getResourceAsStream(this.avprSrc);
      try {
         protocol = Protocol.parse(str);
         
         Collection<Schema> types = protocol.getTypes();
         
         for (Schema schema : types) {
            if (schema.getType().getName().equalsIgnoreCase("fixed")) {
               builders.put(schema.getName(), new AvroFixedBuilder(schema.getName(), schema));
            } else if (schema.getType().getName().equalsIgnoreCase("record")) {
               builders.put(schema.getName(), makeRecordBuilder(schema.getName(), schema));
            } else if (schema.getType().getName().equalsIgnoreCase("enum")) {
               builders.put(schema.getName(), new AvroEnumBuilder(schema.getName(), schema));
            }
         }
         
         // TODO Improvs: Quick solution to get version.
         version = ((Fixed)((GenericRecord) builders.get("ofp_header").build().get()).get("version")).bytes()[0];
         
         
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
   }

   @Override
   public AvroRecord structure(String structureName, byte[]... in) {
      return (AvroRecord) builders.get(structureName).value(in[0]).build();
   }
   
   @Override
   public IProtocolAtom<String, GenericContainer> atom(String atomName, byte[]... in) {
      return (IProtocolAtom<String, GenericContainer>) builders.get(atomName).value(in[0]).build();
   }
   
/*   @Override
   public IProtocolAtom<String, GenericContainer> atom(String atomName, GenericContainer... in) {
      return (IProtocolAtom<String, GenericContainer>) builders.get(atomName).value(in[0]).build();
   }*/
   
   @Override
   public byte version() {
      return version;
   }
   
   protected static AvroRecordBuilder makeRecordBuilder (String name, Schema schema) {
      
      AvroRecordBuilder b = new AvroRecordBuilder(name, schema);
      ArrayList<Field> fields = (ArrayList<Field>) schema.getFields();
      for (Field field : fields) {
         if (field.schema().getType().getName().equalsIgnoreCase("fixed") || field.schema().getType().getName().equalsIgnoreCase("bitmap")) {
            b.addItemBuilder(field.name(), new AvroFixedBuilder(field.name(), field.schema()));
            if (field.defaultValue() == null)
               b.notReadyToBinary();
         } else if (field.schema().getType().getName().equalsIgnoreCase("record")) {
            b.addItemBuilder(field.name(), makeRecordBuilder(field.name(), field.schema()));
            if (field.defaultValue() == null)
               b.notReadyToBinary();
         } else if (field.schema().getType().getName().equalsIgnoreCase("union")) {
            b.addItemBuilder(field.name(), new AvroUnionBuilder(field.name(), field.schema()));
            b.notReadyToBinary();
         } else if (field.schema().getType().getName().equalsIgnoreCase("array")) {
            b.addItemBuilder(field.name(), new AvroArrayBuilder(field.name(), field.schema()));
            b.notReadyToBinary();
         }
      }
      
      return b;
   }
   
   private static class Holder {
      private static final Map<String, AvroProtocol> PROTOCOLS = new HashMap<>();
   }
   
   public static AvroProtocol getInstance (byte version) {
      return getInstance(Context.getInstance().value("OFP", "version." + Byte.toString(version)));
   }
   
   public static AvroProtocol getInstance (String src) {
      if (Holder.PROTOCOLS.containsKey(src)) 
         return Holder.PROTOCOLS.get(src);
      else {
         AvroProtocol newProtocol = new AvroProtocol(src);
         newProtocol.init();
         Holder.PROTOCOLS.put(src, newProtocol);
         
         return newProtocol;
      }
   }
}
