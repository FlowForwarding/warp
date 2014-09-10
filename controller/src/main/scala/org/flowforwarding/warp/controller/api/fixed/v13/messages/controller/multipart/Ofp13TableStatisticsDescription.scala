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

trait TableStatisticsReply extends Ofp13MultipartMultiValueMessage[TableStatistics]

trait TableStatistics{
  val tableId: UShort      /* Identifier of table. Lower numbered tables are consulted first. */
  val activeCount: ULong   /* Number of active entries. */
  val lookupCount: ULong   /* Number of packets looked up in table. */
  val matchedCount: ULong  /* Number of packets that hit table. */
}

case class TableStatisticsRequestInput(reqMore: Boolean) extends MultipartMessageEmptyBodyRequestInput

trait TableStatisticsReplyHandler{
  def onTableStatisticsReply(dpid: ULong, msg: TableStatisticsReply): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13TableStatisticsDescription extends Ofp13MultipartMessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with TableStatisticsReplyHandler] with Ofp13HeaderDescription =>

  implicit object TableStatistics extends FromDynamic[TableStatistics]{
    val fromDynamic: PartialFunction[DynamicStructure, TableStatistics] = {
      case s if s.ofType[SwitchDescription] => new OfpStructure[TableStatistics](s) with TableStatistics {
        val tableId      = primitiveField[UShort]("table_id")
        val activeCount  = primitiveField[ULong]("active_count")
        val lookupCount  = primitiveField[ULong]("lookup_count")
        val matchedCount = primitiveField[ULong]("matched_count")
      }
    }
  }

  class TableStatisticsRequestBuilder extends Ofp13MultipartMessageEmptyBodyRequestBuilder[TableStatisticsRequestInput]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): TableStatisticsRequestInput = TableStatisticsRequestInput("flags")
  }

  case class TableStatisticsReplyStructure(s: DynamicStructure)
    extends OfpMultipartMultiValueMessage[TableStatisticsReply, TableStatistics](s) with TableStatisticsReply

  abstract override def builderClasses = classOf[TableStatisticsRequestBuilder] :: super.builderClasses
  abstract override def messageClasses = classOf[TableStatisticsReplyStructure] :: super.messageClasses
}