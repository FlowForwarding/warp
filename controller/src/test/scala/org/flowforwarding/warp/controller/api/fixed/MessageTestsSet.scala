/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed

trait MessageTestsSet[Description <: MessagesDescriptionHelper[_]]{

  trait TestData { val description: String }
  case class TestResponse(test: PartialFunction[Description#OfpMessage[_], Boolean], description: String) extends TestData
  case class TestNoError(errorClass: Class[_], description: String) extends TestData

  def tests: Map[Description#MessageInput, TestData] = Map()
}