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
import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

trait SwitchDescriptionReply extends Ofp13MultipartSingleValueMessage[SwitchDescription]

trait SwitchDescription{
  val manufacturer: String /* Manufacturer description. */
  val hardware: String     /* Hardware description. */
  val software: String     /* Software description. */
  val serialNumber: String /* Serial number. */
  val datapath: String     /* Human readable description of datapath. */
}

case class SwitchDescriptionRequestInput(reqMore: Boolean) extends MultipartMessageEmptyBodyRequestInput

trait SwitchDescriptionReplyHandler{
  def onSwitchDescriptionReply(dpid: ULong, msg: SwitchDescriptionReply): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13SwitchDescriptionDescription extends Ofp13MultipartMessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with SwitchDescriptionReplyHandler] with Ofp13HeaderDescription =>

  class SwitchDescriptionRequestBuilder extends Ofp13MultipartMessageEmptyBodyRequestBuilder[SwitchDescriptionRequestInput]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): SwitchDescriptionRequestInput = SwitchDescriptionRequestInput("flags")
  }

  implicit object SwitchDescription extends FromDynamic[SwitchDescription]{
    val fromDynamic: PartialFunction[DynamicStructure, SwitchDescription] = {
      case s if s.ofType[SwitchDescription] => new OfpStructure[SwitchDescription](s) with SwitchDescription {
        val manufacturer = stringField("mfr_desc")  
        val hardware     = stringField("hw_desc")   
        val software     = stringField("sw_desc")   
        val serialNumber = stringField("serial_num")
        val datapath     = stringField("dp_desc")   
      }
    }
  }

  case class SwitchDescriptionReplyStructure(s: DynamicStructure)
    extends OfpMultipartSingleValueMessage[SwitchDescriptionReply, SwitchDescription](s) with SwitchDescriptionReply

  abstract override def builderClasses = classOf[SwitchDescriptionRequestBuilder] :: super.builderClasses
  abstract override def messageClasses = classOf[SwitchDescriptionReplyStructure] :: super.messageClasses
}