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
import org.flowforwarding.warp.protocol.internals.IProtocolAtom;
import org.flowforwarding.warp.protocol.internals.IProtocolContainer;
import org.flowforwarding.warp.protocol.internals.IProtocolStructure;
import org.flowforwarding.warp.protocol.internals.avro.AvroFixedField.*;
import org.flowforwarding.warp.protocol.internals.avro.AvroRecord.*;
import org.flowforwarding.warp.protocol.internals.avro.AvroEnum.*;

/**
 * @author Infoblox Inc.
 *
 */
public class AvroProtocol implements IProtocolContainer<String, GenericContainer>{

   private String avprSrc;
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
         
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
   }

   @Override
   public IProtocolStructure<String, GenericContainer> getStructure(String structureName, byte[]... in) {
      return (IProtocolStructure<String, GenericContainer>) builders.get(structureName).Value(in[0]).build();
   }
   
   @Override
   public IProtocolAtom<String, GenericContainer> getAtom(String atomName,
         byte[]... in) {
      return (IProtocolAtom<String, GenericContainer>) builders.get(atomName).Value(in[0]).build();
   }
   
   protected static AvroRecordBuilder makeRecordBuilder (String name, Schema schema) {
      
      AvroRecordBuilder b = new AvroRecordBuilder(name, schema);
      ArrayList<Field> fields = (ArrayList<Field>) schema.getFields();
      for (Field field : fields) {
         if (field.schema().getType().getName().equalsIgnoreCase("fixed")) {
            b.addItemBuilder(field.name(), new AvroFixedBuilder(field.name(), field.schema()));
         } else if (field.schema().getType().getName().equalsIgnoreCase("record")) {
            b.addItemBuilder(field.name(), makeRecordBuilder(field.name(), field.schema()));
         }
      }
      
      return b;
   }
   
   private static class Holder {
      private static final Map<String, AvroProtocol> PROTOCOLS = new HashMap<>();
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
