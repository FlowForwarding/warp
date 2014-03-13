/**
 * В© 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.demo

import org.flowforwarding.warp.controller.session.SessionHandlerLauncher

import org.flowforwarding.warp.protocol.adapter.OFJDriverSessionHandler
import org.flowforwarding.warp.protocol.ofmessages.{OFMessageProviderFactoryAvroProtocol, OFMessageRef, IOFMessageProvider, IOFMessageProviderFactory}
import org.flowforwarding.warp.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessageError.OFMessageErrorRef
import org.flowforwarding.warp.controller.Controller

class JDriverSimpleHandler(pFactory: IOFMessageProviderFactory) extends OFJDriverSessionHandler(pFactory){

  def packetIn(provider: IOFMessageProvider, dpid: Long, pIn: OFMessagePacketInRef): Seq[OFMessageRef[_]] = {
    val flowMod = provider.buildFlowModMsg
    if (pIn.existMatchInPort()) {
      flowMod.addMatchInPort(pIn.getMatchInPort.getMatch)
    } else if (pIn.existMatchEthDst()) {
      flowMod.addMatchEthDst(pIn.getMatchEthDst.getMatch)
    } else if (pIn.existMatchEthSrc()) {
      flowMod.addMatchEthSrc(pIn.getMatchEthSrc.getMatch)
    }

    val instruction = provider.buildInstructionApplyActions
    instruction.addActionOutput("2")
    flowMod.addInstruction("apply_actions", instruction)
    Seq(flowMod)
  }

  def switchConfig(provider: IOFMessageProvider, dpid: Long, config: OFMessageSwitchConfigRef): Seq[OFMessageRef[_]] = {
    println(s"[OF-INFO] DPID: $dpid Configuration: ")
    if (config.isFragDrop)   println("Drop fragments")
    if (config.isFragMask)   println("Mask")
    if (config.isFragNormal) println("Normal")
    if (config.isFragReasm)  println("Reassemble")
    Seq()
  }

  def error(provider: IOFMessageProvider, dpid: Long, error: OFMessageErrorRef): Seq[OFMessageRef[_]] = {
    println(s"[OF-INFO] DPID: $dpid Error(code = ${error.getCode}, type = ${error.getType})")
    Seq()
  }
}

object JavaLauncher extends App {
  Controller.launch(Set(SessionHandlerLauncher(classOf[JDriverSimpleHandler], new OFMessageProviderFactoryAvroProtocol)))
}

