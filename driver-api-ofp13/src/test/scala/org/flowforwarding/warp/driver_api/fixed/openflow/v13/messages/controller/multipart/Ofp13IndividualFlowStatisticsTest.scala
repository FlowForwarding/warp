/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed
package openflow.v13
package messages.controller.multipart

import spire.syntax.literals._

import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart.data._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.{MatchInput, GroupId, PortNumber}

import org.flowforwarding.warp.driver_api.fixed.test.MessageTestsSet

trait Ofp13IndividualFlowStatisticsTest extends MessageTestsSet[Ofp13DriverApi] {
  private val req = IndividualFlowStatisticsRequest(uh"0",
                                            PortNumber.ControllerPort,
                                            GroupId.AllGroups,
                                            ul"100",
                                            ul"256",
                                            MatchInput(true, Array()))

  abstract override def tests = super.tests + {
    MultipartRequestInput(IndividualFlowStatisticsRequestBodyInput(req)) -> TestResponse({
      case r: MultipartReply => r.body.isInstanceOf[IndividualFlowStatisticsReplyBody]
    }, "IndividualFlowStatistics")
  }
}