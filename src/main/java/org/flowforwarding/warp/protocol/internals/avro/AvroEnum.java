/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.internals.avro;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericData.Fixed;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.codehaus.jackson.JsonNode;
import org.flowforwarding.warp.protocol.internals.IProtocolAtom;

/**
 * @author Infoblox Inc.
 *
 */
public class AvroEnum implements IProtocolAtom <String, GenericContainer> {

   protected final String name;
   protected final Schema schema;
   
   // TODO Improv: Fixed? array? We should think about it.
   protected GenericContainer value;
   
   @Override
   public GenericContainer get() {
      // TODO Improve: catch possible exception. It will be thrown in case inconsistence between value and schema
      return null;

   }
   
   private AvroEnum (AvroEnumBuilder builder) {
      schema = builder.schema;
      
      if (builder.value != null) {
         value = builder.value;
         name = builder.keys.get(value);
         builder.value = null;
      } else {
         name = null;
      }
/*      if (builder.binValue != null) {
         fixedValue = new GenericData.Fixed(schema);
         GenericDatumReader<Fixed> reader = new GenericDatumReader<>(schema);
         Decoder decoder = DecoderFactory.get().binaryDecoder(builder.binValue, null);
         
         try {
            reader.read(fixedValue, decoder);
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }*/
   }
   
   @Override
   public String name() {
      return name;
   }

   public Schema getSchema() {
      return schema;
   }

   public GenericContainer getValue() {
      return value;
   }

   @Override
   public void set(byte[] value) {
      // TODO Improve: throw Exception??!
   }
   
   public static class AvroEnumBuilder extends AvroItemBuilder{
      protected String name;
      protected Schema schema;
      protected Schema itemsType;
      protected byte[] binValue;
      protected GenericContainer value;
      
      protected Map <GenericContainer, String> keys = null;  
      protected Map <String, GenericContainer> values = null;

      @Override
      public AvroEnum build() {
         return new AvroEnum(this);
      }
      
      @Override
      public AvroEnumBuilder value (byte[] in) {
         this.binValue = in;
         //TODO Improvs: Fixed only?
         this.value = new GenericData.Fixed(schema, in);
         
         return this;
      }
      
      @Override
      public AvroEnumBuilder value (GenericContainer in) {
         this.value = in;
         
         return this;
      }
      
      public AvroEnumBuilder(String nm, Schema sch) {
         name = nm;
         schema = sch;
         
         itemsType = schema.getEnumItemsSchema();
         
         keys = new HashMap <>();
         values = new HashMap <>();
         
         List<String> symbols = schema.getEnumSymbols();
         for (String n : symbols) {
            // TODO Improvs: currently Enum returns ONLY JsonNode. We must implement that it returns instance corresponding with enumItemsSchema!
            // TODO Improvs: currently Enum is operable ONLY for Fixed. We must implement that it returns instance corresponding with enumItemsSchema!
            JsonNode item = schema.getEnumItem(n);
            
            if (item.isArray()) {
               int size = item.size();
               byte[] val = new byte[size];
               
               for (int i=0; i<size; i++) {
                  val[i] = (byte)item.get(0).asInt();
               }
               
               Fixed rec = new Fixed(itemsType, val);
               
               keys.put(rec, n);
               values.put(n, rec);
            }         
         }
      }
   }

}
