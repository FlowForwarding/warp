package org.flowforwarding.of.protocol.ofmessages;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModRef;

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
    * 
    */
   byte [] encodeEchoRequest();
   
   /*
    * 
    */
   public byte[] encodeFlowMod (OFMessageFlowModRef fmRef);
   
   /**
    * 
    */
   public OFMessageFlowModRef buildFlowModMsg ();
}
