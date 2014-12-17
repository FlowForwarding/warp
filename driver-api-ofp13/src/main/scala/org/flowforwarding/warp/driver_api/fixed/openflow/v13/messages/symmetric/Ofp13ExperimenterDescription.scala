/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.symmetric

import org.flowforwarding.warp.driver_api.fixed.SpecificVersionMessageHandlersSet
import spire.math._

import org.flowforwarding.warp.driver_api.dynamic._
import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.Ofp13HeaderDescription
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.{Ofp13MessageInput, Ofp13Message, Ofp13MessageDescription}
import org.flowforwarding.warp.driver_api.fixed.text_view.BITextView

trait Experimenter extends Ofp13Message{
  val expId: UInt
  val expType: UInt
  val data: Array[Byte]
}

case class ExperimenterInput(expId: UInt, expType: UInt, data: Array[Byte]) extends Ofp13MessageInput

private[fixed] trait ExperimenterHandler{
  def onExperimenter(dpid: ULong, msg: Experimenter): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13ExperimenterDescription extends Ofp13MessageDescription{
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _] with ExperimenterHandler] with Ofp13HeaderDescription =>

  private class ExperimenterBuilder extends OfpMessageBuilder[ExperimenterInput]{
    // Fills the underlying builder with the specified input.
    protected override def applyInput(input: ExperimenterInput): Unit = {
      super.applyInput(input)
      setMember("experimenter", input.expId)
      setMember("exp_type", input.expType)
      setMember("data", input.data)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ExperimenterInput = ExperimenterInput("experimenter", "exp_type", "data")
  }

  private class ExperimenterStructure(s: DynamicStructure) extends OfpMessage[Experimenter](s) with Experimenter{
    val expId: UInt = primitiveField[UInt]("experimenter")
    val expType: UInt = primitiveField[UInt]("exp_type")
    val data: Array[Byte] = bytes("data")
  }

  protected abstract override def builderClasses = classOf[ExperimenterBuilder] :: super.builderClasses
  protected abstract override def messageClasses = classOf[ExperimenterStructure] :: super.messageClasses
}