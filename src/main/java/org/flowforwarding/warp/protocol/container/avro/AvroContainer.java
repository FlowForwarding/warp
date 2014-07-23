/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.container.avro;

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
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericData.Fixed;
import org.flowforwarding.warp.context.Context;
import org.flowforwarding.warp.protocol.container.IAtom;
import org.flowforwarding.warp.protocol.container.IBuilder;
import org.flowforwarding.warp.protocol.container.IContainer;
import org.flowforwarding.warp.protocol.container.avro.AvroFixed.AvroFixedBuilder;
import org.flowforwarding.warp.protocol.container.avro.AvroRecord.AvroRecordBuilder;

/**
 * @author Infoblox Inc.
 *
 */
public class AvroContainer implements IContainer <String, GenericContainer> {

   private String avprSrc;
   private byte version;
   Protocol protocol;
   
   protected Map<String, IBuilder<String, GenericContainer>> builders = new HashMap<>();
   
   private AvroContainer (String src) {
      this.avprSrc = src;
   }
   
   @Override
   public IAtom<String, GenericContainer> atom(String atomName, byte[]... in) {
      return (IAtom<String, GenericContainer>) builders.get(atomName).value(in[0]).build();
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
            }
         }
         
         // TODO Improvs: Just a quick solution to get version.
         //version = ((Fixed)((GenericRecord) builders.get("ofp_header").build().get()).get("version")).bytes()[0];
         
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
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
         }/* else if (field.schema().getType().getName().equalsIgnoreCase("union")) {
            b.addItemBuilder(field.name(), new AvroUnionBuilder(field.name(), field.schema()));
            b.notReadyToBinary();
         } else if (field.schema().getType().getName().equalsIgnoreCase("array")) {
            b.addItemBuilder(field.name(), new AvroArrayBuilder(field.name(), field.schema()));
            b.notReadyToBinary();
         }*/
      }
      
      return b;
   }
   
   private static class Holder {
      private static final Map<String, AvroContainer> PROTOCOLS = new HashMap<>();
   }
   
   public static AvroContainer getInstance (byte version) {
      return getInstance(Context.getInstance().value("OFP", "version." + Byte.toString(version)));
   }
   
   public static AvroContainer getInstance (String src) {
      if (Holder.PROTOCOLS.containsKey(src)) 
         return Holder.PROTOCOLS.get(src);
      else {
         AvroContainer newProtocol = new AvroContainer(src);
         newProtocol.init();
         Holder.PROTOCOLS.put(src, newProtocol);
         
         return newProtocol;
      }
   }
}
