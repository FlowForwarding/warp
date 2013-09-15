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
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModHandler;
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
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#encodeSwitchFeaturesRequest()
    */
   @Override
   public byte[] encodeSwitchFeaturesRequest() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#getDPID(byte[])
    */
   @Override
   public Long getDPID(byte[] array) {
      // TODO Auto-generated method stub
      return null;
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
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#isFeautureReply(byte[])
    */
   @Override
   public boolean isFeautureReply(byte[] in) {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#isConfig(byte[])
    */
   @Override
   public boolean isConfig(byte[] in) {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#parseSwitchConfig(byte[])
    */
   @Override
   public OFMessageSwitchConfigHandler parseSwitchConfig(byte[] in) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider#isPacketIn(byte[])
    */
   @Override
   public boolean isPacketIn(byte[] in) {
      // TODO Auto-generated method stub
      return false;
   }
   
}
