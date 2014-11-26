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

trait Ofp13PortStatisticsTest extends MessageTestsSet[Ofp13DriverApi] {
  private val req = PortStatisticsRequest(PortNumber.AnyPort)

  abstract override def tests = super.tests + {
    MultipartRequestInput(PortStatisticsRequestBodyInput(req)) -> TestResponse({
      case r: MultipartReply => r.body.isInstanceOf[PortStatisticsReplyBody]
    }, "PortStatistics")
  }
}