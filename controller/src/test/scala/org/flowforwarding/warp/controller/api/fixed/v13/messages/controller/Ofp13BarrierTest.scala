/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed
package v13
package messages.controller

trait Ofp13BarrierTest extends MessageTestsSet[Ofp13DriverApi] {
  abstract override def tests = super.tests + {
    val input = BarrierRequestInput()
    input -> TestResponse({
      case reply: BarrierReply => reply.header.xid == input.header.xid
    }, "Barrier")
  }
}