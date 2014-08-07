package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart

import spire.math._

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.util._
import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

trait MeterStatisticsReply extends Ofp13MultipartMultiValueMessage[MeterStatistics]

trait MeterStatistics{
  val meterId: MeterId               
  val flowCount: UInt                   /* Number of flows bound to meter. */
  val packetInCount: ULong              /* Number of packets in input. */
  val byteInCount: ULong                /* Number of bytes in input. */
  val durationSeconds: ULong            /* Time meter has been alive in seconds. */
  val durationNanoseconds: ULong        /* Time meter has been alive in nanoseconds beyond duration_sec. */
  val bandStats: Array[MeterBandStats]  /* The band_stats length is inferred from the length field. */
}

trait MeterBandStats {
  val packetBandCount: ULong
  val byteBandCount: ULong
}

case class MeterStatisticsRequestBody(meterId: MeterId) extends BuilderInput

case class MeterStatisticsRequestInput(reqMore: Boolean, body: MeterStatisticsRequestBody)
  extends MultipartMessageWithBodyRequestInput[MeterStatisticsRequestBody]

trait MeterStatisticsReplyHandler{
  def onMeterStatisticsReply(dpid: ULong, msg: MeterStatisticsReply): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13MeterStatisticsDescription extends Ofp13MultipartMessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with MeterStatisticsReplyHandler] with Ofp13HeaderDescription with Ofp13MatchDescription =>


  implicit object MeterStatisticsRequestBody extends ToDynamic[MeterStatisticsRequestBody]{
    def toDynamic: PartialFunction[MeterStatisticsRequestBody, DynamicBuilderInput] = {
      case b => new MeterStatisticsRequestBodyBuilder toDynamicInput b
    }
  }

  implicit object MeterBandStats extends FromDynamic[MeterBandStats]{
    def fromDynamic: PartialFunction[DynamicStructure, MeterBandStats] = {
      case s if s.ofType[MeterBandStats] => new OfpStructure[MeterBandStats](s) with MeterBandStats {
        val byteBandCount: ULong = primitiveField[ULong]("byte_band_count")
        val packetBandCount: ULong = primitiveField[ULong]("packet_band_count")
      }
    }
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

  private class MeterStatisticsRequestBodyBuilder extends OfpStructureBuilder[MeterStatisticsRequestBody]{
    // Fills the underlying builder with the specified input.
    protected def applyInput(input: MeterStatisticsRequestBody): Unit = {
      setMember("meter_id", input.meterId.id)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): MeterStatisticsRequestBody = new MeterStatisticsRequestBody(MeterId("meter_id"))
  }

  class MeterStatisticsRequestBuilder
    extends Ofp13MultipartMessageWithBodyRequestBuilder[MeterStatisticsRequestBody, MeterStatisticsRequestInput]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): MeterStatisticsRequestInput =
      MeterStatisticsRequestInput("flags", structure[MeterStatisticsRequestBody]("body"))
  }

  case class MeterStatisticsReplyStructure(s: DynamicStructure)
    extends OfpMultipartMultiValueMessage[MeterStatisticsReply, MeterStatistics](s) with MeterStatisticsReply

  abstract override def builderClasses = classOf[MeterStatisticsRequestBuilder] :: classOf[MeterStatisticsRequestBodyBuilder] :: super.builderClasses
  abstract override def messageClasses = classOf[MeterStatisticsReplyStructure] :: super.messageClasses
}