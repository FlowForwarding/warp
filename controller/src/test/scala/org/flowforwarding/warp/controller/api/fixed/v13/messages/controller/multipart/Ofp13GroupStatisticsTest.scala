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
import org.flowforwarding.warp.controller.api.fixed.v13.structures.GroupId

trait Ofp13GroupStatisticsTest extends MessageTestsSet[Ofp13DriverApi] {
  private val req = GroupStatisticsRequest(GroupId.AllGroups)

  abstract override def tests = super.tests + {
    MultipartRequestInput(GroupStatisticsRequestBodyInput(req)) -> TestResponse({
      case r: MultipartReply => r.body.isInstanceOf[GroupStatisticsReplyBody]
    }, "GroupStatistics")
  }
}