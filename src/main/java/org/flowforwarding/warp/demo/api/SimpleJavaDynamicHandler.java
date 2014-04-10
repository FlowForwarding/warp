/**
 * В© 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.demo.api;

import org.flowforwarding.warp.controller.api.dynamic.DynamicMessageHandler;
import org.flowforwarding.warp.protocol.adapter.JDriverMessage;
import org.flowforwarding.warp.protocol.adapter.JDriverMessageBuilder;
import org.flowforwarding.warp.protocol.adapter.IOFMessageProviderAdapter;

class SimpleJavaDynamicHandler extends DynamicMessageHandler<IOFMessageProviderAdapter, JDriverMessage> {
    @Override
    public short[] supportedVersions(){
        return new short[] { 4 }; //1.3 only
    }

    @Override
    public JDriverMessage[] onDynamicMessage(IOFMessageProviderAdapter driver, long dpid, JDriverMessage msg) {
        if(msg.isTypeOf("ofp_switch_features_reply")){
            System.out.println("DPID from dynamic message: " + msg.primitiveField("datapathId"));
            JDriverMessageBuilder reqHeader = driver.getBuilder("ofp_header")
                                                    .setMember("xid", 0)
                                                    .setMember("length", 8 + 5);

            JDriverMessageBuilder request = driver.getBuilder("echo_request")
                                                  .setMember("header", reqHeader.build())
                                                  .setMember("elements", new byte[]{2, 2, 2, 2, 2});
            return new JDriverMessage[] { request.build() };
        }
        else if(msg.isTypeOf("echo_reply")){
            System.out.println("[OF-INFO] DPID: " + dpid + " Length of echo reply: " + msg.primitivesSequence("elements").length);
            return new JDriverMessage[]{};
        }
        else return new JDriverMessage[]{};
    }
}

