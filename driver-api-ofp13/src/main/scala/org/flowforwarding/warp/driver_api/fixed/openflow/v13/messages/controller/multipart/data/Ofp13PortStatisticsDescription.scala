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

case class PortStatisticsRequest(portNumber: PortNumber) extends BuilderInput

case class PortStatisticsRequestBodyInput(structure: PortStatisticsRequest) extends SingletonMultipartRequestBodyInput[PortStatisticsRequest]

trait PortStatistics{
  /** port number. */
  val portNumber: PortNumber
  /** Number of received packets. */
  val receivedPackets: ULong
  /** Number of transmitted packets. */
  val transmittedPackets: ULong
  /** Number of received bytes. */
  val receivedBytes: ULong
  /** Number of transmitted bytes. */
  val transmittedBytes: ULong
  /** Number of packets dropped by RX. */
  val receivedDropped: ULong
  /** Number of packets dropped by TX. */
  val transmittedDropped: ULong
  /** Number of receive errors. This is a super-set
      of more specific receive errors and should be
      greater than or equal to the sum of all
      rx_*_err values. */
  val receiveErrors: ULong
  /** Number of transmit errors. This is a super-set
      of more specific transmit errors and should be
      greater than or equal to the sum of all
      tx_*_err values (none currently defined.) */
  val transmittedErrors: ULong
  /** Number of frame alignment errors. */
  val frameAlignmentErrors: ULong
  /** Number of packets with RX overrun. */
  val overrunErrors: ULong
  /** Number of CRC errors. */
  val crcErrors: ULong
  /** Number of collisions. */
  val collisions: ULong
  /** Time port has been alive in seconds. */
  val durationSeconds: UInt
  /** Time port has been alive in nanoseconds beyond duration_sec. */
  val durationNanoseconds: UInt
}

trait PortStatisticsReplyBody extends MultipartReplyBody[PortStatistics]

trait PortStatisticsReplyHandler{
  def onPortStatisticsReply(dpid: ULong, msg: PortStatistics): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13PortStatisticsDescription extends StructureDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _] with PortStatisticsReplyHandler] =>

  implicit object PortStatistics extends FromDynamic[PortStatistics]{
    val fromDynamic: PartialFunction[DynamicStructure, PortStatistics] = {
      case s if s.ofType[PortStatistics] => new OfpStructure[PortStatistics](s) with PortStatistics{
        val portNumber           = PortNumber(primitiveField[UInt]("port_no"))
        val receivedPackets      = primitiveField[ULong]("rx_packets")
        val transmittedPackets   = primitiveField[ULong]("tx_packets")
        val receivedBytes        = primitiveField[ULong]("rx_bytes")
        val transmittedBytes     = primitiveField[ULong]("tx_bytes")
        val receivedDropped      = primitiveField[ULong]("rx_dropped")
        val transmittedDropped   = primitiveField[ULong]("tx_dropped")
        val receiveErrors        = primitiveField[ULong]("rx_errors")
        val transmittedErrors    = primitiveField[ULong]("tx_errors")
        val frameAlignmentErrors = primitiveField[ULong]("rx_frame_err")
        val overrunErrors        = primitiveField[ULong]("rx_over_err")
        val crcErrors            = primitiveField[ULong]("rx_crc_err")
        val collisions           = primitiveField[ULong]("collisions")
        val durationSeconds      = primitiveField[UInt]("duration_sec")
        val durationNanoseconds  = primitiveField[UInt]("duration_nsec")
      }
    }
  }

  private class PortStatisticsRequestBuilder extends OfpStructureBuilder[PortStatisticsRequest]{
    // Fills the underlying builder with the specified input.
    protected def applyInput(input: PortStatisticsRequest): Unit = {
      setMember("port_no", input.portNumber.number)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): PortStatisticsRequest =
      new PortStatisticsRequest(PortNumber("port_no"))
  }

  protected[fixed] implicit object PortStatisticsRequest extends ToDynamic[PortStatisticsRequest]{
    val toDynamic: PartialFunction[PortStatisticsRequest, DynamicBuilderInput] = {
      case s => new PortStatisticsRequestBuilder toDynamicInput s
    }
  }

  protected class PortStatisticsRequestBodyInputBuilder extends OfpStructureBuilder[PortStatisticsRequestBodyInput] {
    protected def applyInput(input: PortStatisticsRequestBodyInput): Unit = {
      setMember("structure", input.structure)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): PortStatisticsRequestBodyInput =
      PortStatisticsRequestBodyInput(structure[PortStatisticsRequest]("structure"))
  }

  abstract override def builderClasses = classOf[PortStatisticsRequestBodyInputBuilder] :: super.builderClasses
}