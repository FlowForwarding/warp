package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod

import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.{GroupId, Ofp13HeaderDescription}
import org.flowforwarding.warp.controller.api.fixed.v13.structures.{Ofp13BucketDescription, Bucket}
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod.GroupModCommand.GroupModCommand
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod.GroupModType.GroupModType
import org.flowforwarding.warp.controller.api.fixed.v13.messages.{Ofp13MessageInput, Ofp13MessageDescription}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

object GroupModCommand extends Enumeration{
  type GroupModCommand = Value
  val Add, Modify, Delete = Value
}

object GroupModType extends Enumeration{
  type GroupModType = Value
  val All, Select, Indirect, FF = Value
}

case class GroupModInput(command: GroupModCommand, groupType: GroupModType, groupId: GroupId, buckets: Array[Bucket]) extends Ofp13MessageInput

private[fixed] trait Ofp13GroupModDescription extends Ofp13MessageDescription{
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _]] with Ofp13HeaderDescription with Ofp13BucketDescription =>

  private class GroupModBuilder extends OfpMessageBuilder[GroupModInput]{
    // Fills the underlying builder with the specified input.
    protected override def applyInput(input: GroupModInput): Unit = {
      super.applyInput(input)
      setMember("command", input.command)
      setMember("type", input.groupType)
      setMember("group_id", input.groupId.id)
      setMember("buckets", input.buckets)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): GroupModInput =
      GroupModInput(
        GroupModCommand("command"),
        GroupModType("type"),
        GroupId("group_id"),
        "buckets")
  }

  protected abstract override def builderClasses = classOf[GroupModBuilder] :: super.builderClasses
}