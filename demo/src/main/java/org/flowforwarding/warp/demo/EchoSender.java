/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.demo;

import spire.math.UInt;

import org.flowforwarding.warp.controller.bus.ControllerBus;
import org.flowforwarding.warp.controller.message_handlers.fixed.IncomingMessagePredicate;

import org.flowforwarding.warp.driver_api.dynamic.DynamicStructureBuilder;
import org.flowforwarding.warp.driver_api.fixed.BuilderInput;
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.Ofp13MessageHandlers;
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.symmetric.EchoReply;
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.symmetric.EchoRequestInput;


class EchoSender extends Ofp13MessageHandlers {
    int n = 0;
    int max = 0;
    int messagesCount = 0;

    public EchoSender(ControllerBus bus, String n, String max, String messagesCount){
        super(bus);
        this.n = Integer.valueOf(n);
        this.max = Integer.valueOf(max);
        this.messagesCount = Integer.valueOf(messagesCount) / this.max;
    }

    @Override
    public void started() {
        subscribe("echo", testIncomingMessage(new IncomingMessagePredicate() {
            @Override
            public Boolean test(long id, Object payload) {
                return (Long.valueOf(id).hashCode() % max) == n && payload instanceof EchoReply;
            }
        }));
    }

    @Override
    public void handleDisconnected(DynamicStructureBuilder api, long dpid) {
        super.handleDisconnected(api, dpid);
        shutdown();
    }

    @Override
    public BuilderInput[] onEchoReply(long dpid, EchoReply msg) {
//        System.out.println("[OF-INFO] SenderID: " + n +
//                           " DPID: " + dpid +
//                           " Length of echo reply: " + msg.elements().length +
//                           ". Xid: " + msg.header().xid());
        int xid = (int)(Object)msg.header().xid();

        if(xid > messagesCount - 100)
            System.out.println(msg.header().xid());

        if(messagesCount > 0){
            messagesCount--;
            return new BuilderInput[] { new EchoRequestInput(new byte[]{2, 2, 2, 2, 2}) };
        }
        else {
            shutdown();
            return new BuilderInput[] { };
        }
    }
}