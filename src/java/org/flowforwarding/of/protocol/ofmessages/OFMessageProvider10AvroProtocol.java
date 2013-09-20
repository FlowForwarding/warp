/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofmessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

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
import org.flowforwarding.of.protocol.ofmessages.OFMessageError.OFMessageErrorHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigHandler;
import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction.OFStructureInstructionHandler;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessageProvider10AvroProtocol implements IOFMessageProvider{
   
   private final String schemaSrc = "of_protocol_10.avpr";
   
   private Schema helloHeaderSchema = null;
   private Schema ofpHelloSchema = null;

   private Schema switchFeaturesRequestHeaderSchema = null;
   private Schema ofpSwitchFeaturesRequestSchema = null;
   private Schema switchFeaturesHeaderSchema = null;   
   private Schema ofpSwitchFeaturesSchema = null;
   
   private Schema getConfigRequestHeaderSchema = null;
   private Schema ofpGetConfigRequestSchema = null;
   private Schema switchConfigHeaderSchema = null;
   private Schema ofpSwitchConfigSchema = null;
   
   private Schema echoRequestHeaderSchema = null;
   private Schema echoReplyHeaderSchema = null;
   private Schema ofpEchoRequestSchema = null;
   private Schema ofpEchoReplySchema = null;
   
   private Schema packetInHeaderSchema = null;
   private Schema ofpPacketInSchema = null;
   
   
   private Protocol protocol = null;
   protected IOFMessageBuilder builder;
   
   public void init () {
      try {
         //protocol = org.apache.avro.Protocol.parse(new File(schemaSrc));
         protocol = org.apache.avro.Protocol.parse(getClass().getClassLoader().getResourceAsStream(schemaSrc));
         builder = new OFMessageBuilder10();
         
      } catch (IOException e) {
         // TODO Auto-generated catch block
           e.printStackTrace();
      }

      helloHeaderSchema = protocol.getType("of10.hello_header");
      ofpHelloSchema = protocol.getType("of10.ofp_hello");
      
      switchFeaturesRequestHeaderSchema = protocol.getType("of10.features_request_header");
      ofpSwitchFeaturesRequestSchema = protocol.getType("of10.ofp_features_request");
      switchFeaturesHeaderSchema = protocol.getType("of10.switch_features_header");
      ofpSwitchFeaturesSchema = protocol.getType("of10.ofp_switch_features");
      
      getConfigRequestHeaderSchema = protocol.getType("of10.get_config_request_header");
      ofpGetConfigRequestSchema = protocol.getType("of10.ofp_get_config_request");
      switchConfigHeaderSchema = protocol.getType("of10.switch_config_header");
      ofpSwitchConfigSchema = protocol.getType("of10.ofp_switch_config");
      
      echoRequestHeaderSchema = protocol.getType("of10.echo_request_header");
      ofpEchoRequestSchema = protocol.getType("of10.ofp_echo_request");
      echoReplyHeaderSchema = protocol.getType("of10.echo_reply_header");
      ofpEchoReplySchema = protocol.getType("of10.ofp_echo_reply");
      
      packetInHeaderSchema = protocol.getType("of10.packet_in_header");
      ofpPacketInSchema = protocol.getType("of10.ofp_packet_in");
   }

   public byte[] encodeMessage (Schema headerSchema, Schema bodySchema) {
      
      GenericRecord bodyRecord = new GenericData.Record(bodySchema);
      GenericRecordBuilder builder = new GenericRecordBuilder(headerSchema);

      GenericRecord headerRecord = builder.build();
      bodyRecord.put("header", headerRecord);  
      
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      
      DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(bodySchema);
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
   
   protected GenericRecord getRecord (Schema schema, byte[] buffer) {
      // TODO Improvs: make a protected getter to get general records.
      try {
         GenericRecord record = new GenericData.Record(schema);
         GenericDatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);
         Decoder decoder = DecoderFactory.get().binaryDecoder(buffer, null);
         
         reader.read(record, decoder);
         
         return record;
    } catch (IOException e) {
       // TODO Auto-generated catch block
       e.printStackTrace();
       
       return null;
    }
   }
   
   protected GenericRecord getSwitchFeaturesRecord (byte[] buffer) {
      
      try {
         GenericRecord featureReplyRecord = new GenericData.Record(ofpSwitchFeaturesSchema);
         GenericDatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(ofpSwitchFeaturesSchema);
         Decoder decoder = DecoderFactory.get().binaryDecoder(buffer, null);
         
         reader.read(featureReplyRecord, decoder);
         
         return featureReplyRecord;
    } catch (IOException e) {
       // TODO Auto-generated catch block
       e.printStackTrace();
       
       return null;
    }
   }
   
   private static Byte getByte(GenericData.Fixed in) {
      return new Byte(in.bytes()[0]);
   }
   
   private static Long getLong(GenericData.Fixed in) {
      long result = 0;
      byte [] buffer = in.bytes();
      
      result |=  ((long)(buffer[7])  & 255);
      result |=  (((long)(buffer[6])  & 255) << 8);
      result |=  (((long)(buffer[5])  & 255) << 16);
      result |=  (((long)(buffer[4])  & 255) << 24);
      result |=  (((long)(buffer[3])  & 255) << 32);
      result |=  (((long)(buffer[2])  & 255) << 40);
      result |=  (((long)(buffer[1])  & 255) << 48);
      result |=  (((long)(buffer[0])  & 255) << 56);
 
      return new Long(result);
   }
   
   private static Short getShort(GenericData.Fixed in) {
      short result = 0;
      byte [] buffer = in.bytes();
      
      result |=  ((long)(buffer[1])  & 255);
      result |=  (((long)(buffer[0])  & 255) << 8);
      
      return new Short(result);
   }

   
   /**
    * @return
    */
   @Override
   public byte[] encodeHelloMessage() {
      return encodeMessage(helloHeaderSchema, ofpHelloSchema);
   }
   
   /**
    * @return
    */
   @Override
   public byte[] encodeSwitchFeaturesRequest() {
      return encodeMessage (switchFeaturesRequestHeaderSchema, ofpSwitchFeaturesRequestSchema);
   }
   
   /**
    * @return
    */
   public byte[] encodeEchoRequest() {
      return encodeMessage(echoRequestHeaderSchema, ofpEchoRequestSchema);
   }
   
   /**
    * @return
    */
   public byte[] encodeEchoReply() {
      return encodeMessage(echoReplyHeaderSchema, ofpEchoReplySchema);
   }
   

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#getVersion()
    */
   @Override
   public Short getVersion() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#getHello(java.io.ByteArrayOutputStream)
    */
   @Override
   public ByteArrayOutputStream getHello(
         ByteArrayOutputStream byteArrayOutputStream) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#getSwitchFeaturesRequest(java.io.ByteArrayOutputStream)
    */
   @Override
   public ByteArrayOutputStream getSwitchFeaturesRequest(
         ByteArrayOutputStream byteArrayOutputStream) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#getSetSwitchConfig(java.io.ByteArrayOutputStream)
    */
   @Override
   public ByteArrayOutputStream getSetSwitchConfig(
         ByteArrayOutputStream byteArrayOutputStream) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#getSwitchConfigRequest(java.io.ByteArrayOutputStream)
    */
   @Override
   public ByteArrayOutputStream getSwitchConfigRequest(
         ByteArrayOutputStream byteArrayOutputStream) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#getFlowMod(java.util.Map, java.io.ByteArrayOutputStream)
    */
   @Override
   public ByteArrayOutputStream getFlowMod(Map<String, Object> entries,
         ByteArrayOutputStream byteArrayOutputStream) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#encodeSwitchConfigRequest()
    */
   @Override
   public byte[] encodeSwitchConfigRequest() {
      return encodeMessage(getConfigRequestHeaderSchema, ofpGetConfigRequestSchema);
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#getDPID(byte[])
    */
   public Long getDPID(byte[] buffer) {
      
      GenericRecord featureReplyRecord = getSwitchFeaturesRecord (buffer);
      
      GenericData.Fixed dpid = (GenericData.Fixed) featureReplyRecord.get("datapath_id");
      return getLong(dpid);
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#isHello(byte[])
    */
   @Override
   public boolean isHello(byte[] in) {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#encodeFlowMod(org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModHandler)
    */
   @Override
   public byte[] encodeFlowMod(OFMessageFlowModHandler fmHandler) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#buildFlowModMsg()
    */
   @Override
   public OFMessageFlowModHandler buildFlowModMsg() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#buildInstructionApplyActions()
    */
   @Override
   public OFStructureInstructionHandler buildInstructionApplyActions() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#buildInstructionWriteActions()
    */
   @Override
   public OFStructureInstructionHandler buildInstructionWriteActions() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#buildInstructionGotoTable()
    */
   @Override
   public OFStructureInstructionHandler buildInstructionGotoTable() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#buildInstructionClearActions()
    */
   @Override
   public OFStructureInstructionHandler buildInstructionClearActions() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#buildInstructionMeter()
    */
   @Override
   public OFStructureInstructionHandler buildInstructionMeter() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#buildInstructionWriteMetadata()
    */
   @Override
   public OFStructureInstructionHandler buildInstructionWriteMetadata() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#isConfig(byte[])
    */
   @Override
   public boolean isConfig(byte[] in) {
      GenericRecord header = getRecord(switchConfigHeaderSchema, in);
      // TODO Improvs: We plan to get all types from Avro protocol type... soon... so let it be now just 8
      Byte type = getByte((GenericData.Fixed)header.get("type")); 
      if (type.byteValue() == 8 )  // OFPT_GET_CONFIG_REPLY
         return true;
      else 
         return false;
   }
   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#parseSwitchConfig(byte[])
    */
   @Override
   public OFMessageSwitchConfigHandler parseSwitchConfig(byte[] in) {
      GenericRecord record = getRecord(ofpSwitchConfigSchema, in);
      
   // TODO Improvs: We plan to get all flags from Avro protocol type... soon... so let it be now just numbers
      short flags = getShort((GenericData.Fixed)record.get("flags"));
      OFMessageSwitchConfigHandler configH = builder.buildSwitchConfig();
      if (flags == 0) {
         configH.setConfigFlagFragNormal();
      } else {
         if ((flags & 1) != 0) configH.setConfigFlagFragDrop();
         if ((flags & 2) != 0) configH.setConfigFlagFragReasm();
         if ((flags & 3) != 0) configH.setConfigFlagFragMask();
      }
      
      return configH;
   }
   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#isPacketIn(byte[])
    */
   @Override
   public boolean isPacketIn(byte[] in) {
      GenericRecord header = getRecord(packetInHeaderSchema, in);

      // TODO Improvs: We plan to get all types from Avro protocol type... soon... so let it be now just 10
      Byte type = getByte((GenericData.Fixed)header.get("type")); 
      if (type.byteValue() == 10 )  // OFPT_PACKET_IN
         return true;
      else 
         return false;   
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#parsePacketIn(byte[])
    */
   @Override
   public OFMessagePacketInHandler parsePacketIn(byte[] in) {
      GenericRecord packetIn = getRecord(ofpPacketInSchema, in);
      
      return null;      
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#isError(byte[])
    */
   @Override
   public boolean isError(byte[] in) {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#parseError(byte[])
    */
   @Override
   public OFMessageErrorHandler parseError(byte[] in) {
      // TODO Auto-generated method stub
      return null;
   }
   
   @Override
   public boolean isMessage (Schema headerSchema, byte[] in) {
      GenericRecord header = getRecord(headerSchema, in);

      // TODO Improvs: We plan to get all types from Avro protocol type... soon... so let it be now just 1
      Byte type = getByte((GenericData.Fixed)header.get("type")); 
      if (type.byteValue() == 1 )  // ERROR
         return true;
      else 
         return false;
   }
   
   @Override
   public boolean isSwitchFeatures(byte[] in) {
      GenericRecord header = getRecord(switchFeaturesHeaderSchema, in);
      
      // TODO Improvs: We plan to get all types from Avro protocol type... soon... so let it be now just 6
      Byte type = getByte((GenericData.Fixed)header.get("type"));
      if (type.byteValue() == 6 )  // FEATURES_REPLY
         return true;
      else
         return false;
   }

   @Override
   public boolean isEchoRequest(byte[] in) {
      GenericRecord header = getRecord(echoRequestHeaderSchema, in);
      
      // TODO Improvs: We plan to get all types from Avro protocol type... soon... so let it be now just 6
      Byte type = getByte((GenericData.Fixed)header.get("type"));
      if (type.byteValue() == 2 )  // ECHO_REQUEST
         return true;
      else
         return false;
   }
   
   
}
