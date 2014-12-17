/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart.data

import org.flowforwarding.warp.driver_api.fixed.SpecificVersionMessageHandlersSet
import spire.math._

import org.flowforwarding.warp.driver_api.dynamic._
import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.fixed.util._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart.{SingletonMultipartRequestBodyInput, MultipartReplyBody}
import org.flowforwarding.warp.driver_api.fixed.text_view.BITextView

case class QueueStatisticsRequest(
  /** Queue id. All ports if OFPP_ANY. */
  portNumber: PortNumber,
  /** Queue id. All queues if OFPQ_ALL. */
  queueId: UInt) extends BuilderInput


case class QueueStatisticsRequestBodyInput(structure: QueueStatisticsRequest) extends SingletonMultipartRequestBodyInput[QueueStatisticsRequest]

trait QueueStatistics{
  /** Queue id. */
  val portNumber: PortNumber
  /** Queue id. */
  val queueId: UInt
  /** Number of transmitted packets. */
  val transmittedPackets: ULong
  /** Number of transmitted bytes. */
  val transmittedBytes: ULong
  /** Number of transmit errors. This is a super-set
      of more specific transmit errors and should be
      greater than or equal to the sum of all
      tx_*_err values (none currently defined.) */
  val transmittedErrors: ULong
  /** Time port has been alive in seconds. */
  val durationSeconds: UInt
  /** Time port has been alive in nanoseconds beyond duration_sec. */
  val durationNanoseconds: UInt
}

trait QueueStatisticsReplyBody extends MultipartReplyBody[Array[QueueStatistics]]

trait QueueStatisticsReplyHandler{
  def onQueueStatisticsReply(dpid: ULong, msg: Array[QueueStatistics]): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13QueueStatisticsDescription extends StructureDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _] with QueueStatisticsReplyHandler] =>

  implicit object QueueStatistics extends FromDynamic[QueueStatistics]{
    val fromDynamic: PartialFunction[DynamicStructure, QueueStatistics] = {
      case s if s.ofType[QueueStatistics] => new OfpStructure[QueueStatistics](s) with QueueStatistics{
        val portNumber          = PortNumber(primitiveField[UInt]("port_no"))
        val queueId             = primitiveField[UInt]("queue_id")
        val transmittedPackets  = primitiveField[ULong]("tx_packets")
        val transmittedBytes    = primitiveField[ULong]("tx_bytes")
        val transmittedErrors   = primitiveField[ULong]("tx_errors")
        val durationSeconds     = primitiveField[UInt]("duration_sec")
        val durationNanoseconds = primitiveField[UInt]("duration_nsec")
      }
    }
  }

  private class QueueStatisticsRequestBuilder extends OfpStructureBuilder[QueueStatisticsRequest]{
    // Fills the underlying builder with the specified input.
    protected def applyInput(input: QueueStatisticsRequest): Unit = {
      setMember("port_no", input.portNumber.number)
      setMember("queue_id", input.queueId)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): QueueStatisticsRequest =
      new QueueStatisticsRequest(PortNumber("port_no"), "queue_id")
  }

  protected[fixed] implicit object QueueStatisticsRequest extends ToDynamic[QueueStatisticsRequest]{
    val toDynamic: PartialFunction[QueueStatisticsRequest, DynamicBuilderInput] = {
      case s => new QueueStatisticsRequestBuilder toDynamicInput s
    }
  }

  protected class QueueStatisticsRequestBodyInputBuilder extends OfpStructureBuilder[QueueStatisticsRequestBodyInput] {
    protected def applyInput(input: QueueStatisticsRequestBodyInput): Unit = {
      setMember("structure", input.structure)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): QueueStatisticsRequestBodyInput =
      QueueStatisticsRequestBodyInput(structure[QueueStatisticsRequest]("structure"))
  }

  abstract override def builderClasses = classOf[QueueStatisticsRequestBodyInputBuilder] :: super.builderClasses
}