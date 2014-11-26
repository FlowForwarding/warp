/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.data

import spire.math._

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.util._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.{MultipartReplyBody, EmptyMultipartRequestBodyInput}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView


trait TableStatistics{
  /** Identifier of table. Lower numbered tables are consulted first. */
  val tableId: UByte
  /** Number of active entries. */
  val activeCount: UInt
  /** Number of packets looked up in table. */
  val lookupCount: ULong
  /** Number of packets that hit table. */
  val matchedCount: ULong
}

case class TableStatisticsRequestBodyInput() extends EmptyMultipartRequestBodyInput

trait TableStatisticsReplyBody extends MultipartReplyBody[Array[TableStatistics]]

trait TableStatisticsReplyHandler{
  def onTableStatisticsReply(dpid: ULong, msg: Array[TableStatistics]): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13TableStatisticsDescription extends StructureDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with TableStatisticsReplyHandler] =>

  implicit object TableStatistics extends FromDynamic[TableStatistics]{
    val fromDynamic: PartialFunction[DynamicStructure, TableStatistics] = {
      case s if s.ofType[TableStatistics] => new OfpStructure[TableStatistics](s) with TableStatistics {
        val tableId      = primitiveField[UByte]("table_id")
        val activeCount  = primitiveField[UInt]("active_count")
        val lookupCount  = primitiveField[ULong]("lookup_count")
        val matchedCount = primitiveField[ULong]("matched_count")
      }
    }
  }

  class TableStatisticsRequestBodyInputBuilder extends OfpStructureBuilder[TableStatisticsRequestBodyInput]{
    protected def applyInput(input: TableStatisticsRequestBodyInput): Unit = { }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableStatisticsRequestBodyInput = TableStatisticsRequestBodyInput()
  }

  abstract override def builderClasses = classOf[TableStatisticsRequestBodyInputBuilder] :: super.builderClasses
}