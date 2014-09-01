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

trait AggregateFlowStatisticsReply extends Ofp13MultipartSingleValueMessage[AggregateFlowStatistics]

trait AggregateFlowStatistics{
  val packetCount: ULong
  val byteCount: ULong
  val flowCount: ULong
}

case class AggregateFlowStatisticsRequestInput(reqMore: Boolean, body: FlowStatisticsRequestBody)
  extends MultipartMessageWithBodyRequestInput[FlowStatisticsRequestBody]

trait AggregateFlowStatisticsReplyHandler{
  def onAggregateFlowStatisticsReply(dpid: ULong, msg: AggregateFlowStatisticsReply): Array[BuilderInput] = Array.empty[BuilderInput]
}


private[fixed] trait Ofp13AggregateFlowStatisticsDescription extends Ofp13FlowStatisticsDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with AggregateFlowStatisticsReplyHandler] with Ofp13HeaderDescription with Ofp13MatchDescription =>

  implicit object AggregateFlowStatistics extends FromDynamic[AggregateFlowStatistics]{
    val fromDynamic: PartialFunction[DynamicStructure, AggregateFlowStatistics] = {
      case s if s.ofType[AggregateFlowStatistics] => new OfpStructure[AggregateFlowStatistics](s) with AggregateFlowStatistics{
        val packetCount = primitiveField[ULong]("packet_count")
        val byteCount   = primitiveField[ULong]("byte_count")
        val flowCount   = primitiveField[ULong]("flow_count")
      }
    }
  }

  class AggregateFlowStatisticsRequestBuilder
    extends Ofp13MultipartMessageWithBodyRequestBuilder[FlowStatisticsRequestBody, AggregateFlowStatisticsRequestInput]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): AggregateFlowStatisticsRequestInput =
      AggregateFlowStatisticsRequestInput("flags", structure[FlowStatisticsRequestBody]("body"))
  }

  case class AggregateFlowStatisticsReplyStructure(s: DynamicStructure)
    extends OfpMultipartSingleValueMessage[AggregateFlowStatisticsReply, AggregateFlowStatistics](s) with AggregateFlowStatisticsReply

  abstract override def builderClasses = classOf[AggregateFlowStatisticsRequestBuilder] :: super.builderClasses
  abstract override def messageClasses = classOf[AggregateFlowStatisticsReplyStructure] :: super.messageClasses
}