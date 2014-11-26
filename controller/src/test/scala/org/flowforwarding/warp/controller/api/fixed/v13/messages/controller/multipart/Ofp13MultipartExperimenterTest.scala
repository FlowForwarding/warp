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

trait Ofp13MultipartExperimenterTest extends MessageTestsSet[Ofp13DriverApi] {
  private val req = MultipartExperimenter(ui"0", ui"0",  Array(1, 1, 1, 1))

  abstract override def tests = super.tests + {
    MultipartRequestInput(MultipartExperimenterRequestBodyInput(req)) -> TestResponse({
      case r: MultipartReply => r.body.isInstanceOf[MultipartExperimenterReplyBody]
    }, "MultipartExperimenter")
  }
}