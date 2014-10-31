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
import org.flowforwarding.warp.controller.api.fixed.v13.structures.{Ofp13HeaderDescription, Ofp13MatchDescription, Match}
import org.flowforwarding.warp.controller.api.fixed.v13.messages.async.FlowRemovedReason.FlowRemovedReason
import org.flowforwarding.warp.controller.api.fixed.v13.messages.{Ofp13Message, Ofp13MessageDescription}

object FlowRemovedReason extends Enumeration{
  type FlowRemovedReason = Value
  val IdleTimeout, HardTimeout, Delete, GroupDelete = Value
}

trait FlowRem extends Ofp13Message{
  val cookie: ULong	              // Opaque controller-issued identifier
  val priority: UInt              // Priority level of flow entry
  val reason: FlowRemovedReason
  val tableId: UShort	            // ID of the table
  val durationSeconds: ULong	    // Time flow was alive in seconds
  val durationNanoseconds: ULong  // Time flow was alive in nanoseconds beyond duration_sec
  val idleTimeout: UInt	          // Idle timeout from original flow mod
  val hardTimeout: UInt	          // Hard timeout from original flow mod
  val packetCount: ULong          // Number of packets that was associated with the flow
  val byteCount: ULong            // Number of bytes that was associated with the flow
  val m: Match	
}

trait FlowRemHandler{
  def onFlowRem(dpid: ULong, msg: FlowRem): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13FlowRemDescription extends Ofp13MessageDescription{
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with FlowRemHandler] with Ofp13HeaderDescription with Ofp13MatchDescription =>

  private class FlowRemStructure(s: DynamicStructure) extends OfpMessage[FlowRem](s) with FlowRem{
    val cookie: ULong	             = primitiveField[ULong]("cookie")
    val priority: UInt	           = primitiveField[UInt]("priority")
    val reason: FlowRemovedReason  = enumField(FlowRemovedReason, "reason")
    val tableId: UShort	           = primitiveField[UShort]("table_id")
    val durationSeconds: ULong	   = primitiveField[ULong]("duration_sec")
    val durationNanoseconds: ULong = primitiveField[ULong]("duration_nsec")
    val idleTimeout: UInt	         = primitiveField[UInt]("idle_timeout")
    val hardTimeout: UInt	         = primitiveField[UInt]("hard_timeout")
    val packetCount: ULong         = primitiveField[ULong]("packet_count")
    val byteCount: ULong           = primitiveField[ULong]("byte_count")
    val m: Match                   = structureField[Match]("match")
  }

  protected abstract override def messageClasses = classOf[FlowRemStructure] :: super.messageClasses
}