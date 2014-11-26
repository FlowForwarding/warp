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
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod.MeterModFlags
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.{MultipartReplyBody, EmptyMultipartRequestBodyInput}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

case class MeterFeaturesRequestBodyInput() extends EmptyMultipartRequestBodyInput

trait MeterFeatures{
  /** Maximum number of meters. */
  val maxMeter: ULong
  /** Bitmaps of (1 << OFPMBT_*) values supported. */    // TODO: improve API
  val bandTypes: UInt
  /** Bitmaps of "ofp_meter_flags". */
  val capabilities: MeterModFlags
  /** Maximum bands per meters */
  val maxBands: UShort
  /** Maximum color value */
  val maxColor: UShort
}

trait MeterFeaturesReplyBody extends MultipartReplyBody[MeterFeatures]

trait MeterFeaturesReplyHandler{
  def onMeterFeaturesReply(dpid: ULong, msg: MeterFeatures): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13MeterFeaturesDescription extends StructureDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with MeterFeaturesReplyHandler]  =>

  implicit object MeterFeatures extends FromDynamic[MeterFeatures]{
    val fromDynamic: PartialFunction[DynamicStructure, MeterFeatures] = {
      case s if s.ofType[MeterFeatures] => new OfpStructure[MeterFeatures](s) with MeterFeatures {
        val maxMeter     = primitiveField[ULong]("max_meter")
        val bandTypes    = primitiveField[UInt]("band_types")
        val capabilities = bitmapField[MeterModFlags]("capabilities")
        val maxBands     = primitiveField[UShort]("max_bands")
        val maxColor     = primitiveField[UShort]("max_color")
      }
    }
  }

  class MeterFeaturesRequestBodyInputBuilder extends OfpStructureBuilder[MeterFeaturesRequestBodyInput]{
    protected def applyInput(input: MeterFeaturesRequestBodyInput): Unit = { }

    override private[fixed] def inputFromTextView(implicit input: BITextView): MeterFeaturesRequestBodyInput = MeterFeaturesRequestBodyInput()
  }

  abstract override def builderClasses = classOf[MeterFeaturesRequestBodyInputBuilder] :: super.builderClasses
}