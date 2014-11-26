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
import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod.{MeterModFlags, GroupModType}
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.{MultipartReplyBody, EmptyMultipartRequestBodyInput}
import org.flowforwarding.warp.controller.api.fixed.v13.structures.meter_bands.{Ofp13MeterBandsDescription, MeterBand}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

case class MeterConfigRequest(meterId: MeterId) extends BuilderInput

case class MeterConfigRequestBodyInput(structure: MeterConfigRequest) extends EmptyMultipartRequestBodyInput

trait MeterConfig{
  val flags: MeterModFlags
  val meterId: MeterId
  val bands: Array[MeterBand]
}

trait MeterConfigReplyBody extends MultipartReplyBody[Array[MeterConfig]]

trait MeterConfigReplyHandler{
  def onMeterConfigReply(dpid: ULong, msg: Array[MeterConfig]): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13MeterConfigDescription extends StructureDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with MeterConfigReplyHandler] with Ofp13MeterBandsDescription =>

  implicit object MeterConfig extends FromDynamic[MeterConfig]{
    val fromDynamic: PartialFunction[DynamicStructure, MeterConfig] = {
      case s if s.ofType[MeterConfig] => new OfpStructure[MeterConfig](s) with MeterConfig {
        val flags = bitmapField[MeterModFlags]("flags")
        val meterId = MeterId(primitiveField[UInt]("group_id"))
        val bands = structuresSequence[MeterBand]("bands")
      }
    }
  }

  private class MeterConfigRequestBuilder extends OfpStructureBuilder[MeterConfigRequest]{
    // Fills the underlying builder with the specified input.
    protected def applyInput(input: MeterConfigRequest): Unit = {
      setMember("meter_id", input.meterId.id)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView) = new MeterConfigRequest(MeterId("meter_id"))
  }


  protected[fixed] implicit object MeterConfigRequest extends ToDynamic[MeterConfigRequest]{
    val toDynamic: PartialFunction[MeterConfigRequest, DynamicBuilderInput] = {
      case s => new MeterConfigRequestBuilder toDynamicInput s
    }
  }

  class MeterConfigRequestBodyInputBuilder extends OfpStructureBuilder[MeterConfigRequestBodyInput]{
    protected def applyInput(input: MeterConfigRequestBodyInput): Unit = {
      setMember("structure", input.structure)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): MeterConfigRequestBodyInput =
      new MeterConfigRequestBodyInput(structure[MeterConfigRequest]("structure"))
  }

  abstract override def builderClasses = classOf[MeterConfigRequestBodyInputBuilder] :: super.builderClasses
}