/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed
package openflow.v13
package messages.controller.multipart

import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.oxm_tlv.{OxmHeader, in_port}
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.table_features._
import spire.syntax.literals._

import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart.data._

import org.flowforwarding.warp.driver_api.fixed.test.MessageTestsSet

trait Ofp13TableFeaturesTest extends MessageTestsSet[Ofp13DriverApi] {
  val OxmHeader(data) = in_port(ui"100").header
  private val req = TableFeatures(ub"0", "name", ul"0", ul"0", ui"0", ui"10",
    Array(TableFeaturePropertyInstructions(Array(InstructionGotoTableId())),
          TableFeaturePropertyWildcards(Array(data))))

  abstract override def tests = super.tests + {
    MultipartRequestInput(TableFeaturesRequestBodyInput(Array(req))) -> TestResponse({
      case r: MultipartReply => r.body.isInstanceOf[TableFeaturesReplyBody]
      case m: messages.async.Error => true
    }, "TableFeatures")
  }
}