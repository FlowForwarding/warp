package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart

import spire.math._

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import org.flowforwarding.warp.controller.api.fixed.util.Bitmap
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

trait GroupFeaturesReply extends Ofp13MultipartSingleValueMessage[GroupFeatures]

trait GroupFeatures{
  val types: UInt                      /* Bitmap of (1 << OFPGT_*) values supported. */
  val capabilities: GroupCapabilities /* Bitmap of OFPGFC_* capability supported. */
  val maxGroups: Array[UInt]           /* Maximum number of groups for each type. */
  val actions: Array[UInt]             /* Bitmaps of (1 << OFPAT_*) values supported. */
}

case class GroupCapabilities(selectWeight: Boolean, selectLiveness: Boolean, chaining: Boolean, chainingChecks: Boolean) extends Bitmap

case class GroupFeaturesRequestInput(reqMore: Boolean) extends MultipartMessageEmptyBodyRequestInput

trait GroupFeaturesReplyHandler{
  def onGroupFeaturesReply(dpid: ULong, msg: GroupFeaturesReply): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13DGroupFeaturesDescription extends Ofp13MultipartMessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with GroupFeaturesReplyHandler] with Ofp13HeaderDescription =>

  class GroupFeaturesRequestBuilder extends Ofp13MultipartMessageEmptyBodyRequestBuilder[GroupFeaturesRequestInput]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): GroupFeaturesRequestInput = GroupFeaturesRequestInput("flags")
  }

  implicit object GroupFeatures extends FromDynamic[GroupFeatures]{
    val fromDynamic: PartialFunction[DynamicStructure, GroupFeatures] = {
      case s if s.ofType[GroupFeatures] => new OfpStructure[GroupFeatures](s) with GroupFeatures {
        val types        = primitiveField[UInt]("types")
        val capabilities = bitmapField[GroupCapabilities]("capabilities")
        val maxGroups    = primitivesSequence[UInt]("max_groups")
        val actions      = primitivesSequence[UInt]("actions")
      }
    }
  }

  case class GroupFeaturesReplyStructure(s: DynamicStructure)
    extends OfpMultipartSingleValueMessage[GroupFeaturesReply, GroupFeatures](s) with GroupFeaturesReply

  abstract override def builderClasses = classOf[GroupFeaturesRequestBuilder] :: super.builderClasses
  abstract override def messageClasses = classOf[GroupFeaturesReplyStructure] :: super.messageClasses
}