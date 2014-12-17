/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed
package openflow.v13
package messages.controller.mod

import structures.MeterId._
import messages.async.{Error => msgError}
import spire.syntax.literals._
import org.flowforwarding.warp.driver_api.fixed.test.MessageTestsSet

trait Ofp13MeterModTest extends MessageTestsSet[Ofp13DriverApi] {
  abstract override def tests = super.tests + {
    MeterModInput(
      MeterModCommand.Add,
      MeterModFlags(true, false, false, true),
      ControllerMeter,
      Array.empty
    ) ->
    TestResponse({
      case e: msgError => e.errorType == uh"12"
    }, "MeterMod >> ADD action")
  }
}
