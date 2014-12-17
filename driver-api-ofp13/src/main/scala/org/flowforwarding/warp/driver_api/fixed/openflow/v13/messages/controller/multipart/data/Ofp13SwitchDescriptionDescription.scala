/*
* © 2013 FlowForwarding.Org
* All Rights Reserved.  Use is subject to license terms.
*
* @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
*/
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart.data

import org.flowforwarding.warp.driver_api.fixed.SpecificVersionMessageHandlersSet
import org.flowforwarding.warp.driver_api.dynamic.DynamicStructure
import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.fixed.text_view.BITextView
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart._

import spire.math.ULong

case class SwitchDescriptionRequestBodyInput() extends EmptyMultipartRequestBodyInput

trait SwitchDescription{
  val manufacturer: String /* Manufacturer description. */
  val hardware: String     /* Hardware description. */
  val software: String     /* Software description. */
  val serialNumber: String /* Serial number. */
  val datapath: String     /* Human readable description of datapath. */
}

trait SwitchDescriptionReplyBody extends MultipartReplyBody[SwitchDescription]

trait SwitchDescriptionReplyHandler{
  def onSwitchDescriptionReply(dpid: ULong, desc: SwitchDescription): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13SwitchDescriptionDescription extends StructureDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _] with SwitchDescriptionReplyHandler] =>

  implicit object SwitchDescription extends FromDynamic[SwitchDescription] {
    val fromDynamic: PartialFunction[DynamicStructure, SwitchDescription] = {
      case s if s.ofType[SwitchDescription] => new OfpStructure[SwitchDescription](s) with SwitchDescription {
        val manufacturer = stringField("mfr_desc")
        val hardware = stringField("hw_desc")
        val software = stringField("sw_desc")
        val serialNumber = stringField("serial_num")
        val datapath = stringField("dp_desc")
      }
    }
  }

  protected class SwitchDescriptionRequestBodyInputBuilder extends OfpStructureBuilder[SwitchDescriptionRequestBodyInput] {
    protected def applyInput(input: SwitchDescriptionRequestBodyInput): Unit = { }

    override private[fixed] def inputFromTextView(implicit input: BITextView): SwitchDescriptionRequestBodyInput = SwitchDescriptionRequestBodyInput()
  }

  protected abstract override def builderClasses = classOf[SwitchDescriptionRequestBodyInputBuilder] :: super.builderClasses
}