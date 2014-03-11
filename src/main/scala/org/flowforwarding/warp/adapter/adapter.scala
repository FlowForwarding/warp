package org.flowforwarding.warp.protocol.adapter

import scala.util.Try

import org.flowforwarding.warp.controller.session.{OFSessionHandler, MessageDriverFactory, OFMessage, MessageDriver}

import org.flowforwarding.warp.protocol.ofmessages.{OFMessageRef, IOFMessageProviderFactory, IOFMessageProvider}
import org.flowforwarding.warp.protocol.ofmessages.OFMessageHello.OFMessageHelloRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessageSwitchConfigRequest.OFMessageSwitchConfigRequestRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessageSwitchFeaturesRequest.OFMessageSwitchFeaturesRequestRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessageEchoReply.OFMessageEchoReplyRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessageEchoRequest.OFMessageEchoRequestRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessageGroupMod.OFMessageGroupModRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessagePacketIn.OFMessagePacketInRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessageSwitchConfig.OFMessageSwitchConfigRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessageError.OFMessageErrorRef


case class JDriverMessage(ref: OFMessageRef[_]) extends OFMessage

case class IOFMessageProviderAdapter(provider: IOFMessageProvider) extends MessageDriver[JDriverMessage]{
  provider.init()

  def decodeMessage(in: Array[Byte]): Try[JDriverMessage] = Try {
    val res = if (provider.isHello(in))
                provider.parseHelloMessage(in)
              else if (provider.isPacketIn(in))
                provider.parsePacketIn(in)
              else if(provider.isConfig(in))
                provider.parseSwitchConfig(in)
              else if (provider.isError(in))
                provider.parseError(in)
              //else if (provider.isEchoRequest(in))
              //  provider.parseEchoRequest(in)
              //else if (provider.isSwitchFeatures(in))
              //  provider.parseSwitchFeatures(in)
              else throw new RuntimeException("Unrecognized message")
    JDriverMessage(res)
  }

  def encodeMessage(msg: JDriverMessage): Try[Array[Byte]] = Try {
    msg.ref match{
      case m: OFMessageHelloRef                 => provider.encodeHelloMessage()
      case m: OFMessageSwitchConfigRequestRef   => provider.encodeSwitchConfigRequest()
      case m: OFMessageSwitchFeaturesRequestRef => provider.encodeSwitchFeaturesRequest()
      case m: OFMessageEchoReplyRef             => provider.encodeEchoReply()
      case m: OFMessageEchoRequestRef           => provider.encodeEchoRequest()
      case m: OFMessageFlowModRef               => provider.encodeFlowMod(m)
      case m: OFMessageGroupModRef              => provider.encodeGroupMod(m)
      case m => throw new RuntimeException("Unable to encode such kind of message: " + m)
    }
  }

  def getDPID(in: Array[Byte]): Try[Long] = Try(provider.getDPID(in).longValue())

  val versionCode: Short = provider.getVersion
}

case class IOFMessageProviderFactoryAdapter(factory: IOFMessageProviderFactory) extends MessageDriverFactory[JDriverMessage]{
  def get(versionCode: Short): Option[MessageDriver[JDriverMessage]] =
    Try(factory.getMessageProvider(versionCode)).map(IOFMessageProviderAdapter.apply).toOption
}

abstract class OFJDriverSessionHandler(pFactory: IOFMessageProviderFactory) extends OFSessionHandler(IOFMessageProviderFactoryAdapter(pFactory)){

  var provider: IOFMessageProvider = null

  override def connected(versionCode: Short) {
    provider = pFactory.getMessageProvider(versionCode)
  }

  implicit def refsToMessages(refs: Seq[OFMessageRef[_]]) = refs map JDriverMessage.apply

  protected def getHandshakeMessage(msg: JDriverMessage): Seq[JDriverMessage] = {
    refsToMessages(Seq(OFMessageHelloRef.create, OFMessageSwitchFeaturesRequestRef.create))
  }

  protected def onReceivedMessage(dpid: Long, msg: JDriverMessage): Seq[JDriverMessage] = {
    msg.ref match{
      case p: OFMessagePacketInRef     => packetIn(provider, dpid, p)
      case c: OFMessageSwitchConfigRef => switchConfig(provider, dpid, c)
      case e: OFMessageErrorRef        => error(provider, dpid, e)
    }
  }

  def packetIn(provider: IOFMessageProvider, dpid: Long, pIn: OFMessagePacketInRef): Seq[OFMessageRef[_]]
  def switchConfig(provider: IOFMessageProvider, dpid: Long, config: OFMessageSwitchConfigRef): Seq[OFMessageRef[_]]
  def error(provider: IOFMessageProvider, dpid: Long, error: OFMessageErrorRef): Seq[OFMessageRef[_]]
}