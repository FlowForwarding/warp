/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.demo;

import org.flowforwarding.warp.controller.ModuleManager;
import org.flowforwarding.warp.controller.bus.ControllerBus;
import org.flowforwarding.warp.controller.message_handlers.fixed.IncomingMessagePredicate;

import org.flowforwarding.warp.driver_api.fixed.util.MacAddress;
import org.flowforwarding.warp.driver_api.fixed.util.ULongFitData;
import org.flowforwarding.warp.driver_api.fixed.BuilderInput;
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.*;
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.FeaturesReply;
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.symmetric.EchoReply;
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.symmetric.EchoRequestInput;

public class EchoSendersRunner extends Ofp13MessageHandlers {
    int max = 0;
    long messagesCount = 0;

    public EchoSendersRunner(ControllerBus bus, String max, String messagesCount){
        super(bus);
        this.max = Integer.valueOf(max);
        this.messagesCount = Long.valueOf(messagesCount);
    }

    @Override
    public void started() {
        subscribe("connected", testIncomingMessage(
            new IncomingMessagePredicate() {
                @Override
                public Boolean test(long dpid, Object payload) {
                    return payload instanceof FeaturesReply;
                }
            }));

        String echoSenderClass = EchoSender.class.getCanonicalName();
        for(int i = 0; i < max; i++){
            String[] args = new String[] { Integer.toString(i), Integer.toString(max), Long.toString(messagesCount) };
            askFirst(new ModuleManager.AddModule("echo_" + i, echoSenderClass, args));
        }
    }

    @Override
    public BuilderInput[] onFeaturesReply(long dpid, FeaturesReply msg) {
        System.out.println("DPID from dynamic message: " + msg.datapathId());
        System.out.println("Features: " + msg.capabilities());

        return new BuilderInput[] { new EchoRequestInput(new byte[]{2, 2, 2, 2, 2}) };
    }
}
