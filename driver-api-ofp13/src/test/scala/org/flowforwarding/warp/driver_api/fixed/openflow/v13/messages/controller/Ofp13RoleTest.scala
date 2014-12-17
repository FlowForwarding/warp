/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed
package openflow.v13
package messages.controller

import spire.syntax.literals._
import org.flowforwarding.warp.driver_api.fixed.test.MessageTestsSet

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