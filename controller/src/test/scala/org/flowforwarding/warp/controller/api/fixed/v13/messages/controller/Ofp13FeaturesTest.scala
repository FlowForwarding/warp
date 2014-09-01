/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed
package v13
package messages.controller

trait Ofp13FeaturesTest extends MessageTestsSet[Ofp13DriverApi] {
  abstract override def tests = super.tests + {
    FeaturesRequestInput() -> TestResponse({
      case r: FeaturesReply => true
    }, "Features")
  }
}
