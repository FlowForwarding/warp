/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed
package v13
package messages.controller

import structures.PortNumber

import spire.math.UInt

trait Ofp13QueueGetConfigTest extends MessageTestsSet[Ofp13DriverApi] {
  abstract override def tests = super.tests + {
    val portNo = UInt(33)

    QueueGetConfigRequestInput(
      PortNumber(portNo)
    ) ->
    TestResponse({
      case r: QueueGetConfigReply => r.port.number == portNo
    }, "QueueGetConfig")
  }
}