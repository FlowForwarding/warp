/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed
package v13
package messages.controller

import spire.syntax.literals._

trait Ofp13RoleTest extends MessageTestsSet[Ofp13DriverApi] {
  abstract override def tests = super.tests + {
    RoleRequestInput(
      ul"33",
      Role.NoChange
    ) ->
    TestResponse({
      case r: RoleReply => true
    }, "Role")
  }
}