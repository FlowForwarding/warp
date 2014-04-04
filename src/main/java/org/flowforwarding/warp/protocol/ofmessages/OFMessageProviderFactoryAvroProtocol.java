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

    @Override
    public IOFMessageProvider getMessageProvider(Short version) {
        switch (version) {
            case 1: return new OFMessageProvider10AvroProtocol();
            case 4: return new OFMessageProvider13AvroProtocol();
            default: return null;
        }
    }

    /**
   * @author Infoblox Inc.
   * @doc.desc Gets the Message Provider based on Byte array containing Hello message
   * @param  version Byte array with Hello message 
   * @see org.flowforwarding.warp.controller.protocol.OFMessageProviderFactory#getMessageProvider(byte[])
   */
   @Override
   public IOFMessageProvider getMessageProvider(byte[] hello) {
       // TODO Improvs: Getting provider, let's check whether this is a Hello message?
       return getMessageProvider(new Short(hello[0]));
   }
   
   @Override
   public IOFMessageProvider getMessageProvider() {
      return new OFMessageProviderAvroProtocol();
  }

}
