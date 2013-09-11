/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofmessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessageProvider10AvroProtocol {
   
   private final String schemaSrc = "of_protocol_10.avpr";
   
   private Schema helloHeaderSchema = null;
   private Schema ofpHelloSchema = null;
   
   private Schema echoRequestHeader = null;
   private Schema echoReplyHeader = null;
   private Schema ofpEchoRequest = null;
   private Schema ofpEchoReply = null;
   
   
   
   private Protocol protocol = null;
   protected IOFMessageBuilder builder;
   
   public void init () {
      try {
         //protocol = org.apache.avro.Protocol.parse(new File(schemaSrc));
         protocol = org.apache.avro.Protocol.parse(getClass().getClassLoader().getResourceAsStream(schemaSrc));
         builder = new OFMessageBuilder13();
         
         } catch (IOException e) {
         // TODO Auto-generated catch block
           e.printStackTrace();
         }

      helloHeaderSchema = protocol.getType("of.hello_header");
      ofpHelloSchema = protocol.getType("of.ofp_hello");
      
      echoRequestHeader = protocol.getType("echo_request_header");
      echoReplyHeader = protocol.getType("echo_reply_header");
      ofpEchoRequest = protocol.getType("ofp_echo_request");
      ofpEchoReply = protocol.getType("ofp_echo_reply");
      
   }

   private byte[] encodeMessage (Schema headerSchema, Schema bodySchema) {
      
      GenericRecord bodyRecord = new GenericData.Record(bodySchema);
      GenericRecordBuilder builder = new GenericRecordBuilder(headerSchema);

      GenericRecord headerRecord = builder.build();
      bodyRecord.put("header", headerRecord);  
      
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      
      DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(ofpHelloSchema);
      Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
      
      try {
         writer.write(bodyRecord, encoder);
         encoder.flush();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      return out.toByteArray();
   }
   
   /**
    * @return
    */
   public byte[] encodeHelloMessage() {
      return encodeMessage(helloHeaderSchema, ofpHelloSchema);
   }
   
   /**
    * @return
    */
   public byte[] encodeEchoRequest() {
      return encodeMessage(echoRequestHeader, ofpEchoRequest);
   }
   
   /**
    * @return
    */
   public byte[] encodeEchoReply() {
      return encodeMessage(echoReplyHeader, ofpEchoReply);
   }
   
}
