package org.flowforwarding.warp.protocol.adapter

import scala.util.Try
import org.flowforwarding.warp.controller.session._
import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.protocol.ofmessages.{OFMessageRef, IOFMessageProviderFactory, IOFMessageProvider}
import org.flowforwarding.warp.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessageGroupMod.OFMessageGroupModRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessageEchoRequest.OFMessageEchoRequestRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessageEchoReply.OFMessageEchoReplyRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessageSwitchFeaturesRequest.OFMessageSwitchFeaturesRequestRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessageSwitchConfigRequest.OFMessageSwitchConfigRequestRef
import org.flowforwarding.warp.protocol.ofmessages.OFMessageHello.OFMessageHelloRef


case class JDriverMessage(ref: OFMessageRef[_]) extends DynamicStructure[JDriverMessage]{
  def primitiveField(name: String): Long = ???

  def structureField(name: String): JDriverMessage = ???

  def primitivesSequence(name: String): Array[Long] = ???

  def structuresSequence(name: String): Array[JDriverMessage] = ???

  def isTypeOf(typeName: String): Boolean = ???
}

class JDriverMessageBuilder extends DynamicStructureBuilder[JDriverMessageBuilder, JDriverMessage]{
  def setMember(memberName: String, value: Long): JDriverMessageBuilder = ???

  def setMember[T](memberName: String, values: Array[T]): JDriverMessageBuilder = ???

  def setMember(memberName: String, value: JDriverMessage): JDriverMessageBuilder = ???

  def build: JDriverMessage = ???
}

case class IOFMessageProviderAdapter(provider: IOFMessageProvider) extends DynamicDriver[JDriverMessageBuilder, JDriverMessage]{
  provider.init()

  def getBuilder(msgType: String): JDriverMessageBuilder = ???

  def getHelloMessage(supportedVersions: Array[Short]): Array[Byte] = ???

  def rejectVersionError(reason: String): Array[Byte] = ???

  def getFeaturesRequest: Array[Byte] = ???

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

case class IOFMessageProviderFactoryAdapter(factory: IOFMessageProviderFactory) extends MessageDriverFactory[JDriverMessage, IOFMessageProviderAdapter]{
  def get(versionCode: Short): IOFMessageProviderAdapter =
    IOFMessageProviderAdapter(factory.getMessageProvider(versionCode))

  def supportedVersions: Array[Short] = Array(4.toShort) // ???
}