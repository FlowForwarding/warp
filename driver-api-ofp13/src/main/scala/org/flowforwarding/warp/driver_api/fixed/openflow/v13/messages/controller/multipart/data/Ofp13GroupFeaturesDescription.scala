/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart.data

import org.flowforwarding.warp.driver_api.fixed.SpecificVersionMessageHandlersSet
import spire.math._

import org.flowforwarding.warp.driver_api.dynamic._
import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart.{EmptyMultipartRequestBodyInput, MultipartReplyBody}
import org.flowforwarding.warp.driver_api.fixed.util.Bitmap
import org.flowforwarding.warp.driver_api.fixed.text_view.BITextView

case class GroupFeaturesRequestBodyInput() extends EmptyMultipartRequestBodyInput

trait GroupFeatures{
  /** Bitmap of (1 << OFPGT_*) values supported. */
  val types: UInt
  /** Bitmap of OFPGFC_* capability supported. */
  val capabilities: GroupCapabilities
  /** Maximum number of groups for each type. */
  val maxGroups: Array[UInt]
  /** Bitmaps of (1 << OFPAT_*) values supported. */
  val actions: Array[UInt]
}

case class GroupCapabilities(selectWeight: Boolean = false,
                             selectLiveness: Boolean = false,
                             chaining: Boolean = false,
                             chainingChecks: Boolean = false) extends Bitmap

trait GroupFeaturesReplyBody extends MultipartReplyBody[GroupFeatures]

trait GroupFeaturesReplyHandler{
  def onGroupFeaturesReply(dpid: ULong, msg: GroupFeatures): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13GroupFeaturesDescription extends StructureDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _] with GroupFeaturesReplyHandler] =>

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

  class GroupFeaturesRequestBodyInputBuilder extends OfpStructureBuilder[GroupFeaturesRequestBodyInput]{
    protected def applyInput(input: GroupFeaturesRequestBodyInput): Unit = { }

    override private[fixed] def inputFromTextView(implicit input: BITextView): GroupFeaturesRequestBodyInput = GroupFeaturesRequestBodyInput()
  }

  abstract override def builderClasses = classOf[GroupFeaturesRequestBodyInputBuilder] :: super.builderClasses
}