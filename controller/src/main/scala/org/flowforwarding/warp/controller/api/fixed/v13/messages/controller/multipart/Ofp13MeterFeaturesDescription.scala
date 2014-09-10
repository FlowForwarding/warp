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
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod.MeterModFlags
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

trait MeterFeaturesReply extends Ofp13MultipartSingleValueMessage[MeterFeatures]

trait MeterFeatures{
  val maxMeter: ULong              /* Maximum number of meters. */
  val bandTypes: UInt              /* Bitmaps of (1 << OFPMBT_*) values supported. */    // TODO: improve API
  val capabilities: MeterModFlags /* Bitmaps of "ofp_meter_flags". */
  val maxBands: UShort             /* Maximum bands per meters */
  val maxColor: UShort             /* Maximum color value */
}

case class MeterFeaturesRequestInput(reqMore: Boolean) extends MultipartMessageEmptyBodyRequestInput

trait MeterFeaturesReplyHandler{
  def onMeterFeaturesReply(dpid: ULong, msg: MeterFeaturesReply): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13DMeterFeaturesDescription extends Ofp13MultipartMessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with MeterFeaturesReplyHandler] with Ofp13HeaderDescription =>

  class MeterFeaturesRequestBuilder extends Ofp13MultipartMessageEmptyBodyRequestBuilder[MeterFeaturesRequestInput]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): MeterFeaturesRequestInput = MeterFeaturesRequestInput("flags")
  }

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

  case class MeterFeaturesReplyStructure(s: DynamicStructure)
    extends OfpMultipartSingleValueMessage[MeterFeaturesReply, MeterFeatures](s) with MeterFeaturesReply

  abstract override def builderClasses = classOf[MeterFeaturesRequestBuilder] :: super.builderClasses
  abstract override def messageClasses = classOf[MeterFeaturesReplyStructure] :: super.messageClasses
}