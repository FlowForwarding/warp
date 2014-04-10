package org.flowforwarding.warp.demo.api;

import org.flowforwarding.warp.controller.api.fixed.BuilderInput;
import org.flowforwarding.warp.controller.api.fixed.v13.*;
import org.flowforwarding.warp.protocol.adapter.JDriverMessage;
import org.flowforwarding.warp.protocol.adapter.JDriverMessageBuilder;

class SimpleJavaOfp13Handler extends Ofp13MessageHandler<JDriverMessageBuilder, JDriverMessage> {

    public SimpleJavaOfp13Handler() {
        super(JDriverMessage.class);
    }

    @Override
    public BuilderInput[] onEchoReply(long dpid, EchoReply msg) {
        System.out.println("[OF-INFO] DPID: " + dpid + " Length of echo reply: " + msg.elements().length);
        return new BuilderInput[0];
    }

    @Override
    public BuilderInput[] onFeaturesReply(long dpid, FeaturesReply msg) {
        System.out.println("DPID from dynamic message: " + msg.datapathId());
        System.out.println("Features: " + msg.capabilities());

        return new BuilderInput[] { new EchoRequestInput(msg.header().xid(), new byte[]{2, 2, 2, 2, 2}) };
    }
}