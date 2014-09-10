/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.messages.async

import spire.math._

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.async.PortReason.PortReason
import org.flowforwarding.warp.controller.api.fixed.v13.structures.{Ofp13HeaderDescription, Ofp13PortDescription, Port}
import org.flowforwarding.warp.controller.api.fixed.v13._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.{Ofp13Message, Ofp13MessageDescription}

object PortReason extends Enumeration{
  type PortReason = Value
  val ADD, DELETE, MODIFY = Value
}

trait PortStatus extends Ofp13Message{
  val reason: PortReason
  val port: Port
}

trait PortStatusHandler{
  def onPortStatus(dpid: ULong, msg: PortStatus): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13PortStatusDescription extends Ofp13MessageDescription{
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with PortStatusHandler] with Ofp13HeaderDescription with Ofp13PortDescription =>

  private class PortStatusStructure(s: DynamicStructure) extends OfpMessage[PortStatus](s) with PortStatus{
    val reason: PortReason = enumField(PortReason, "reason")
    val port: Port = structureField[Port]("desc")
  }

  protected abstract override def messageClasses = classOf[PortStatusStructure] :: super.messageClasses
}