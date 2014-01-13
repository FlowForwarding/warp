package org.flowforwarding.warp.demo.ofdriver;

import org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider;
import org.flowforwarding.warp.protocol.ofmessages.IOFMessageProviderFactory;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageProviderFactoryAvroProtocol;

public class Launcher {

   public static void main(String[] args) {
      IOFMessageProviderFactory factory = new OFMessageProviderFactoryAvroProtocol();
      IOFMessageProvider provider = factory.getMessageProvider("1.3");
   }

}
