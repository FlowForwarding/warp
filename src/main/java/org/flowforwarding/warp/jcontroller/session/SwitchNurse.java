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
   IOFMessageProvider providerNew = null;
   
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
            providerNew = factory.getMessageProvider();
            
            if ((provider != null) && (providerNew != null)) {
               
               provider.init();
               providerNew.init();
               provider.parseMessages(in.toArray());
               
               swRef.setVersion(provider.getVersion());
               
               getSender().tell(TcpMessage.write(ByteString.fromArray(provider.encodeHelloMessage())), getSelf());
               this.state = State.CONNECTED;
               getSender().tell(TcpMessage.write(ByteString.fromArray(provider.encodeSwitchFeaturesRequest())), getSelf());    
               
               tcpChannel = getSender();
            }
            
            break;
         case CONNECTED:   
            in = ((Received) msg).data();
            
            if (provider.isSwitchFeatures(in.toArray())) {
               if (provider.getDPID(in.toArray()) != null) {
                  swRef.setDpid(provider.getDPID(in.toArray()));
                  System.out.println("[OF-INFO] DPID: " + Long.toHexString(swRef.getDpid().longValue()) +" Feature Reply is received from the Switch ");
                  System.out.println("[OF-INFO] Connected to Switch "+ Long.toHexString(swRef.getDpid().longValue()));
                  state = State.HANDSHAKED;
                  
                  ofSessionHandler.tell(new OFEventHandshaked(swRef), getSelf());
               }
            }
            break;
            
         case HANDSHAKED:
            in = ((Received) msg).data();
            
            if (provider.isConfig(in.toArray())) {
               System.out.println("[OF-INFO] DPID: " + Long.toHexString(swRef.getDpid().longValue()) +" Switch Config is received from the Switch ");
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
