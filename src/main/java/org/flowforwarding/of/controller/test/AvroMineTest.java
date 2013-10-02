/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.controller.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;

/*
* TODO List:
* TODO uint_8 var <- [1,2] works (var <- 1); uint_32 <- [1,2] throws NP exception. Why?  
*
*/


public class AvroMineTest {
  /**
    * @param args
    */
  protected static Protocol protocol_13 = null;
  protected static Protocol protocol_test = null;
  protected static Schema ofpHelloSchema = null;
  protected static Schema ofpHelloHeaderSchema = null;
  
   
  public static void main(String[] args) {
     
     try {
        protocol_13 = org.apache.avro.Protocol.parse(new File("src/resources/of_protocol_131.avpr"));
        protocol_test = org.apache.avro.Protocol.parse(new File("src/resources/test.avpr"));
   } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
   }
     
     byte [] buf = TestFixedEncoding(protocol_test);
  }
  
  
  protected static byte [] TestFixedEncoding (Protocol protocol) {
     
     byte [] in = {1,2,3,4};
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     try {
        
        Schema simpleUint32Schema = protocol.getType("of.simple_uint_32");
        Schema uint32Schema = protocol.getType("of.uint_32");
        
        GenericData.Fixed field = new GenericData.Fixed(uint32Schema, in);
        GenericRecord simpleUint32Record = new GenericData.Record(simpleUint32Schema);
        simpleUint32Record.put("field_uint32", field);      

        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(simpleUint32Schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        
        writer.write(simpleUint32Record, encoder);
        encoder.flush();
              
      
     } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
     }
     
     return out.toByteArray();
     
  }
  
  protected static void Test1 (Protocol protocol) {
     AvroMineTest test = new AvroMineTest(); 
//        protocol = org.apache.avro.Protocol.parse(new File("src/resources/of_protocol_131.avpr"));
     
     ofpHelloHeaderSchema = protocol.getType("of.ofp_hello_header");
     ofpHelloSchema = protocol.getType("of.ofp_hello");
     
     ByteArrayOutputStream helloOut = new ByteArrayOutputStream();
     helloOut = test.getHelloBinary(helloOut);
     
     GenericRecord helloRecord = test.getHelloRecord(helloOut);
     
     helloRecord.get(1);
       
       
       return;     
  }
  
  protected GenericRecord getHelloRecord (ByteArrayOutputStream out) {
     
     try {
        GenericRecord helloRecord = new GenericData.Record(ofpHelloSchema);
        GenericDatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(ofpHelloSchema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(), null);
        
        reader.read(helloRecord, decoder);
        
        return helloRecord;
   } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      
      return null;
   }
  }
  
  protected ByteArrayOutputStream getHelloBinary(ByteArrayOutputStream out) {
     
     GenericRecord ofpHelloRecord = new GenericData.Record(ofpHelloSchema);
     GenericRecordBuilder builder = new GenericRecordBuilder(ofpHelloHeaderSchema);

     GenericRecord ofpHelloHeaderRecord = builder.build();
     ofpHelloRecord.put("header", ofpHelloHeaderRecord);  
     
     DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(ofpHelloSchema);
     Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
     
     try {
        writer.write(ofpHelloRecord, encoder);
        encoder.flush();
     } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
     }

     return out;
   }
  
  
  
  private void x() {
     Protocol protocol = null;
     Schema recordSchema = null;

     try {
        //protocol = org.apache.avro.Protocol.parse(new File("src/resources/test_orig.avpr"));
        protocol = org.apache.avro.Protocol.parse(new File("src/resources/test.avpr"));
        
        recordSchema = protocol.getType("of12.a_record");
        GenericRecordBuilder builder = new GenericRecordBuilder(recordSchema);
        
        GenericRecord aRecord = builder.build();
        
        ByteArrayOutputStream oxmOut = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> oxmWriter = new GenericDatumWriter<GenericRecord>(recordSchema);
        Encoder oxmEncoder = EncoderFactory.get().binaryNonEncoder(oxmOut, null);
        
        oxmWriter.write(aRecord, oxmEncoder);
        oxmEncoder.flush();
        
        GenericDatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(recordSchema);
        Decoder oxmDecoder = DecoderFactory.get().binaryDecoder(oxmOut.toByteArray(), null);
        
        GenericRecord bRecord = new GenericData.Record(recordSchema);
        
        reader.read(bRecord, oxmDecoder);

/*        GenericBitmap bitmap1 = new GenericData.Bitmap(bitmap1Schema);
        
        ByteArrayOutputStream oxmOut = new ByteArrayOutputStream();
        DatumWriter<GenericBitmap> oxmWriter = new GenericDatumWriter<GenericBitmap>(bitmap1Schema);
        Encoder oxmEncoder = EncoderFactory.get().binaryNonEncoder(oxmOut, null);
        
        oxmWriter.write(bitmap1, oxmEncoder);
        oxmEncoder.flush();*/
        
        Object afield = bRecord.get("a_field");
        
        
        oxmOut.toByteArray();
        
     } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
     }
     
/*     bitmap1Schema = protocol.getType("of12.bitmap1");
     GenericRecordBuilder builder = new GenericRecordBuilder(bitmap1Schema);
     
     GenericRecord bitmap1Record = builder.build();*/
     
     
/*     ofpHelloHeaderSchema = protocol.getType("of12.ofp_hello_header");
     ofpHelloSchema = protocol.getType("of12.ofp_hello");
     
     GenericRecordBuilder builder = new GenericRecordBuilder(ofpHelloHeaderSchema);
     ofpHelloRecord = new GenericData.Record(ofpHelloSchema);
     
     ofpHelloHeaderRecord = builder.build();
     ofpHelloRecord.put("header", ofpHelloHeaderRecord);  
     
     
     ByteArrayOutputStream out = new ByteArrayOutputStream();

     DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(ofpHelloSchema);
     Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
     
     try {
        writer.write(ofpHelloRecord, encoder);
        encoder.flush();
     } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
     }*/
  }
}

