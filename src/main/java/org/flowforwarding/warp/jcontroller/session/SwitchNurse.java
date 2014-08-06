/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */

package org.flowforwarding.warp.jcontroller.session;

import org.flowforwarding.warp.ofswitch.SwitchState.SwitchRef;
import org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider;
import org.flowforwarding.warp.protocol.ofmessages.IOFMessageProviderFactory;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageProviderFactoryAvroProtocol;
import org.flowforwarding.warp.protocol.ofstructures.OFStructureInstruction.OFStructureInstructionRef;
import org.flowforwarding.warp.protocol.ofp.avro.OFMessage;
import org.flowforwarding.warp.protocol.ofp.avro.OFMessage.OFMessageBuilder;
import org.flowforwarding.warp.util.Convert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.io.TcpMessage;
import akka.io.Tcp.ConnectionClosed;
import akka.io.Tcp.Received;
import akka.util.ByteString;

/**
 * 
 * @author Infoblox Inc.
 * @doc.desc Handles connection Switch - Controller. Primarily analyzes incoming messages
 *
 */
public class SwitchNurse extends UntypedActor {
   
   private final String schemaSrc = "of_protocol_13.avpr";
   private static final Logger log =  LoggerFactory.getLogger(SwitchNurse.class);
   
   private enum State {
      STARTED,
      CONNECTED,
      HANDSHAKED,
      CONFIG_READY,
      READY
   }
   
   private State state = State.STARTED;
   private SwitchRef swRef;
   
   private ActorRef ofSessionHandler = null;
   private ActorRef tcpChannel = null;
   
   IOFMessageProviderFactory factory = new OFMessageProviderFactoryAvroProtocol();
   IOFMessageProvider provider = null;

   OFMessageBuilder builder = null;
   
   
   @Override
   public void preStart() throws Exception {
      super.preStart();
      
      swRef = SwitchRef.create();
   }
   @Override
   public void onReceive(Object msg) throws Exception {
      if (msg instanceof Received) {
         
         switch (this.state) {
         case STARTED:
            ByteString in = ((Received) msg).data();
            provider = factory.getMessageProvider(in.toArray());

            builder = new org.flowforwarding.warp.protocol.ofp.avro.OFMessage.OFMessageBuilder(in.toArray());            
            OFMessage inMsg = builder.value(in.toArray()).build(); 

            if ((provider != null) && (inMsg != null)) {
                if (inMsg.type().equals("OFPT_HELLO")) {
                	log.info ("IN: Hello");
                    swRef.setVersion(builder.version());
//                    getSender().tell(TcpMessage.write(ByteString.fromArray(builder.type("ofp_hello").set("header.xid", "0xabba").build().binary())), getSelf());
                    OFMessage helloMsg = builder.type("ofp_hello").build();
                    byte[] v = {127,127,127,127};
                    helloMsg.get("header").get("xid").set(v);

                    //getSender().tell(TcpMessage.write(ByteString.fromArray(builder.type("ofp_hello").build().binary())), getSelf());
                    getSender().tell(TcpMessage.write(ByteString.fromArray(helloMsg.binary())), getSelf());
                    this.state = State.CONNECTED;
                    log.info ("STATE: Connected to OF Switch version "+ builder.version());
//                    getSender().tell(TcpMessage.write(ByteString.fromArray(builder.type("ofp_switch_features_request").set("xid", "0xabba").build().binary())), getSelf());
                    getSender().tell(TcpMessage.write(ByteString.fromArray(builder.type("ofp_switch_features_request").build().binary())), getSelf());

                    // TODO REMOVE THIS:
                    provider.init();
                    tcpChannel = getSender();
                }
            }
            
            break;
         case CONNECTED:   
            in = ((Received) msg).data();
            inMsg = builder.value(in.toArray()).build();
           
            if (inMsg.type().equals("OFPT_FEATURES_REPLY")) {
               log.info("IN: Features Reply");
               byte[] DPID = inMsg.field("datapath_id");
               swRef.setDpid(Convert.toLong(DPID));
               log.info("INFO: Switch DPID is " + Long.toHexString(Convert.toLong(DPID)).toUpperCase());
             
               state = State.HANDSHAKED;
               ofSessionHandler.tell(new OFEventHandshaked(swRef), getSelf());

               getSender().tell(TcpMessage.write(ByteString.fromArray(builder.type("ofp_get_config_request").build().binary())), getSelf());
            }
            
            break;
            
         case HANDSHAKED:
            in = ((Received) msg).data();
            inMsg = builder.value(in.toArray()).build();
            
            if (inMsg.type().equals("OFPT_GET_CONFIG_REPLY")) {
               log.info("IN: Config Reply from Switch " + Long.toHexString(swRef.getDpid().longValue()));
               
               OFMessage flowMod = builder.type("ofp_flow_mod").build();
/*               OFItemRef matchInPort = itemBuilder.type("oxm_tlv_ingress_port").build();
               matchInPort.set("tlv", "4");
               OFItemRef tlv = itemBuilder.type("oxm_tlv").build();
               tlv.add("match", matchInPort);
               
               OFMessageRef tlv_fields = builder.type("oxm_tlv_fields").build();
               //tlv_fields.add("oxm_tlvs", tlv);
               tlv_fields.get("oxm_tlvs").add(tlv);
               
               tlv_fields.binary();*/
            } 
            
            if (provider.isConfig(in.toArray())) {
               ofSessionHandler.tell(new OFEventSwitchConfig(swRef, provider.parseSwitchConfig(in.toArray())), getSelf());
            } else if (provider.isPacketIn(in.toArray())) {
               System.out.println("[OF-INFO] DPID: " + Long.toHexString(swRef.getDpid().longValue()) +" Packet-In is received from the Switch");
               ofSessionHandler.tell(new OFEventPacketIn(swRef, provider.parsePacketIn(in.toArray())), getSelf());
            }  else if (provider.isError(in.toArray())) {
               System.out.println("[OF-INFO] DPID: " + Long.toHexString(swRef.getDpid().longValue()) + " Error is received from the Switch ");
               ofSessionHandler.tell(new OFEventError(swRef, provider.parseError(in.toArray())), getSelf());
            }  else if (provider.isEchoRequest(in.toArray())) {
               System.out.println("[OF-INFO] DPID: " + Long.toHexString(swRef.getDpid().longValue()) + " Echo request is received from the Switch ");
               getSender().tell(TcpMessage.write(ByteString.fromArray(provider.encodeEchoReply())), getSelf());
            }
            //ofSessionHandler.tell(new OFEventIncoming(swRef), getSelf());
            
            break;
         default:
            break;
            
         }

      } else if (msg instanceof ConnectionClosed) {
         getContext().stop(getSelf());
      } else if (msg instanceof ActorRef) {
         ofSessionHandler = (ActorRef) msg;
      } else if (msg instanceof OFCommandSendSwConfigRequest) {
         tcpChannel.tell(TcpMessage.write(ByteString.fromArray(provider.encodeSwitchConfigRequest())), getSelf());
      } else if (msg instanceof TellToSendFlowMod) {
         System.out.println("[OF-INFO]: Send Flow Mod");
         
         OFMessageFlowModRef flowModRef = provider.buildFlowModMsg();
         flowModRef.addField("priority", "32000");
         flowModRef.addMatchInPort(swRef.getDpid().toString().substring(0, 3));

         OFStructureInstructionRef instruction = provider.buildInstructionApplyActions();
         instruction.addActionOutput("2");
         flowModRef.addInstruction("apply_actions", instruction);

         instruction = provider.buildInstructionGotoTable();
         flowModRef.addInstruction("goto_table", instruction);
         
         tcpChannel.tell(TcpMessage.write(ByteString.fromArray(provider.encodeFlowMod(flowModRef))), getSelf());
      }
   }

}
