/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller

import spire.math._

import java.nio.ByteBuffer

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.Ofp13HeaderDescription
import org.flowforwarding.warp.controller.api.fixed.v13.messages.{Ofp13MessageInput, Ofp13Message, Ofp13MessageDescription}
import org.flowforwarding.warp.controller.api.fixed.util.ByteBufferExt
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

trait GetAsyncReply extends Ofp13Message{
  val packetInMask1: UInt
  val packetInMask2: UInt
  val portStatusMask1: UInt
  val portStatusMask2: UInt
  val flowRemovedMask1: UInt
  val flowRemovedMask2: UInt
}

case class SetAsyncInput(packetInMask1: UInt, packetInMask2: UInt,
                         portStatusMask1: UInt, portStatusMask2: UInt,
                         flowRemovedMask1: UInt, flowRemovedMask2: UInt) extends Ofp13MessageInput

case class GetAsyncRequestInput() extends Ofp13MessageInput

private[fixed] trait GetAsyncReplyHandler{
  def onGetAsyncReply(dpid: ULong, msg: GetAsyncReply): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13AsyncDescription extends Ofp13MessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with GetAsyncReplyHandler] with Ofp13HeaderDescription =>

  private class GetAsyncRequestBuilder extends OfpMessageBuilder[GetAsyncRequestInput]{
    def inputFromTextView(implicit input: BITextView): GetAsyncRequestInput = GetAsyncRequestInput()
  }

  private class SetAsyncBuilder extends OfpMessageBuilder[SetAsyncInput]{
    // Fills the underlying builder with the specified input.
    override protected def applyInput(input: SetAsyncInput): Unit = {
      super.applyInput(input)
      setMember("packet_in_mask", ULong(input.packetInMask1.toLong | (input.packetInMask2.toLong << 32)))
      setMember("port_status_mask", ULong(input.portStatusMask1.toLong | (input.portStatusMask2.toLong << 32)))
      setMember("flow_removed_mask", ULong(input.flowRemovedMask1.toLong | (input.flowRemovedMask2.toLong << 32)))
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): SetAsyncInput =
      new SetAsyncInput("packet_in_mask_1", "packet_in_mask_2",
                        "port_status_mask_1", "port_status_mask_2",
                        "flow_removed_mask_1", "flow_removed_mask_2")
  }

  private class GetAsyncReplyStructure(s: DynamicStructure) extends OfpMessage[GetAsyncReply](s) with GetAsyncReply {
    private val packetIn    = ByteBuffer.allocate(8).putLong(primitiveField[ULong]("packet_in_mask").signed)
    private val portStatus  = ByteBuffer.allocate(8).putLong(primitiveField[ULong]("port_status_mask").signed)
    private val flowRemoved = ByteBuffer.allocate(8).putLong(primitiveField[ULong]("flow_removed_mask").signed)

    val  packetInMask1 = packetIn.getUInt(4)
    val  packetInMask2 = packetIn.getUInt(0)
    val  portStatusMask1 = portStatus.getUInt(4)
    val  portStatusMask2 = portStatus.getUInt(0)
    val  flowRemovedMask1 = flowRemoved.getUInt(4)
    val  flowRemovedMask2 = flowRemoved.getUInt(0)
  }

  protected abstract override def builderClasses = classOf[SetAsyncBuilder] :: classOf[GetAsyncRequestBuilder] :: super.builderClasses
  protected abstract override def messageClasses = classOf[GetAsyncReplyStructure] :: super.messageClasses
}