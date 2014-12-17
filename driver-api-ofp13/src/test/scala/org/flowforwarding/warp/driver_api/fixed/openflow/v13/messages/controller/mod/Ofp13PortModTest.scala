/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed
package openflow.v13
package messages.controller.mod

import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures._
import util.MacAddress

import spire.math.{UInt, ULong}
import org.flowforwarding.warp.driver_api.fixed.test.MessageTestsSet

trait Ofp13PortModTest  extends MessageTestsSet[Ofp13DriverApi] {
  abstract override def tests = super.tests +
    (PortModInput(
      PortNumber(UInt(6633)),
      MacAddress(ULong(0x010203040506L)),
      PortConfig(false, true, true, false),
      PortConfig(true, true, true, true),
      PortFeatures(true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true)
    ) -> TestResponse({ case res: messages.async.Error => true }, "PortMod" ))
}
