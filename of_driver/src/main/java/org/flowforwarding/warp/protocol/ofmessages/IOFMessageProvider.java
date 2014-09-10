package org.flowforwarding.warp.protocol.ofmessages;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageError.OFMessageErrorRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageGroupMod.OFMessageGroupModRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageHello.OFMessageHelloRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigRef;
import org.flowforwarding.warp.protocol.ofstructures.OFStructureInstruction.OFStructureInstructionRef;

public interface IOFMessageProvider {

   Short getVersion ();
   
   /**
    * @param byteArrayOutputStream
    * @return Output stream, containg encoded OpenFlow Hello message, or null
    */
   ByteArrayOutputStream getHello(ByteArrayOutputStream byteArrayOutputStream);

   /**
    * @param byteArrayOutputStream
    * @return Output stream, containg encoded OpenFlow Switch Feature Request message, or null
    */
   ByteArrayOutputStream getSwitchFeaturesRequest(ByteArrayOutputStream byteArrayOutputStream);

   /**
    * @param byteArrayOutputStream
    * @return Output stream, containg encoded OpenFlow Set Swicth Config message, or null
    */
   ByteArrayOutputStream getSetSwitchConfig(ByteArrayOutputStream byteArrayOutputStream);

   /**
    * @param byteArrayOutputStream
    * @return Output stream, containg encoded OpenFlow Get Swicth Config message, or null
    */
   ByteArrayOutputStream getSwitchConfigRequest(ByteArrayOutputStream byteArrayOutputStream);

   /**
    * @param entries
    * @param byteArrayOutputStream
    * @return Output stream, containg encoded OpenFlow Flow Modification message, or null
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
    * @return byte array containing encoded Switc Feature Request message
    */
   byte[] encodeSwitchFeaturesRequest();

   /**
    * Encodes a OpenFlow protocol Switch Config request message
    * @return byte array containing encoded  message
    */
   byte[] encodeSwitchConfigRequest();

   /**
    * @param array
    * @return Long value of Switch DPID
    */
   Long getDPID(byte[] array);
   
   /**
    * @return <b>true</b> if byte array contains an OpenFlow message 
    */
   boolean isMessage (Schema header, byte[] in);
   
   /**
    * Verify whether a binary message is an OpenFlow Hello message
    * @param in Should contain an OpenFlow message
    * @return <b>true</b> if byte array contains an OpenFlow Hello message
    * 
    */
   boolean isHello(byte [] in);
   
   /**
    * @param in
    * @return <b>true</b> if byte array contains an OpenFlow Switch Configuration message
    */
   boolean isConfig (byte [] in);
   
   /**
    * @param in
    * @return <b>true</b> if byte array contains an OpenFlow packet-In massage
    */
   boolean isPacketIn (byte [] in);
   
   /**
    * @param in
    * @return <b>true</b> if byte array contains an OpenFlow Error message
    */
   boolean isError (byte [] in);
   
   /**
    * @param in
    * @return <b>true</b> if byte array contains an OpenFlow Echo Request message
    */
   boolean isEchoRequest (byte [] in);

   /**
    * @return a byte array contains an OpenFlow Echo Request message
    */
   byte [] encodeEchoRequest();
   
   /**
    * @return a byte array contains an OpenFlow Echo Reply message
    */
   byte [] encodeEchoReply();
   
   /**
    * @return a byte array contains an OpenFlow Flow Modification message
    */
   public byte[] encodeFlowMod (OFMessageFlowModRef fmRef);

    /**
     * @return a byte array contains an OpenFlow Group Modification message
     */
   public byte[] encodeGroupMod (OFMessageGroupModRef fmRef);
   
   /**
    * @return a Reference to parsed OpenFlow Switch Configuration message
    */
   public OFMessageSwitchConfigRef parseSwitchConfig (byte[] in);
   
   /**
    * @return a Reference to parsed OpenFlow Packet-In message 
    */
   public OFMessagePacketInRef parsePacketIn (byte[] in);
   
   /**
    * @return a Reference to inintialized Flow Modification object  
    */
   public OFMessageFlowModRef buildFlowModMsg ();
   
   public OFStructureInstructionRef buildInstructionApplyActions ();
   
   public OFStructureInstructionRef buildInstructionWriteActions ();
   
   public OFStructureInstructionRef buildInstructionGotoTable ();
   
   public OFStructureInstructionRef buildInstructionClearActions ();
   
   public OFStructureInstructionRef buildInstructionMeter ();
   
   public OFStructureInstructionRef buildInstructionWriteMetadata ();

   /**
    * @param in
    * @return A Reference to parsed Error Message object
    */
   OFMessageErrorRef parseError(byte[] in);

   boolean isSwitchFeatures(byte[] in);
   
   public List<OFMessageRef> parseMessages (byte[] in);
   public OFMessageHelloRef parseHelloMessage (byte[] in);
}
