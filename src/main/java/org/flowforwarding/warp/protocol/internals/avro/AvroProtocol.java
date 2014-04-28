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
import org.flowforwarding.warp.protocol.internals.IProtocolContainer;
import org.flowforwarding.warp.protocol.internals.IProtocolStructure;

/**
 * @author Infoblox Inc.
 *
 */
public class AvroProtocol implements IProtocolContainer<String, GenericContainer>{

   private String avprSrc;
   Protocol protocol;
   protected Map<String, AvroItemBuilder> builders = new HashMap<>();
   
   @Override
   public void init() {
      // TODO Auto-generated method stub
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.internals.IProtocolContainer#init(java.lang.String)
    */
   @Override
   public void init(String src) {
      this.avprSrc = src;

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

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.internals.IProtocolContainer#getStructure(java.lang.String)
    */
   @Override
   public IProtocolStructure<String, GenericContainer> getStructure(
         String structureName) {
      // TODO Auto-generated method stub
      return null;
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

}
