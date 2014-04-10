package org.flowforwarding.warp.demo.api;

import java.util.HashSet;

import org.flowforwarding.warp.controller.Controller;
import org.flowforwarding.warp.controller.session.SessionHandlerRef;

import org.flowforwarding.warp.controller.api.SessionHandler;
import org.flowforwarding.warp.controller.api.dynamic.DynamicMessageHandler;
import org.flowforwarding.warp.controller.api.fixed.SpecificVersionMessageHandler;

import org.flowforwarding.warp.protocol.adapter.IOFMessageProviderFactoryAdapter;
import org.flowforwarding.warp.protocol.adapter.JDriverMessage;
import org.flowforwarding.warp.protocol.adapter.IOFMessageProviderAdapter;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageProviderFactoryAvroProtocol;

class JavaDynamicLauncher{
    public static void main(String[] args){
        IOFMessageProviderFactoryAdapter factory = new IOFMessageProviderFactoryAdapter(new OFMessageProviderFactoryAvroProtocol());

        final SessionHandlerRef launcher = SessionHandler.makeRef(
            factory,
            new HashSet<DynamicMessageHandler<IOFMessageProviderAdapter, JDriverMessage>>()
            {{
                //add(new SimpleJavaDynamicHandler());
            }},
            new HashSet<SpecificVersionMessageHandler<?, JDriverMessage>>()
            {{
                add(new SimpleJavaOfp13Handler());
            }});

        Controller.launch(new HashSet<SessionHandlerRef>() {{
            add(launcher);
        }});
    }
}
