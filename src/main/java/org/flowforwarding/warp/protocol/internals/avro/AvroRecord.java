/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.internals.avro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericData.*;
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
import org.flowforwarding.warp.protocol.internals.IProtocolAtom;
import org.flowforwarding.warp.protocol.internals.IProtocolBuilder;
import org.flowforwarding.warp.protocol.internals.IProtocolItem;
import org.flowforwarding.warp.protocol.internals.IProtocolStructure;

/**
 * @author Infoblox Inc.
 *
 */
public class AvroRecord implements IProtocolStructure <String, GenericContainer>{
   
   protected String name;
   protected Schema schema;
   protected GenericRecord recordValue;
   protected List<IProtocolItem<String, GenericContainer>> items = new ArrayList<>();
   
   private AvroRecord (AvroRecordBuilder builder) {
      name = builder.name;
      schema = builder.schema;
      
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
   public GenericContainer get(String name) {
      if (recordValue != null) {
         return (GenericContainer) recordValue.get(name);
      } else
         return null;
   }
   
   @Override
   public GenericContainer get() {
      
      GenericRecordBuilder builder = new GenericRecordBuilder(schema);
      for (IProtocolItem<String, GenericContainer> item : items) {
         GenericContainer val = item.get();
         // TODO Improv. I dislike this NULL verification
         if (val != null)
            builder.set(item.getName(), val);
      }
      
      GenericRecord record = builder.build();
      return record;
   }

   @Override
   public void add(IProtocolItem<String, GenericContainer> item) {
      // TODO Improvs: Check name against Avro Schema
      items.add(item);
   }
   
   public String getName() {
      return name;
   }

   public Schema getSchema() {
      return schema;
   }
   
   public GenericRecord getRecord() {
      return recordValue;
   }
   
   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.internals.IProtocolStructure#encode()
    */
   @Override
   public byte[] encode() {
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
   
   public static class AvroRecordBuilder extends AvroItemBuilder {
      protected final String name;
      protected final Schema schema;
      protected Map<String, IProtocolBuilder<String, GenericContainer>> builders = new HashMap<>();
      protected GenericRecord recordValue;
      protected byte[] binValue;
      
      public AvroRecordBuilder (String nm, Schema sch) {
         name = nm;
         schema = sch;
      }
      
      @Override
      public AvroRecordBuilder Value (byte[] in) {
         binValue = in;
         return this;
      }
      
      @Override
      public IProtocolItem<String, GenericContainer> build() {
         AvroRecord rec = new AvroRecord(this);
         for (String nm: builders.keySet()) {
            rec.add(builders.get(nm).build());
         }
         return rec;
      }
      
      public String getName() {
         return name;
      }
      
      public void addItemBuilder (String nm, IProtocolBuilder<String, GenericContainer> builder) {
         builders.put(nm, builder);
      }
      
      public Schema getSchema() {
         return schema;
      }
      
   }
}
