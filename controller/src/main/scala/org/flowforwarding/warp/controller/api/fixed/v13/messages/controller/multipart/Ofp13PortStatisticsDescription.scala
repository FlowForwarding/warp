/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart

import spire.math._

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.util._
import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

trait PortStatisticsReply extends Ofp13MultipartMultiValueMessage[PortStatistics]

trait PortStatistics{
  val portNumber: PortNumber
  val receivedPackets: ULong       /* Number of received packets. */
  val transmittedPackets: ULong    /* Number of transmitted packets. */
  val receivedBytes: ULong         /* Number of received bytes. */
  val transmittedBytes: ULong      /* Number of transmitted bytes. */
  val receivedDropped: ULong       /* Number of packets dropped by RX. */
  val transmittedDropped: ULong    /* Number of packets dropped by TX. */
  val receiveErrors: ULong         /* Number of receive errors. This is a super-set
                                     of more specific receive errors and should be
                                     greater than or equal to the sum of all
                                     rx_*_err values. */
  val transmittedErrors: ULong    /* Number of transmit errors. This is a super-set
                                     of more specific transmit errors and should be
                                     greater than or equal to the sum of all
                                     tx_*_err values (none currently defined.) */
  val frameAlignmentErrors: ULong  /* Number of frame alignment errors. */
  val overrunErrors: ULong         /* Number of packets with RX overrun. */
  val crcErrors: ULong             /* Number of CRC errors. */
  val collisions: ULong            /* Number of collisions. */
  val durationSeconds: UInt        /* Time port has been alive in seconds. */
  val durationNanoseconds: UInt    /* Time port has been alive in nanoseconds beyond duration_sec. */
}

case class PortStatisticsRequestBody(portNumber: PortNumber) extends BuilderInput

case class PortStatisticsRequestInput(reqMore: Boolean, body: PortStatisticsRequestBody)
  extends MultipartMessageWithBodyRequestInput[PortStatisticsRequestBody]

trait PortStatisticsReplyHandler{
  def onPortStatisticsReply(dpid: ULong, msg: PortStatisticsReply): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13PortStatisticsDescription extends Ofp13MultipartMessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with PortStatisticsReplyHandler] with Ofp13HeaderDescription =>

  implicit object PortStatisticsRequestBody extends ToDynamic[PortStatisticsRequestBody]{
    def toDynamic: PartialFunction[PortStatisticsRequestBody, DynamicBuilderInput] = {
      case b => new PortStatisticsRequestBodyBuilder toDynamicInput b
    }
  }

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

  private class PortStatisticsRequestBodyBuilder extends OfpStructureBuilder[PortStatisticsRequestBody]{
    // Fills the underlying builder with the specified input.
    protected def applyInput(input: PortStatisticsRequestBody): Unit = {
      setMember("port_no", input.portNumber.number)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): PortStatisticsRequestBody = new PortStatisticsRequestBody(PortNumber("port_no"))
  }

  class PortStatisticsRequestBuilder
    extends Ofp13MultipartMessageWithBodyRequestBuilder[PortStatisticsRequestBody, PortStatisticsRequestInput]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): PortStatisticsRequestInput =
      PortStatisticsRequestInput("flags", structure[PortStatisticsRequestBody]("body"))
  }

  case class PortStatisticsReplyStructure(s: DynamicStructure)
    extends OfpMultipartMultiValueMessage[PortStatisticsReply, PortStatistics](s) with PortStatisticsReply

  abstract override def builderClasses = classOf[PortStatisticsRequestBuilder] :: classOf[PortStatisticsRequestBodyBuilder] :: super.builderClasses
  abstract override def messageClasses = classOf[PortStatisticsReplyStructure] :: super.messageClasses
}