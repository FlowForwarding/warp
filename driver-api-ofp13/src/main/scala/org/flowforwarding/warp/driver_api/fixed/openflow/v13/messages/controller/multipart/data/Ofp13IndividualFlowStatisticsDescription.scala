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
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart.{SingletonMultipartRequestBodyInput, MultipartRequestBodyInput, MultipartReplyBody}
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.mod.FlowModFlags
import org.flowforwarding.warp.driver_api.fixed.text_view.BITextView

case class IndividualFlowStatisticsRequest(
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

case class IndividualFlowStatisticsRequestBodyInput(structure: IndividualFlowStatisticsRequest) extends SingletonMultipartRequestBodyInput[IndividualFlowStatisticsRequest]

trait IndividualFlowStatistics{
  /** ID of table flow came from. */
  val tableId: UShort
  /** Time flow has been alive in seconds. */
  val durationSeconds: ULong
  /** Time flow has been alive in nanoseconds beyond duration_sec. */
  val durationNanoseconds: ULong
  /** Priority of the entry. */
  val priority: UInt
  /** Number of seconds idle before expiration. */
  val idleTimeout: UInt
  /** Number of seconds before expiration. */
  val hardTimeout: UInt
  /** Bitmap of OFPFF_* flags. */
  val flags: FlowModFlags
  /** Opaque controller-issued identifier. */
  val cookie: ULong
  /** Number of packets in flow. */
  val packetCount: ULong
  /** Number of bytes in flow. */
  val byteCount: ULong
  /** Description of fields. Variable size. */
  val fMatch: Match
}

trait IndividualFlowStatisticsReplyBody extends MultipartReplyBody[Array[IndividualFlowStatistics]]

trait IndividualFlowStatisticsReplyHandler{
  def onIndividualFlowStatisticsReply(dpid: ULong, msg: Array[IndividualFlowStatistics]): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13IndividualFlowStatisticsDescription extends StructureDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _] with IndividualFlowStatisticsReplyHandler] with Ofp13MatchDescription =>

  implicit object IndividualFlowStatistics extends FromDynamic[IndividualFlowStatistics]{
    val fromDynamic: PartialFunction[DynamicStructure, IndividualFlowStatistics] = {
      case s if s.ofType[IndividualFlowStatistics] => new OfpStructure[IndividualFlowStatistics](s) with IndividualFlowStatistics{
        val tableId             = primitiveField[UShort]("table_id")
        val durationSeconds     = primitiveField[ULong]("duration_sec")
        val durationNanoseconds = primitiveField[ULong]("duration_nsec")
        val priority            = primitiveField[UInt]("priority")
        val idleTimeout         = primitiveField[UInt]("idle_timeout")
        val hardTimeout         = primitiveField[UInt]("hard_timeout")
        val flags               = bitmapField("flags")
        val cookie: ULong       = primitiveField[ULong]("cookie")
        val packetCount         = primitiveField[ULong]("packet_count")
        val byteCount           = primitiveField[ULong]("byte_count")
        val fMatch              = structureField[Match]("match")
      }
    }
  }
  private class IndividualFlowStatisticsRequestBuilder extends OfpStructureBuilder[IndividualFlowStatisticsRequest]{
    // Fills the underlying builder with the specified input.
    protected def applyInput(input: IndividualFlowStatisticsRequest): Unit = {
      setMember("table_id", input.tableId)
      setMember("out_port", input.outPort.number)
      setMember("out_group", input.outGroup.id)
      setMember("cookie", input.cookie)
      setMember("cookie_mask", input.cookieMask)
      setMember("match", input.rMatch)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): IndividualFlowStatisticsRequest =
      new IndividualFlowStatisticsRequest("table_id",
                                          PortNumber("out_port"),
                                          GroupId("out_group"),
                                          "cookie",
                                          "cookie_mask",
                                          structure[MatchInput]("match"))
  }

  protected[fixed] implicit object IndividualFlowStatisticsRequest extends ToDynamic[IndividualFlowStatisticsRequest]{
    val toDynamic: PartialFunction[IndividualFlowStatisticsRequest, DynamicBuilderInput] = {
      case s => new IndividualFlowStatisticsRequestBuilder toDynamicInput s
    }
  }

  protected class IndividualFlowStatisticsRequestBodyInputBuilder extends OfpStructureBuilder[IndividualFlowStatisticsRequestBodyInput] {
    protected def applyInput(input: IndividualFlowStatisticsRequestBodyInput): Unit = {
      setMember("structure", input.structure)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): IndividualFlowStatisticsRequestBodyInput =
      IndividualFlowStatisticsRequestBodyInput(structure[IndividualFlowStatisticsRequest]("structure"))
  }

  abstract override def builderClasses = classOf[IndividualFlowStatisticsRequestBodyInputBuilder] :: super.builderClasses
}