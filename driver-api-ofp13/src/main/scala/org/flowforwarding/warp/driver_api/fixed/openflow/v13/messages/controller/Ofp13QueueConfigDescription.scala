/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller

import org.flowforwarding.warp.driver_api.fixed.SpecificVersionMessageHandlersSet
import spire.math._

import org.flowforwarding.warp.driver_api.dynamic._
import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.{Ofp13HeaderDescription, PortNumber}
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.{Ofp13MessageInput, Ofp13Message, Ofp13MessageDescription}
import org.flowforwarding.warp.driver_api.fixed.text_view.BITextView

/* Full description for a queue. */
trait PacketQueue{
  val id: UInt
  val port: PortNumber
  val properties: Array[QueueProperties]
}

trait QueueProperties

/* Min-Rate queue property description. */
trait MinRate extends QueueProperties{ val rate: UInt }

/* Max-Rate queue property description. */
trait MaxRate extends QueueProperties{ val rate: UInt }

/* Experimenter queue property description. */
trait ExperimenterProperties extends QueueProperties{
  val experimenter: UInt
  val data: Array[Byte]
}


trait QueueGetConfigReply extends Ofp13Message{
  def port: PortNumber
  def queues: Array[PacketQueue]
}

case class QueueGetConfigRequestInput(port: PortNumber) extends Ofp13MessageInput

private[fixed] trait QueueGetConfigReplyHandler{
  def onFeaturesReply(dpid: ULong, msg: QueueGetConfigReply): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13QueueGetConfigDescription extends Ofp13MessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _] with QueueGetConfigReplyHandler] with Ofp13HeaderDescription =>

  protected[fixed] implicit object QueueProperties extends FromDynamic[QueueProperties]{
    val fromDynamic: PartialFunction[DynamicStructure, QueueProperties] = {
      case s if s.ofType[MinRate] => new OfpStructure[MinRate](s) with MinRate { val rate = primitiveField[UInt]("rate") }
      case s if s.ofType[MaxRate] => new OfpStructure[MaxRate](s) with MaxRate { val rate = primitiveField[UInt]("rate") }
      case s if s.ofType[ExperimenterProperties] =>
        new OfpStructure[ExperimenterProperties](s) with ExperimenterProperties{
          val experimenter = primitiveField[UInt]("experimenter")
          val data: Array[Byte] = bytes("data")
        }
    }
  }

  protected[fixed] implicit object PacketQueue extends FromDynamic[PacketQueue]{
    val fromDynamic: PartialFunction[DynamicStructure, PacketQueue] = {
      case s if s.ofType[PacketQueue] => new OfpStructure[PacketQueue](s) with PacketQueue{
        val id = primitiveField[UInt]("id")
        val port = PortNumber(primitiveField[UInt]("port"))
        val properties: Array[QueueProperties] = structuresSequence("properties")
      }
    }
  }

  private class QueueGetConfigRequestBuilder extends OfpMessageBuilder[QueueGetConfigRequestInput]{
    // Fills the underlying builder with the specified input.
    override protected def applyInput(input: QueueGetConfigRequestInput): Unit = {
      super.applyInput(input)
      setMember("port", input.port.number)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): QueueGetConfigRequestInput = QueueGetConfigRequestInput(PortNumber("port"))
  }

  private class QueueGetConfigReplyStructure(s: DynamicStructure) extends OfpMessage[QueueGetConfigReply](s) with QueueGetConfigReply {
    def port: PortNumber = new PortNumber(primitiveField[UInt]("port"))
    def queues: Array[PacketQueue] = structuresSequence("queues")
  }

  protected abstract override def builderClasses = classOf[QueueGetConfigRequestBuilder] :: super.builderClasses
  protected abstract override def messageClasses = classOf[QueueGetConfigReplyStructure] :: super.messageClasses
}