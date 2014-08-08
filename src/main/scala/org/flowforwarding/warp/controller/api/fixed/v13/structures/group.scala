package org.flowforwarding.warp.controller.api.fixed.v13.structures

import spire.math._

case class GroupId(id: UInt)

object GroupId{
  /** Represents all groups for group delete commands. */
  val AllGroups = GroupId(UInt(0xfffffffcL))

  /** Wildcard group used only for flow stats requests. */
  val AnyGroup = GroupId(UInt(0xffffffffL))
}