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

trait Ofp13FeaturesTest extends MessageTestsSet[Ofp13DriverApi] {
  abstract override def tests = super.tests + {
    FeaturesRequestInput() -> TestResponse({
      case r: FeaturesReply => true
    }, "Features")
  }
}
