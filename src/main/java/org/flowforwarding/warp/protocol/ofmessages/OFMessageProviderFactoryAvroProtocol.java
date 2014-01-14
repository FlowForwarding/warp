/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.ofmessages;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessageProviderFactoryAvroProtocol implements IOFMessageProviderFactory{

   /**
    * @author Infoblox Inc.
    * @doc.desc Gets the Message Provider based on Version number
    * @param  version String containing version value 
    * @see org.flowforwarding.warp.controller.protocol.OFMessageProviderFactory#getMessageProvider(java.lang.String)
    */
   
   // TODO Improvs: Make Provider static and final?
   @Override
   public IOFMessageProvider getMessageProvider(String version) {
      
      // TODO Improvs: select an appropriate Protocol based on Version
      return new OFMessageProvider13AvroProtocol();
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.warp.controller.protocol.OFMessageProviderFactory#getMessageProvider(byte[])
    */
  /**
   * @author Infoblox Inc.
   * @doc.desc Gets the Message Provider based on Byte array containing Hello message
   * @param  version Byte array with Hello message 
   * @see org.flowforwarding.warp.controller.protocol.OFMessageProviderFactory#getMessageProvider(java.lang.String)
   */
   @Override
   public IOFMessageProvider getMessageProvider(byte[] hello) {
      IOFMessageProvider provider;     
      byte probVersion = hello[0];
      
      switch (probVersion) {
      case 1:
         provider = new OFMessageProvider10AvroProtocol(); 
         break;
      case 4:
         provider = new OFMessageProvider13AvroProtocol();
         break;
      default:
         provider = null;
      }
   // TODO Improvs: Getting provider, let's check whether this is a Hello message?
      return provider; 
   }

}
