/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */

package org.flowforwarding.warp.ofswitch;

import java.io.ByteArrayOutputStream;

import org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider;
import org.flowforwarding.warp.protocol.ofmessages.IOFMessageProviderFactory;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageProviderFactoryAvroProtocol;

/**
 * 
 * @author Infoblox Inc.
 * @doc.desc Contains OpenFlow switch-related data:
 * <ul>
 *  <li> DPID </li> 
 *  <li> Version supported </li>
 *  <li> Message Provider </li>
 */
public class SwitchState {
   
   protected Long dpid = null;
   protected ByteArrayOutputStream lastIncomingMessage = null;
   protected Short version = 4;
   protected IOFMessageProvider provider = null;
   protected IOFMessageProviderFactory factory = null;
   
   /**
    * Default constructor
    */
   protected SwitchState () {
      factory = new OFMessageProviderFactoryAvroProtocol();
      provider = factory.getMessageProvider("1.3");
   }
   
   /**
    * 
    * @return Long value of DPID
    */
   protected Long getDpid() {
      return dpid;
   }

   /**
    * 
    * @param dpid
    * - Long DPID
    */
   protected void setDpid(Long dpid) {
      this.dpid = dpid;
   }
   
   /**
    * @author Infoblox Inc.
    * @doc.desc Reference to SwitchState object 
    */
   public static class SwitchRef {
      
      protected SwitchState swState = null;
      
      protected SwitchRef () {
         swState = new SwitchState ();
      }
      
      protected SwitchRef (SwitchState sws) {
         swState = sws;
      }
      /**
       * @param sws
       * @return switchRef
       */
      public SwitchRef create (SwitchState sws) {
         return new SwitchRef (sws);
      }
      
      /**
       * @return switchRef
       */
      public static SwitchRef create () {
         return new SwitchRef ();
      }

      /**
       * @param dpid
       * - Long value of DPID
       */
      public void setDpid(Long dpid) {
         swState.setDpid(dpid);
         
      }

      /**
       * @return Long value of DPID
       */
      public Long getDpid() {
         // TODO Auto-generated method stub
         return swState.getDpid();
      }
      
      /**
       * 
       * @return OF Message Provider
       */
      public IOFMessageProvider getProvider () {
         return swState.provider;
      }

      /**
       * @param version
       */
      public void setVersion(Short version) {
         swState.version = version;
      }
      
      /**
       * @return OpenFlow protocol version supported by Switch 
       */
      public Short getVersion(Short version) {
         return swState.version;
      }
      
   }

   
}