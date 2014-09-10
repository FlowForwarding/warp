/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.container.avro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.generic.GenericData.Fixed;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.flowforwarding.warp.protocol.container.IBinary;
import org.flowforwarding.warp.protocol.container.IBuilder;
import org.flowforwarding.warp.protocol.container.IBuilt;
import org.flowforwarding.warp.protocol.container.INamedValue;
import org.flowforwarding.warp.protocol.container.IStructure;

/**
 * @author Infoblox Inc.
 *
 */
public class AvroRecord implements IBinary<String, GenericContainer>,
                                   INamedValue<String, GenericContainer>,
                                   IStructure<String, GenericContainer>,
                                   IBuilt<String, GenericContainer>{
   
   protected String name;
   protected Schema schema;
   protected GenericRecord recordValue;
   protected Map<String, AvroItem> fields = new HashMap<>();
   protected boolean readyToBinary = true;
   
   private AvroRecord (AvroRecordBuilder builder) {
      name = builder.name;
      schema = builder.schema;
      readyToBinary = builder.readyToBinary;
      
      if (builder.binValue != null) {
         recordValue = new GenericData.Record(schema);
         GenericDatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
         Decoder decoder = DecoderFactory.get().binaryDecoder(builder.binValue, null);
         
         try {
            reader.read(recordValue, decoder);
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }      
   }
   
   @Override
   public GenericContainer get() {
      if (readyToBinary) {
         GenericRecordBuilder builder = new GenericRecordBuilder(schema);
         Set<String> keys = fields.keySet();
         for (String key : keys) {
            INamedValue<String, GenericContainer> item = fields.get(key);
            GenericContainer val = item.get();
            // TODO Improv. I dislike this NULL verification
            if (val != null)
               builder.set(item.name(), val);
         }
         
         return builder.build();
      } else {
         GenericRecord record = new GenericData.Record (schema);

         Set<String> keys = fields.keySet();
         for (String key : keys) {
            INamedValue<String, GenericContainer> item = fields.get(key);
            GenericContainer val = item.get();
            // TODO Improv. I dislike this NULL verification
            if (val != null)
               record.put(key, val);
         }         
         return record;
      }
   }

   @Override
   public void set(String name, GenericContainer value) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void set(String name, byte[] value) {
      INamedValue<String, GenericContainer> item = fields.get(name);
      
      // TODO Improv: What about non-fixed?
      if (item instanceof AvroFixed) 
         ((AvroFixed) item).set(value); 
      return;
   }

   @Override
   public String name() {
      return name;
   }

   @Override
   public byte[] binary() {
      GenericRecord record = (GenericRecord) get();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      
      DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(this.schema);
      Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
      
      try {
         writer.write(record, encoder);
         encoder.flush();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      return out.toByteArray();   
   }
   
   @Override
   public byte[] binary(String name) {
      if (recordValue != null) {
         //TODO Improvs: excepion should be thrown in case of wrong field name
         return ((Fixed)recordValue.get(name)).bytes();
      }
      return null;
   }
   
   private void add(String name, AvroItem i) {
      fields.put(name, i);
   }
   
   public static class AvroRecordBuilder implements IBuilder<String, GenericContainer> {
      protected final String name;
      protected final Schema schema;
      protected Map<String, IBuilder<String, GenericContainer>> builders = new HashMap<>();
      protected GenericRecord value;
      protected byte[] binValue;
      protected boolean readyToBinary = true;
      
      public AvroRecordBuilder (String nm, Schema sch) {
         name = nm;
         schema = sch;
      }
      
      @Override
      public AvroRecordBuilder value (byte[] in) {
         binValue = in;
         return this;
      }
      
      @Override
      public IBuilder<String, GenericContainer> value(GenericContainer in) {
         value = (GenericRecord) in;
         return this;
      }
      
      @Override
      public AvroRecord build() {
         AvroRecord rec = new AvroRecord(this);
         for (String nm: builders.keySet()) {
            rec.add(nm, new AvroItem(builders.get(nm).build()));
         }
         return rec;
      }
      
      public String getName() {
         return name;
      }
      
      public void addItemBuilder (String nm, IBuilder<String, GenericContainer> builder) {
         builders.put(nm, builder);
      }
      
      public void notReadyToBinary () {
         readyToBinary = false;
      }
      
      public boolean isReadyToBuild() {return readyToBinary;}
   }
   
   @Override
   public AvroItem field(String name) {
      return fields.get(name);
   }
}
