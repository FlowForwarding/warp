/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures

import spire.math._

case class GroupId(id: UInt)

object GroupId{
  /** Represents all groups for group delete commands. */
  val AllGroups = GroupId(UInt(0xfffffffcL))

  /** Wildcard group used only for flow stats requests. */
  val AnyGroup = GroupId(UInt(0xffffffffL))
}