/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart.data

import org.flowforwarding.warp.driver_api.fixed.SpecificVersionMessageHandlersSet
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart.{SingletonMultipartRequestBodyInput, MultipartReplyBody, EmptyMultipartRequestBodyInput}
import spire.math._

import org.flowforwarding.warp.driver_api.dynamic._
import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.fixed.util._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures._
import org.flowforwarding.warp.driver_api.fixed.text_view.BITextView

case class MeterStatisticsRequest(meterId: MeterId) extends BuilderInput

case class MeterStatisticsRequestBodyInput(structure: MeterStatisticsRequest) extends SingletonMultipartRequestBodyInput[MeterStatisticsRequest]

trait MeterStatistics{
  /** Meter id. */
  val meterId: MeterId
  /** Number of flows bound to meter. */
  val flowCount: UInt
  /** Number of packets in input. */
  val packetInCount: ULong
  /** Number of bytes in input. */
  val byteInCount: ULong
  /** Time meter has been alive in seconds. */
  val durationSeconds: ULong
  /** Time meter has been alive in nanoseconds beyond duration_sec. */
  val durationNanoseconds: ULong
  /** The band_stats length is inferred from the length field. */
  val bandStats: Array[MeterBandStats]
}

trait MeterBandStats {
  val packetBandCount: ULong
  val byteBandCount: ULong
}

trait MeterStatisticsReplyBody extends MultipartReplyBody[Array[MeterStatistics]]

trait MeterStatisticsReplyHandler{
  def onMeterStatisticsReply(dpid: ULong, msg: Array[MeterStatistics]): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13MeterStatisticsDescription extends StructureDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _] with MeterStatisticsReplyHandler] with Ofp13MatchDescription =>

  implicit object MeterBandStats extends FromDynamic[MeterBandStats]{
    def fromDynamic: PartialFunction[DynamicStructure, MeterBandStats] = {
      case s if s.ofType[MeterBandStats] => new OfpStructure[MeterBandStats](s) with MeterBandStats {
        val byteBandCount: ULong = primitiveField[ULong]("byte_band_count")
        val packetBandCount: ULong = primitiveField[ULong]("packet_band_count")
      }
    }
  }

  private class MeterStatisticsRequestBuilder extends OfpStructureBuilder[MeterStatisticsRequest]{
    // Fills the underlying builder with the specified input.
    protected def applyInput(input: MeterStatisticsRequest): Unit = {
      setMember("meter_id", input.meterId.id)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView) = new MeterStatisticsRequest(MeterId("meter_id"))
  }

  implicit object MeterStatistics extends FromDynamic[MeterStatistics]{
    val fromDynamic: PartialFunction[DynamicStructure, MeterStatistics] = {
      case s if s.ofType[MeterStatistics] => new OfpStructure[MeterStatistics](s) with MeterStatistics{
        val meterId             = MeterId(primitiveField[UInt]("meter_id"))
        val flowCount           = primitiveField[UInt]("flow_count")
        val packetInCount       = primitiveField[ULong]("packet_in_count")
        val byteInCount         = primitiveField[ULong]("byte_in_count")
        val durationSeconds     = primitiveField[ULong]("duration_sec")
        val durationNanoseconds = primitiveField[ULong]("duration_nsec")
        val bandStats           = structuresSequence[MeterBandStats]("band_stats")
      }
    }
  }

  protected[fixed] implicit object MeterStatisticsRequest extends ToDynamic[MeterStatisticsRequest]{
    val toDynamic: PartialFunction[MeterStatisticsRequest, DynamicBuilderInput] = {
      case s => new MeterStatisticsRequestBuilder toDynamicInput s
    }
  }

  protected class MeterStatisticsRequestBodyInputBuilder extends OfpStructureBuilder[MeterStatisticsRequestBodyInput]{
    // Fills the underlying builder with the specified input.
    protected def applyInput(input: MeterStatisticsRequestBodyInput): Unit = {
      setMember("structure", input.structure)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): MeterStatisticsRequestBodyInput =
      new MeterStatisticsRequestBodyInput(structure[MeterStatisticsRequest]("structure"))
  }

  abstract override def builderClasses = classOf[MeterStatisticsRequestBodyInputBuilder] :: super.builderClasses
}