/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed
package v13
package messages.controller.multipart

import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.data._

trait Ofp13SwitchDescriptionTest extends MessageTestsSet[Ofp13DriverApi] {
  abstract override def tests = super.tests + {
    MultipartRequestInput(SwitchDescriptionRequestDataInput(false)) -> TestResponse({
       case r: MultipartReply =>
        val p = scala.util.Try {
          val desc = r.data.asInstanceOf[SwitchDescriptionReplyData].body
          desc.datapath +
          desc.hardware +
          desc.manufacturer +
          desc.serialNumber +
          desc.software
        }
        r.data.isInstanceOf[SwitchDescriptionReplyData]
    }, "SwitchDescription")
  }
}