/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */

package org.flowforwarding.of.ofswitch;

import java.io.ByteArrayOutputStream;

import org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider;
import org.flowforwarding.of.protocol.ofmessages.IOFMessageProviderFactory;
import org.flowforwarding.of.protocol.ofmessages.OFMessageProviderFactoryAvroProtocol;

public class SwitchState {
   
   protected Long dpid = null;
   protected ByteArrayOutputStream lastIncomingMessage = null;
   protected Short version = 4;
   protected IOFMessageProvider provider = null;
   protected IOFMessageProviderFactory factory = null;
   
   protected SwitchState () {
      factory = new OFMessageProviderFactoryAvroProtocol();
      provider = factory.getMessageProvider("1.3");
   }
   
   protected Long getDpid() {
      return dpid;
   }

   protected void setDpid(Long dpid) {
      this.dpid = dpid;
   }
   
   public static class SwitchHandler {
      
      protected SwitchState swState = null;
      
      protected SwitchHandler () {
         swState = new SwitchState ();
      }
      
      protected SwitchHandler (SwitchState sws) {
         swState = sws;
      }
      
      public SwitchHandler create (SwitchState sws) {
         return new SwitchHandler (sws);
      }
      
      public static SwitchHandler create () {
         return new SwitchHandler ();
      }

      /**
       * @param dpid
       */
      public void setDpid(Long dpid) {
         swState.setDpid(dpid);
         
      }

      /**
       * @return
       */
      public Long getDpid() {
         // TODO Auto-generated method stub
         return swState.getDpid();
      }
      
      public IOFMessageProvider getProvider () {
         return swState.provider;
      }

      /**
       * @param version
       */
      public void setVersion(Short ver) {
         swState.version = ver;
      }
      
      /**
       * @return
       */
      public Short getVersion(Short ver) {
         return swState.version;
      }
      
   }

   
}