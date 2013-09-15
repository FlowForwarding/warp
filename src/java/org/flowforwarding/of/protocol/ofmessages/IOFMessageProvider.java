package org.flowforwarding.of.protocol.ofmessages;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.flowforwarding.of.protocol.ofmessages.OFMessageError.OFMessageErrorHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigHandler;
import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction.OFStructureInstructionHandler;

public interface IOFMessageProvider {

   Short getVersion ();
   
   /**
    * @param byteArrayOutputStream
    * @return
    */
   ByteArrayOutputStream getHello(ByteArrayOutputStream byteArrayOutputStream);

   /**
    * @param byteArrayOutputStream
    * @return
    */
   ByteArrayOutputStream getSwitchFeaturesRequest(ByteArrayOutputStream byteArrayOutputStream);

   /**
    * @param byteArrayOutputStream
    * @return
    */
   ByteArrayOutputStream getSetSwitchConfig(ByteArrayOutputStream byteArrayOutputStream);

   /**
    * @param byteArrayOutputStream
    * @return
    */
   ByteArrayOutputStream getSwitchConfigRequest(ByteArrayOutputStream byteArrayOutputStream);

   /**
    * @param entries
    * @param byteArrayOutputStream
    * @return
    */
   ByteArrayOutputStream getFlowMod(Map<String, Object> entries,
         ByteArrayOutputStream byteArrayOutputStream);

   /**
    * 
    */
   void init();

   /**
    * @return
    */
   byte[] encodeHelloMessage();

   /**
    * @return
    */
   byte[] encodeSwitchConfigRequest();

   /**
    * @return
    */
   byte[] encodeSwitchFeaturesRequest();

   /**
    * @param array
    * @return
    */
   Long getDPID(byte[] array);
   
   /**
    * @param in
    * @return
    */
   boolean isHello(byte [] in);
   
   /**
    * @param in
    * @return
    */
   boolean isFeautureReply (byte [] in);
   
   /**
    * @param in
    * @return
    */
   boolean isConfig (byte [] in);
   
   /**
    * @param in
    * @return
    */
   boolean isPacketIn (byte [] in);
   
   /**
    * @param in
    * @return
    */
   boolean isError (byte [] in);

   /**
    * 
    */
   byte [] encodeEchoRequest();
   
   /**
    * 
    */
   public byte[] encodeFlowMod (OFMessageFlowModHandler fmHandler);
   
   /**
    * 
    */
   public OFMessageSwitchConfigHandler parseSwitchConfig (byte[] in);
   
   /**
    * 
    */
   public OFMessagePacketInHandler parsePacketIn (byte[] in);
   
   /**
    * 
    */
   public OFMessageFlowModHandler buildFlowModMsg ();
   
   public OFStructureInstructionHandler buildInstructionApplyActions ();
   
   public OFStructureInstructionHandler buildInstructionWriteActions ();
   
   public OFStructureInstructionHandler buildInstructionGotoTable ();
   
   public OFStructureInstructionHandler buildInstructionClearActions ();
   
   public OFStructureInstructionHandler buildInstructionMeter ();
   
   public OFStructureInstructionHandler buildInstructionWriteMetadata ();

   /**
    * @param in
    * @return
    */
   OFMessageErrorHandler parseError(byte[] in);
}
