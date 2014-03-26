package org.flowforwarding.warp.protocol.ofitems;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;

public class OFMessage implements IOFMessage{
   
   protected enum OFType {
      OFPT_HELLO, 
      OFPT_ERROR, 
      OFPT_ECHO_REQUEST, 
      OFPT_ECHO_REPLY, 
      OFPT_EXPERIMENTER,
      OFPT_FEATURES_REQUEST,
      OFPT_FEATURES_REPLY,
      OFPT_GET_CONFIG_REQUEST,
      OFPT_GET_CONFIG_REPLY,
      OFPT_SET_CONFIG,
      OFPT_PACKET_IN,
      OFPT_FLOW_REMOVED,
      OFPT_PORT_STATUS,
      OFPT_PACKET_OUT,
      OFPT_FLOW_MOD,
      OFPT_GROUP_MOD,
      OFPT_PORT_MOD,
      OFPT_TABLE_MOD,
      OFPT_MULTIPART_REQUEST,
      OFPT_MULTIPART_REPLY,
      OFPT_BARIER_REQUEST,
      OFPT_BARIER_REPLY,
      OFPT_QUEUE_GET_CONFIG_REQUEST,
      OFPT_QUEUE_GET_CONFIG_REPLY,
      OFPT_ROLE_REQUEST,
      OFPT_ROLE_REPLY,
      OFPT_GET_ASYNC_REQUEST,
      OFPT_GET_ASYNC_REPLY,
      OFPT_SET_ASYNC,
      OFPT_METER_MOD,
      NULL; //TODO OF Changes: Type.BITMAP
      private String name;
      private OFType() { this.name = this.name().toLowerCase(); }
      public String getName() { return name; }
   };
   protected OFType type;
   protected OFItemRecord record;
   
   protected OFMessage(OFItemRecord rec) {
      record = rec;
   }
   
   public OFMessage(Map<String, IOFItemBuilder> builders, byte[] buffer) {
      
      IOFItemBuilder headerBuilder = builders.get("ofp_header");
      IOFItem headerItem = headerBuilder.build();
      GenericRecord headerRecord = (GenericRecord) headerItem.get();
      
      GenericDatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(headerItem.getSchema());
      Decoder decoder = DecoderFactory.get().binaryDecoder(buffer, null);
      
      try {
         reader.read(headerRecord, decoder);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      GenericData.Fixed version = (GenericData.Fixed)headerRecord.get("type");
      
      IOFItemBuilder versionBuilder = builders.get("ofp_type");
      IOFItem versionItem = versionBuilder.build(); 
      
/*      switch (version.bytes()[0]) {
      case 0:
         type = OFType.OFPT_HELLO;
         break;
      case 1:
         type = OFType.OFPT_ERROR;
         break;
      case 3:
         type = OFType.OFPT_ECHO_REQUEST;
         break;
      case 4:
         type = OFType.OFPT_ECHO_REPLY;
         break;
      case 6:
         type = OFType.OFPT_EXPERIMENTER;
         break;
      case 7:
         type = OFType.OFPT_FEATURES_REQUEST;
         break;
      case 8:
         type = OFType.OFPT_FEATURES_REPLY;
         break;
      case 9:
         type = OFType.OFPT_GET_CONFIG_REQUEST;
         break;
      case 10:
         type = OFType.OFPT_GET_CONFIG_REPLY;
         break;
      default:
         break;
         
      }*/
      
      /*    IOFItemBuilder helloBuilder = builders.get("ofp_hello");
      IOFItem hello = helloBuilder.build();
      GenericRecord helloRecord = (GenericRecord) hello.get();
      
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      
      DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(hello.getSchema());
      Encoder encoder = EncoderFactory.get().binaryNonEncoder(out, null);
      
      try {
         writer.write(helloRecord, encoder);
         encoder.flush();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      return out.toByteArray();
  */      
      
      // TODO Improvs: make a protected getter to get general records.
 /*     try {
         GenericRecord record = new GenericData.Record(schema);
         GenericDatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);
         Decoder decoder = DecoderFactory.get().binaryDecoder(buffer, null);
         
         reader.read(record, decoder);
         
         return record;
    } catch (IOException e) {
       // TODO Auto-generated catch block
       e.printStackTrace();
       
       return null;
    }*/
      
      return;
   }
   
   public class OFMessageRef implements IOFMessageRef{
      protected OFMessage message = null;
      
   }
}
