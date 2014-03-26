/**
 * Copyright 2014 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

