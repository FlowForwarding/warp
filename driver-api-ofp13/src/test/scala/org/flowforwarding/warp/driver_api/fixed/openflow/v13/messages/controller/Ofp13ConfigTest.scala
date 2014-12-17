/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed
package openflow.v13
package messages.controller

import org.flowforwarding.warp.driver_api.fixed.test.MessageTestsSet

trait Ofp13ConfigTest extends MessageTestsSet[Ofp13DriverApi] {
  abstract override def tests = super.tests + {
    GetConfigRequestInput() -> TestResponse({
      case r: GetConfigReply => true
    }, "Config")
  }
}