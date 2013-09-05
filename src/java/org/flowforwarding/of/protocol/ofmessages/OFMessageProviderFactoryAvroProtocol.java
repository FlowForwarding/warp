/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.of.protocol.ofmessages;

/**
 * @author Infoblox Inc.
 *
 */
public class OFMessageProviderFactoryAvroProtocol implements OFMessageProviderFactory{

   /* (non-Javadoc)
    * @see org.flowforwarding.of.controller.protocol.OFMessageProviderFactory#getMessageProvider(java.lang.String)
    */
   
   // TODO Improvs: Make Provider static and final?
   @Override
   public OFMessageProvider getMessageProvider(String version) {
      
      // TODO Improvs: select an appropriate Protocol based on Version
      return new OFMessageProvider13AvroProtocol();
   }

   /* (non-Javadoc)
    * @see org.flowforwarding.of.controller.protocol.OFMessageProviderFactory#getMessageProvider(byte[])
    */
   @Override
   public OFMessageProvider getMessageProvider(byte[] hello) {
      // TODO Improvs: Select Protocol depending of version from Hello header
      return new OFMessageProvider13AvroProtocol();
   }

}
