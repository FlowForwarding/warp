package org.flowforwarding.of.demo;

import org.flowforwarding.of.controller.session.OFSessionHandler;
import org.flowforwarding.of.ofswitch.SwitchState.SwitchHandler;
import org.flowforwarding.of.protocol.ofmessages.IOFMessageProvider;
import org.flowforwarding.of.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInHandler;
import org.flowforwarding.of.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigHandler;
import org.flowforwarding.of.protocol.ofstructures.OFStructureInstruction.OFStructureInstructionHandler;

public class SimpleHandler extends OFSessionHandler {

   @Override
   protected void switchConfig(SwitchHandler swH, OFMessageSwitchConfigHandler configH) {
      super.switchConfig(swH, configH);
      
      System.out.print("[OF-INFO] DPID: " + Long.toHexString(swH.getDpid()) + " Configuration: ");

      if (configH.isFragDrop()) {
         System.out.println("Drop fragments");
      }
      
      if (configH.isFragMask()) {
         System.out.println("Mask");
      }
      
      if (configH.isFragNormal()) {
         System.out.println("Normal");
      }

      if (configH.isFragReasm()) {
         System.out.println("Reassemble");
      }      
      
   }

   @Override
   protected void handshaked(SwitchHandler swH) {
      super.handshaked(swH);
      System.out.println("[OF-INFO] HANDSHAKED " + Long.toHexString(swH.getDpid()));
      
      sendSwitchConfigRequest(swH);
   }
   
   @Override
   protected void packetIn(SwitchHandler swH, OFMessagePacketInHandler packetIn) {
      super.packetIn(swH, packetIn);
      IOFMessageProvider provider = swH.getProvider();
      
      OFMessageFlowModHandler flowMod = provider.buildFlowModMsg();
      
      if (packetIn.existMatchInPort()) {
         flowMod.addMatchInPort(packetIn.getMatchInPort().getMatch());
      } else if (packetIn.existMatchEthDst()) {
         flowMod.addMatchEthDst(packetIn.getMatchEthDst().getMatch());
      } else if (packetIn.existMatchEthSrc()) {
         flowMod.addMatchEthSrc(packetIn.getMatchEthSrc().getMatch());
      }
      
      OFStructureInstructionHandler instruction = provider.buildInstructionApplyActions();
      instruction.addActionOutput("2");
      flowMod.addInstruction("apply_actions", instruction);

      sendFlowModMessage(swH, flowMod);
   }
}
