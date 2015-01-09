/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed
package openflow.v13
package messages.controller.multipart

import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart.data._
import org.flowforwarding.warp.driver_api.fixed.test.MessageTestsSet

trait Ofp13SwitchDescriptionTest extends MessageTestsSet[Ofp13DriverApi] {
  abstract override def tests = super.tests + {
    MultipartRequestInput(SwitchDescriptionRequestBodyInput()) -> TestResponse({
       case r: MultipartReply =>
        r.body.isInstanceOf[SwitchDescriptionReplyBody]
    }, "SwitchDescription")
  }
}