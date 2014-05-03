/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.internals.avro;

import java.io.IOException;

import org.flowforwarding.warp.protocol.internals.IProtocolAtom;
import org.flowforwarding.warp.protocol.internals.IProtocolItem;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Fixed;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;

/**
 * @author Infoblox Inc.
 *
 */
public class AvroFixedField implements IProtocolAtom <String, GenericContainer>{
   
   protected String name;

   protected int size;
   protected byte[] binValue = null;
   protected Fixed fixedValue = null;
   protected Schema schema;

   private AvroFixedField (AvroFixedBuilder builder) {
      name = builder.name;
      schema = builder.schema;
  // TODO Improv: catch possible exception. It will be thrown in case Schema is NOT Fixed
      size = schema.getFixedSize();
      
      if (builder.binValue != null) {
         fixedValue = new GenericData.Fixed(schema);
         GenericDatumReader<Fixed> reader = new GenericDatumReader<>(schema);
         Decoder decoder = DecoderFactory.get().binaryDecoder(builder.binValue, null);
         
         try {
            reader.read(fixedValue, decoder);
         } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
      }
   }

   
   @Override
   public GenericContainer get() {
      // TODO Improve: catch possible exception. It will be thrown in case inconsistence between value and schema
      if (binValue != null)
         return new GenericData.Fixed(schema, binValue);
      else return null;
   }

   @Override
   public void set(String value) {
      // TODO Auto-generated method stub
      return;
   }
   
   public int getSize() {
      return size;
   }

   public String getName() {
      return name;
   }

   // TODO Improvs: Do we really need this getter?
   public byte[] getValue() {
      return binValue;
   }

   public Schema getSchema() {
      return schema;
   }
   
   public static class AvroFixedBuilder extends AvroItemBuilder {
      protected final String name;
      protected final Schema schema;
      protected byte [] binValue = null;
      
      @Override
      public IProtocolItem<String, GenericContainer> build() {
         return new AvroFixedField(this);
      }
      
      public AvroFixedBuilder(String nm, Schema sch) {
         name = nm;
         schema = sch;
      }
      
      @Override
      public AvroFixedBuilder Value (byte [] in) {
         this.binValue = in;
         return this;
      }

      public String getName() {
         return name;
      }
   }
   
   
}
