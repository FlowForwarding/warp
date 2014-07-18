/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.container.avro;

import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericData.Fixed;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.flowforwarding.warp.protocol.container.IAtom;
import org.flowforwarding.warp.protocol.container.IBinary;
import org.flowforwarding.warp.protocol.container.IBuilder;
import org.flowforwarding.warp.protocol.container.IBuilt;
import org.flowforwarding.warp.protocol.container.INamedValue;

/**
 * @author Infoblox Inc.
 *
 */
public class AvroFixed implements IAtom<String, GenericContainer>, 
                                  INamedValue<String, GenericContainer>, 
                                  IBuilt<String, GenericContainer>{
   
   protected String name;
   protected int size;
   protected byte[] binValue = null;
   protected Fixed fixedValue = null;
   protected Schema schema;

   private AvroFixed (AvroFixedBuilder builder) {
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
   public void set(byte [] value) {
      // TODO Improvs: Exception in case of size inconsistence
      this.binValue = value;
   }
   
   @Override
   public void set(GenericContainer value) {
      // TODO Improvs: Exception in case of size inconsistence
      this.fixedValue = (Fixed) value;
   }
   
   //TODO Improvs: should we declare it in IProtocolAtom 
   public int size() {
      return size;
   }

   @Override
   public String name() {
      return name;
   }

   // TODO Improvs: Do we really need this getter?
   public byte[] getValue() {
      return binValue;
   }

   public Schema getSchema() {
      return schema;
   }
   
   public static class AvroFixedBuilder implements IBuilder <String, GenericContainer>{
      protected final String name;
      protected final Schema schema;
      protected byte [] binValue = null;
      protected GenericContainer value = null;
      
      @Override
      public IBuilt<String, GenericContainer> build() {
         return new AvroFixed(this);
      }
      
      public AvroFixedBuilder(String nm, Schema sch) {
         name = nm;
         schema = sch;
      }
      
      @Override
      public AvroFixedBuilder value (byte [] in) {
         this.binValue = in;
         return this;
      }
      @Override
      public AvroFixedBuilder value(GenericContainer in) {
         this.value = in;
         return this;
      }
   }
}
