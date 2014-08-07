package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller

import spire.math._

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.Ofp13HeaderDescription
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.Role.Role
import org.flowforwarding.warp.controller.api.fixed.v13.messages.{Ofp13MessageInput, Ofp13Message, Ofp13MessageDescription}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

object Role extends Enumeration{
  type Role = Value
  val NoChange, RoleEqual, RoleMaster, RoleSlave = Value
}

trait RoleReply extends Ofp13Message{
  val generationId: ULong
  val role: Role
}

case class RoleRequestInput(generationId: ULong, role: Role) extends Ofp13MessageInput

private[fixed] trait RoleReplyHandler{
  def onRoleReply(dpid: ULong, msg: RoleReply): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13RoleDescription extends Ofp13MessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with RoleReplyHandler] with Ofp13HeaderDescription =>

  private class RoleRequestBuilder extends OfpMessageBuilder[RoleRequestInput] {
    // Fills the underlying builder with the specified input.
    override protected def applyInput(input: RoleRequestInput): Unit = {
      super.applyInput(input)
      setMember("generation_id", input.generationId)
      setMember("role", input.role)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): RoleRequestInput = RoleRequestInput("generation_id", Role("role"))
  }

  private class RoleReplyStructure(s: DynamicStructure) extends OfpMessage[RoleReply](s) with RoleReply {
    val generationId: ULong = primitiveField[ULong]("generation_id")
    val role = enumField(Role, "role")
  }

  protected abstract override def builderClasses = classOf[RoleRequestBuilder] :: super.builderClasses
  protected abstract override def messageClasses = classOf[RoleReplyStructure] :: super.messageClasses
}