package org.flowforwarding.of.controller.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericEnumSymbol;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.DatumWriter;
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
  public static void main(String[] args) {
     
     Protocol protocol = null;

     Schema ofpHelloHeaderSchema = null;
     Schema ofpHelloSchema = null;
     
     GenericRecord ofpHelloHeaderRecord;
     GenericRecord ofpHelloRecord;

     
     try {
        //protocol = org.apache.avro.Protocol.parse(new File("src/resources/test_orig.avpr"));
        protocol = org.apache.avro.Protocol.parse(new File("src/resources/test.avpr"));
     } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
     }
     
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

     return;
  }
}

