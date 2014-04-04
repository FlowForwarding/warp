/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.protocol.ofmessages;

public interface IOFMessageProviderFactory {
   
   public IOFMessageProvider getMessageProvider (String version);

   public IOFMessageProvider getMessageProvider (Short version);

   public IOFMessageProvider getMessageProvider (byte [] hello);

   /**
    * @return
    */
   IOFMessageProvider getMessageProvider();
}
