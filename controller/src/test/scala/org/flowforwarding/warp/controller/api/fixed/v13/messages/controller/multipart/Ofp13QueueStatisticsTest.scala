/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed
package v13
package messages.controller.multipart

import spire.math.UInt

import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.data._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.PortNumber

trait Ofp13QueueStatisticsTest extends MessageTestsSet[Ofp13DriverApi] {
  private val req = QueueStatisticsRequest(PortNumber.AnyPort, UInt(0xffffffff))

  abstract override def tests = super.tests + {
    MultipartRequestInput(QueueStatisticsRequestBodyInput(req)) -> TestResponse({
      case r: MultipartReply => r.body.isInstanceOf[QueueStatisticsReplyBody]
    }, "QueueStatistics")
  }
}