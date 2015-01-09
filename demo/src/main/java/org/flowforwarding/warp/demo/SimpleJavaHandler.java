/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.demo;

import spire.math.UByte;

import org.flowforwarding.warp.controller.*;
import org.flowforwarding.warp.controller.bus.*;
import org.flowforwarding.warp.controller.message_handlers.dynamic.java_api.*;
import org.flowforwarding.warp.driver_api.XidGenerator;

public class SimpleJavaHandler extends DynamicMessageHandlers {
    public SimpleJavaHandler(ControllerBus bus){
        super(bus);
    }

    @Override
    public void started(){
        subscribe("incomingMessage", new MessageEnvelopePredicate() {
            public Boolean test(MessageEnvelope me) {
                return me instanceof SwitchConnector.SwitchIncomingMessage;
            }
        });
    }

    @Override
    public UByte[] supportedVersions(){
        return new UByte[] { new UByte((byte)4) }; //1.3 only
    }

    public Input getMessageInput(Builder api, String messageType, long xid){
        Input reqHeader = api.newBuilderInput("ofp_header").setMember("xid", xid);
        return api.newBuilderInput(messageType).setMember("header", reqHeader);
    }

    public Input getMessageInput(Builder api, String messageType){
        return getMessageInput(api, messageType, XidGenerator.nextXid());
    }

    @Override
    public Input[] handleMessage(Builder api, long dpid, Structure msg) {
        if(msg.isTypeOf("ofp_switch_features_reply")){
            System.out.println("DPID from dynamic message: " + msg.primitiveField("datapathId"));
            Input request = getMessageInput(api, "echo_request").setMember("elements", new long[]{2, 2, 2, 2, 2});
            return new Input[] { request };
        }
        else if(msg.isTypeOf("echo_reply")){
            System.out.println("[OF-INFO] DPID: " + dpid + " Length of echo reply: " + msg.primitivesSequence("elements").length);
            return new Input[]{};
        }
        else return new Input[]{};
    }
}

