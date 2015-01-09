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

case class AggregateFlowStatisticsRequest(
  /** ID of table to read (from ofp_table_stats),
    * OFPTT_ALL for all tables.*/
  tableId: UShort,
  /** Require matching entries to include this as an output port.
    * A value of OFPP_ANY indicates no restriction. */
  outPort: PortNumber,
  /** Require matching entries to include this as an output group.
    * A value of OFPG_ANY indicates no restriction. */
  outGroup: GroupId,
  /** Require matching entries to contain this cookie value. */
  cookie: ULong,
  /** Mask used to restrict the cookie bits that must match. A value of 0 indicates no restriction. */
  cookieMask: ULong,
  /** Fields to match. */
  rMatch: MatchInput) extends BuilderInput

case class AggregateFlowStatisticsRequestBodyInput(structure: AggregateFlowStatisticsRequest) extends SingletonMultipartRequestBodyInput[AggregateFlowStatisticsRequest]

trait AggregateFlowStatistics{
  val packetCount: ULong
  val byteCount: ULong
  val flowCount: UInt
}

trait AggregateFlowStatisticsReplyBody extends MultipartReplyBody[AggregateFlowStatistics]

trait AggregateFlowStatisticsReplyHandler{
  def onAggregateFlowStatisticsReply(dpid: ULong, msg: AggregateFlowStatistics): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13AggregateFlowStatisticsDescription extends StructureDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _] with AggregateFlowStatisticsReplyHandler] with Ofp13MatchDescription =>

  implicit object AggregateFlowStatistics extends FromDynamic[AggregateFlowStatistics]{
    val fromDynamic: PartialFunction[DynamicStructure, AggregateFlowStatistics] = {
      case s if s.ofType[AggregateFlowStatistics] => new OfpStructure[AggregateFlowStatistics](s) with AggregateFlowStatistics{
        val packetCount = primitiveField[ULong]("packet_count")
        val byteCount   = primitiveField[ULong]("byte_count")
        val flowCount   = primitiveField[UInt]("flow_count")
      }
    }
  }

  private class AggregateFlowStatisticsRequestBuilder extends OfpStructureBuilder[AggregateFlowStatisticsRequest]{
    // Fills the underlying builder with the specified input.
    protected def applyInput(input: AggregateFlowStatisticsRequest): Unit = {
      setMember("table_id", input.tableId)
      setMember("out_port", input.outPort.number)
      setMember("out_group", input.outGroup.id)
      setMember("cookie", input.cookie)
      setMember("cookie_mask", input.cookieMask)
      setMember("match", input.rMatch)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): AggregateFlowStatisticsRequest =
      new AggregateFlowStatisticsRequest("table_id",
                                         PortNumber("out_port"),
                                         GroupId("out_group"),
                                         "cookie",
                                         "cookie_mask",
                                         structure[MatchInput]("match"))
  }

  protected[fixed] implicit object AggregateFlowStatisticsRequest extends ToDynamic[AggregateFlowStatisticsRequest]{
    val toDynamic: PartialFunction[AggregateFlowStatisticsRequest, DynamicBuilderInput] = {
      case s => new AggregateFlowStatisticsRequestBuilder toDynamicInput s
    }
  }

  protected class AggregateFlowStatisticsRequestBodyInputBuilder extends OfpStructureBuilder[AggregateFlowStatisticsRequestBodyInput] {
    protected def applyInput(input: AggregateFlowStatisticsRequestBodyInput): Unit = {
      setMember("structure", input.structure)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): AggregateFlowStatisticsRequestBodyInput =
      AggregateFlowStatisticsRequestBodyInput(structure[AggregateFlowStatisticsRequest]("structure"))
  }

  abstract override def builderClasses = classOf[AggregateFlowStatisticsRequestBodyInputBuilder] :: super.builderClasses
}