/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed
package v13
package messages.controller.multipart

import spire.syntax.literals._

import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.data._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.{MatchInput, GroupId, PortNumber}

trait Ofp13AggregateFlowStatisticsTest extends MessageTestsSet[Ofp13DriverApi] {
  private val req = AggregateFlowStatisticsRequest(uh"0",
    PortNumber.ControllerPort,
    GroupId.AllGroups,
    ul"100",
    ul"256",
    MatchInput(true, Array()))

  abstract override def tests = super.tests + {
    MultipartRequestInput(AggregateFlowStatisticsRequestBodyInput(req)) -> TestResponse({
      case r: MultipartReply => r.body.isInstanceOf[AggregateFlowStatisticsReplyBody]
    }, "AggregateFlowStatistics")
  }
}