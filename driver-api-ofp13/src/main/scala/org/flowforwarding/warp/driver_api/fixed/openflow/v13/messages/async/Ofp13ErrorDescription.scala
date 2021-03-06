/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.async

import org.flowforwarding.warp.driver_api.fixed.SpecificVersionMessageHandlersSet
import spire.math._

import org.flowforwarding.warp.driver_api.dynamic._
import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.Ofp13HeaderDescription
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.{Ofp13Message, Ofp13MessageDescription}

trait Error extends Ofp13Message{
  val errorType: UShort
  val errorCode: UShort
  val data: Array[Byte]
  def message = String.valueOf(data)
}

private[fixed] trait ErrorHandler{
  // TODO: convenient error handling (via specialized class, predefined implementation of onError or something else
  def onError(dpid: ULong, msg: Error): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13ErrorDescription extends Ofp13MessageDescription{
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _] with ErrorHandler] with Ofp13HeaderDescription =>

  private class ErrorStructure(s: DynamicStructure) extends OfpMessage[Error](s) with Error {
    val errorType: UShort = primitiveField[UShort]("type")
    val errorCode: UShort = primitiveField[UShort]("code")
    val data: Array[Byte] = bytes("data")
  }

  protected abstract override def messageClasses = classOf[ErrorStructure] :: super.messageClasses
}