/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.ofmessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.flowforwarding.warp.protocol.ofitems.IOFItemBuilder;
import org.flowforwarding.warp.protocol.ofitems.OFItemEnumBuilder;
import org.flowforwarding.warp.protocol.ofitems.OFItemFixedBuilder;
import org.flowforwarding.warp.protocol.ofitems.OFItemRecordBuilder;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageError.OFMessageErrorRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageGroupMod.OFMessageGroupModRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageHello.OFMessageHelloRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigRef;
import org.flowforwarding.warp.protocol.ofstructures.OFStructureInstruction.OFStructureInstructionRef;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessageProviderAvroProtocol implements IOFMessageProvider {
   
   private final String schemaSrc = "of_protocol_13.avpr";
   private Protocol protocol = null;
   
   protected Map<String, IOFItemBuilder> builders = new HashMap<>();
   
   public void init () {
      InputStream str = Thread.currentThread().getContextClassLoader().getResourceAsStream(schemaSrc);
      try {
         protocol = Protocol.parse(str);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
      Collection<Schema> types = protocol.getTypes();
      
      for (Schema schema : types) {
         if (schema.getType().getName().equalsIgnoreCase("fixed")) {
            builders.put(schema.getName(), new OFItemFixedBuilder(schema.getName(), schema));
         } else if (schema.getType().getName().equalsIgnoreCase("record")) {
            builders.put(schema.getName(), makeRecordBuilder(schema.getName(), schema));
         } else if (schema.getType().getName().equalsIgnoreCase("enum")) {
            builders.put(schema.getName(), new OFItemEnumBuilder(schema.getName(), schema));
         }
      }
      
      return;
   }
   
   protected static OFItemRecordBuilder makeRecordBuilder (String name, Schema schema) {
      
      OFItemRecordBuilder b = new OFItemRecordBuilder(name, schema);
      ArrayList<Field> fields = (ArrayList<Field>) schema.getFields();
      for (Field field : fields) {
         if (field.schema().getType().getName().equalsIgnoreCase("fixed")) {
            b.addItemBuilder(field.name(), new OFItemFixedBuilder(field.name(), field.schema()));
         } else if (field.schema().getType().getName().equalsIgnoreCase("record")) {
            b.addItemBuilder(field.name(), makeRecordBuilder(field.name(), field.schema()));
         }
      }
      
      return b;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#getVersion()
    */
   @Override
   public Short getVersion() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#getHello(java.io.ByteArrayOutputStream)
    */
   @Override
   public ByteArrayOutputStream getHello(
         ByteArrayOutputStream byteArrayOutputStream) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#getSwitchFeaturesRequest(java.io.ByteArrayOutputStream)
    */
   @Override
   public ByteArrayOutputStream getSwitchFeaturesRequest(
         ByteArrayOutputStream byteArrayOutputStream) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#getSetSwitchConfig(java.io.ByteArrayOutputStream)
    */
   @Override
   public ByteArrayOutputStream getSetSwitchConfig(
         ByteArrayOutputStream byteArrayOutputStream) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#getSwitchConfigRequest(java.io.ByteArrayOutputStream)
    */
   @Override
   public ByteArrayOutputStream getSwitchConfigRequest(
         ByteArrayOutputStream byteArrayOutputStream) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#getFlowMod(java.util.Map, java.io.ByteArrayOutputStream)
    */
   @Override
   public ByteArrayOutputStream getFlowMod(Map<String, Object> entries,
         ByteArrayOutputStream byteArrayOutputStream) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#encodeHelloMessage()
    */
   @Override
   public byte[] encodeHelloMessage() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#encodeSwitchFeaturesRequest()
    */
   @Override
   public byte[] encodeSwitchFeaturesRequest() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#encodeSwitchConfigRequest()
    */
   @Override
   public byte[] encodeSwitchConfigRequest() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#getDPID(byte[])
    */
   @Override
   public Long getDPID(byte[] array) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#isMessage(org.apache.avro.Schema, byte[])
    */
   @Override
   public boolean isMessage(Schema header, byte[] in) {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#isHello(byte[])
    */
   @Override
   public boolean isHello(byte[] in) {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#isConfig(byte[])
    */
   @Override
   public boolean isConfig(byte[] in) {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#isPacketIn(byte[])
    */
   @Override
   public boolean isPacketIn(byte[] in) {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#isError(byte[])
    */
   @Override
   public boolean isError(byte[] in) {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#isEchoRequest(byte[])
    */
   @Override
   public boolean isEchoRequest(byte[] in) {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#encodeEchoRequest()
    */
   @Override
   public byte[] encodeEchoRequest() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#encodeEchoReply()
    */
   @Override
   public byte[] encodeEchoReply() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#encodeFlowMod(org.flowforwarding.warp.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModRef)
    */
   @Override
   public byte[] encodeFlowMod(OFMessageFlowModRef fmRef) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#encodeGroupMod(org.flowforwarding.warp.protocol.ofmessages.OFMessageGroupMod.OFMessageGroupModRef)
    */
   @Override
   public byte[] encodeGroupMod(OFMessageGroupModRef fmRef) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#parseSwitchConfig(byte[])
    */
   @Override
   public OFMessageSwitchConfigRef parseSwitchConfig(byte[] in) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#parsePacketIn(byte[])
    */
   @Override
   public OFMessagePacketInRef parsePacketIn(byte[] in) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#buildFlowModMsg()
    */
   @Override
   public OFMessageFlowModRef buildFlowModMsg() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#buildInstructionApplyActions()
    */
   @Override
   public OFStructureInstructionRef buildInstructionApplyActions() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#buildInstructionWriteActions()
    */
   @Override
   public OFStructureInstructionRef buildInstructionWriteActions() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#buildInstructionGotoTable()
    */
   @Override
   public OFStructureInstructionRef buildInstructionGotoTable() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#buildInstructionClearActions()
    */
   @Override
   public OFStructureInstructionRef buildInstructionClearActions() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#buildInstructionMeter()
    */
   @Override
   public OFStructureInstructionRef buildInstructionMeter() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#buildInstructionWriteMetadata()
    */
   @Override
   public OFStructureInstructionRef buildInstructionWriteMetadata() {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#parseError(byte[])
    */
   @Override
   public OFMessageErrorRef parseError(byte[] in) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#isSwitchFeatures(byte[])
    */
   @Override
   public boolean isSwitchFeatures(byte[] in) {
      // TODO Auto-generated method stub
      return false;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#parseMessages(byte[])
    */
   @Override
   public List<OFMessageRef> parseMessages(byte[] in) {
      // TODO Auto-generated method stub
      return null;
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider#parseHelloMessage(byte[])
    */
   @Override
   public OFMessageHelloRef parseHelloMessage(byte[] in) {
      // TODO Auto-generated method stub
      return null;
   }
}
