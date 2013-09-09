package org.flowforwarding.of.protocol.ofmessages;

public interface IOFMessageProviderFactory {
   
   public IOFMessageProvider getMessageProvider (String version);
   
   public IOFMessageProvider getMessageProvider (byte [] hello);

}
