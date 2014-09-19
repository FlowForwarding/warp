/*
* © 2013 FlowForwarding.Org
* All Rights Reserved.  Use is subject to license terms.
*
* @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
*/
package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.data

import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.{Ofp13PortDescription, Port}
import spire.math.ULong

case class PortDescriptionRequestDataInput(reqMore: Boolean) extends MultipartRequestDataInput

trait PortDescriptionReplyData extends MultipartReplyData{
  def reqMore: Boolean // More replies to follow
  def body: Array[Port]
}

trait PortDescriptionReplyHandler{
  def onPortDescriptionReply(dpid: ULong, msg: PortDescriptionReplyData): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13PortDescriptionDescription extends StructureDescription {
  apiProvider: StructuresDescriptionHelper with Ofp13PortDescription =>

  protected class PortDescriptionRequestDataInputBuilder extends OfpStructureBuilder[PortDescriptionRequestDataInput] {
    protected def applyInput(input: PortDescriptionRequestDataInput): Unit = {
      setMember("flags", if (input.reqMore) ULong(0) else ULong(1))
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): PortDescriptionRequestDataInput = ???
  }

  protected abstract override def builderClasses = classOf[PortDescriptionRequestDataInputBuilder] :: super.builderClasses
}