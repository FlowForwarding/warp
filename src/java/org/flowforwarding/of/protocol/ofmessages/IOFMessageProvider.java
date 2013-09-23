package org.flowforwarding.of.protocol.ofmessages;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.apache.avro.Schema;
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
    * Initializes Message provider
    */
   void init();

   /**
    * Encodes a OpenFlow protocol Hello message
    * @return byte array containing encoded Hello message
    */
   byte[] encodeHelloMessage();
   
   /**
    * @return
    */
   byte[] encodeSwitchFeaturesRequest();

   /**
    * Encodes a OpenFlow protocol Switch Config request message
    * @return byte array containing encoded  message
    */
   byte[] encodeSwitchConfigRequest();

   /**
    * @param array
    * @return
    */
   Long getDPID(byte[] array);
   
   /**
    * 
    */
   boolean isMessage (Schema header, byte[] in);
   
   /**
    * Verify whether a binary message is an OpenFlow Hello message
    * @param in Should contain an OpenFlow message
    * @return true if this is a Hello message
    * 
    */
   boolean isHello(byte [] in);
   
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
    * @param in
    * @return
    */
   boolean isEchoRequest (byte [] in);

   /**
    * 
    */
   byte [] encodeEchoRequest();
   
   /**
    * 
    */
   byte [] encodeEchoReply();
   
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

   boolean isSwitchFeatures(byte[] in);
}
