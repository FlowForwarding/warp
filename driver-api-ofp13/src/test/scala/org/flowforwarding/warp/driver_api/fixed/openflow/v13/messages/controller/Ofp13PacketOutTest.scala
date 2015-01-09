/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed
package openflow.v13
package messages.controller

import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.PortNumber._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.actions.Action

import org.flowforwarding.warp.driver_api.fixed.test.MessageTestsSet

trait Ofp13PacketOutTest extends MessageTestsSet[Ofp13DriverApi] {
  abstract override def tests = super.tests + {
    PacketOutInput(Max, ControllerPort, Array(Action.output(AllPorts)), Array(2, 2, 2, 2, 2)) ->
      TestNoError("PacketOut")
  }
}
