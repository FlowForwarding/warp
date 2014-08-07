package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart

import spire.math._

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod.{MeterModFlags, GroupModType}
import org.flowforwarding.warp.controller.api.fixed.v13.structures.meter_bands.{Ofp13MeterBandsDescription, MeterBand}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

trait MeterConfigReply extends Ofp13MultipartMultiValueMessage[MeterConfig]

trait MeterConfig{
  val flags: MeterModFlags
  val meterId: MeterId
  val bands: Array[MeterBand]
}

case class MeterConfigRequestInput(reqMore: Boolean) extends MultipartMessageEmptyBodyRequestInput

trait MeterConfigReplyHandler{
  def onMeterConfigReply(dpid: ULong, msg: MeterConfigReply): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13DMeterConfigDescription extends Ofp13MultipartMessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with MeterConfigReplyHandler] with Ofp13HeaderDescription with Ofp13MeterBandsDescription =>

  class MeterConfigRequestBuilder extends Ofp13MultipartMessageEmptyBodyRequestBuilder[MeterConfigRequestInput]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): MeterConfigRequestInput = MeterConfigRequestInput("flags")
  }

  implicit object MeterConfig extends FromDynamic[MeterConfig]{
    val fromDynamic: PartialFunction[DynamicStructure, MeterConfig] = {
      case s if s.ofType[MeterConfig] => new OfpStructure[MeterConfig](s) with MeterConfig {
        val flags = bitmapField[MeterModFlags]("flags")
        val meterId = MeterId(primitiveField[UInt]("group_id"))
        val bands = structuresSequence[MeterBand]("bands")
      }
    }
  }

  case class MeterConfigReplyStructure(s: DynamicStructure)
    extends OfpMultipartMultiValueMessage[MeterConfigReply, MeterConfig](s) with MeterConfigReply

  abstract override def builderClasses = classOf[MeterConfigRequestBuilder] :: super.builderClasses
  abstract override def messageClasses = classOf[MeterConfigReplyStructure] :: super.messageClasses
}