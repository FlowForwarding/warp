package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart

import spire.math._

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod.GroupModType
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod.GroupModType.GroupModType
import org.flowforwarding.warp.controller.api.fixed.v13.structures.{Ofp13BucketDescription, Bucket}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

trait GroupDescriptionReply extends Ofp13MultipartMultiValueMessage[GroupDescription]

trait GroupDescription{
  val groupType: GroupModType /* One of OFPGT_*. */
  val groupId: GroupId        /* Group identifier. */
  val buckets: Array[Bucket]  /* List of buckets - 0 or more. */
}

case class GroupDescriptionRequestInput(reqMore: Boolean) extends MultipartMessageEmptyBodyRequestInput

trait GroupDescriptionReplyHandler{
  def onGroupDescriptionReply(dpid: ULong, msg: GroupDescriptionReply): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13DGroupDescriptionDescription extends Ofp13MultipartMessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with GroupDescriptionReplyHandler] with Ofp13HeaderDescription with Ofp13BucketDescription =>

  class GroupDescriptionRequestBuilder extends Ofp13MultipartMessageEmptyBodyRequestBuilder[GroupDescriptionRequestInput]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): GroupDescriptionRequestInput = GroupDescriptionRequestInput("flags")
  }

  implicit object GroupDescription extends FromDynamic[GroupDescription]{
    val fromDynamic: PartialFunction[DynamicStructure, GroupDescription] = {
      case s if s.ofType[GroupDescription] => new OfpStructure[GroupDescription](s) with GroupDescription {
        val groupType = enumField(GroupModType, "type")
        val groupId   = GroupId(primitiveField[UInt]("group_id"))
        val buckets   = structuresSequence[Bucket]("buckets")
      }
    }
  }

  case class GroupDescriptionReplyStructure(s: DynamicStructure)
    extends OfpMultipartMultiValueMessage[GroupDescriptionReply, GroupDescription](s) with GroupDescriptionReply

  abstract override def builderClasses = classOf[GroupDescriptionRequestBuilder] :: super.builderClasses
  abstract override def messageClasses = classOf[GroupDescriptionReplyStructure] :: super.messageClasses
}