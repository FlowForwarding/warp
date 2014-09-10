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

case class FlowStatisticsRequestBody(
  tableId: UShort, 	     // ID of table to read
  outPort: PortNumber,   // Require matching entries to include this as an output port
  outGroup: GroupId,     // Require matching entries to include this as an output group
  cookie: ULong,	         // Require matching entries to contain this cookie value
  cookieMask: ULong,	     // Mask used to restrict the cookie bits that must match
  rMatch: MatchInput) extends BuilderInput

private[fixed] trait Ofp13FlowStatisticsDescription extends Ofp13MultipartMessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _]] with Ofp13HeaderDescription with Ofp13MatchDescription =>

  private class FlowStatisticsRequestBodyBuilder extends OfpStructureBuilder[FlowStatisticsRequestBody]{
    // Fills the underlying builder with the specified input.
    protected def applyInput(input: FlowStatisticsRequestBody): Unit = {
      setMember("table_id", input.tableId)
      setMember("out_port", input.outPort.number)
      setMember("out_group", input.outGroup.id)
      setMember("cookie", input.cookie)
      setMember("cookie_mask", input.cookieMask)
      setMember("match", input.rMatch)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): FlowStatisticsRequestBody =
      new FlowStatisticsRequestBody("table_id",
                                    PortNumber("out_port"),
                                    GroupId("out_group"),
                                    "cookie",
                                    "cookie_mask",
                                    structure[MatchInput]("match"))
  }

  implicit object FlowStatisticsRequestBody extends ToDynamic[FlowStatisticsRequestBody]{
    def toDynamic: PartialFunction[FlowStatisticsRequestBody, DynamicBuilderInput] = {
      case b => new FlowStatisticsRequestBodyBuilder toDynamicInput b
    }
  }

  abstract override def builderClasses = classOf[FlowStatisticsRequestBodyBuilder] :: super.builderClasses
}