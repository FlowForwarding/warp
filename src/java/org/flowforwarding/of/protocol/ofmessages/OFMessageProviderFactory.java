package org.flowforwarding.of.protocol.ofmessages;

public interface OFMessageProviderFactory {
   
   public OFMessageProvider getMessageProvider (String version);
   
   public OFMessageProvider getMessageProvider (byte [] hello);

}
