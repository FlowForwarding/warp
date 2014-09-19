/*
* © 2013 FlowForwarding.Org
* All Rights Reserved.  Use is subject to license terms.
*
* @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
*/
package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.data

import org.flowforwarding.warp.controller.api.dynamic.DynamicStructure
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart._

import spire.math.ULong

case class SwitchDescriptionRequestDataInput(reqMore: Boolean) extends MultipartRequestDataInput

trait SwitchDescription{
  val manufacturer: String /* Manufacturer description. */
  val hardware: String     /* Hardware description. */
  val software: String     /* Software description. */
  val serialNumber: String /* Serial number. */
  val datapath: String     /* Human readable description of datapath. */
}

trait SwitchDescriptionReplyData extends MultipartReplyData{
  def reqMore: Boolean // More replies to follow
  def body: SwitchDescription
}

trait SwitchDescriptionReplyHandler{
  def onSwitchDescriptionReply(dpid: ULong, msg: SwitchDescriptionReplyData): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13SwitchDescriptionDescription extends StructureDescription {
  apiProvider: StructuresDescriptionHelper =>

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

  protected class SwitchDescriptionRequestDataInputBuilder extends OfpStructureBuilder[SwitchDescriptionRequestDataInput] {
    protected def applyInput(input: SwitchDescriptionRequestDataInput): Unit = {
      setMember("flags", if (input.reqMore) ULong(0) else ULong(1))
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): SwitchDescriptionRequestDataInput = ???
  }

  protected abstract override def builderClasses = classOf[SwitchDescriptionRequestDataInputBuilder] :: super.builderClasses
}