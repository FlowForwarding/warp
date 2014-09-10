/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.messages.symmetric

import org.flowforwarding.warp.controller.api.dynamic.{DynamicStructure, DynamicStructureBuilder}
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.Ofp13HeaderDescription

import org.flowforwarding.warp.controller.api.fixed.v13.messages.{Ofp13MessageInput, Ofp13Message, Ofp13MessageDescription}
import org.flowforwarding.warp.controller.api.fixed.v13.Ofp13DriverApi

trait Ofp13EchoTest extends MessageTestsSet[Ofp13DriverApi] {
  abstract override def tests = super.tests +
    (EchoRequestInput(Array[Byte](5, 5, 5, 5, 5)) -> TestResponse({ case r: EchoReply if r.elements.sameElements(Array(5, 5, 5, 5, 5)) => true } , "Echo request/reply"))
}


