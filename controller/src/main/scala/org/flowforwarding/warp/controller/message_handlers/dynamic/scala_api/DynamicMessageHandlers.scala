/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.message_handlers.dynamic.scala_api

import org.flowforwarding.warp.controller.message_handlers.MessageHandlers

import scala.util.Try
import scala.language.dynamics

import spire.math.ULong

import org.flowforwarding.warp.driver_api.dynamic._
import org.flowforwarding.warp.controller.bus.ControllerBus


class Input private[scala_api] (private[scala_api] val input: DynamicBuilderInput) extends Dynamic{
  def updateDynamic(name: String)(value: Any): Unit =
    value match {
      case l:   Byte         => input.setMember(name, DynamicPrimitive(l))
      case l:   Short        => input.setMember(name, DynamicPrimitive(l))
      case l:   Int          => input.setMember(name, DynamicPrimitive(l))
      case l:   Long         => input.setMember(name, DynamicPrimitive(l))

      case ls:  Array[Byte]  => input.setMember(name, DynamicPrimitives(ls.map(_.toLong)))
      case ls:  Array[Short] => input.setMember(name, DynamicPrimitives(ls.map(_.toLong)))
      case ls:  Array[Int]   => input.setMember(name, DynamicPrimitives(ls.map(_.toLong)))
      case ls:  Array[Long]  => input.setMember(name, DynamicPrimitives(ls))

      case bi:  Input        => input.setMember(name, DynamicStructureInput(bi.input))
      case bis: Array[Input] => input.setMember(name, DynamicStructureInputs(bis.map(_.input)))
    }
}

class Builder private[scala_api] (builder: DynamicStructureBuilder[_ <: DynamicStructure]) extends Dynamic{
  def selectDynamic(msgType: String): Input = new Input(builder.newBuilderInput(msgType))
}

class Structure private[scala_api] (ds: DynamicStructure) extends Dynamic{
  // select single primitive
  object -> extends Dynamic{
    def selectDynamic(name: String): Long = ds.primitiveField(name)
  }

  // select single structure
  object ~> extends Dynamic{
    def selectDynamic(name: String): Structure = new Structure(ds.structureField(name))
  }

  // select sequences of primitives
  object --> extends Dynamic{
    def selectDynamic(name: String): Seq[Long] = ds.primitivesSequence(name)
  }

  // select sequences of structures
  object ~~> extends Dynamic{
    def selectDynamic(name: String): Seq[Structure] = ds.structuresSequence(name) map { s => new Structure(s) }
  }

  // test of type
  object ? extends Dynamic{
    def selectDynamic(name: String): Boolean = ds.isTypeOf(name)
  }
}

abstract class DynamicMessageHandlers(controllerBus: ControllerBus) extends MessageHandlers[DynamicStructure, DynamicStructureBuilder[_ <: DynamicStructure]](controllerBus){
  final override def handleMessage(api: DynamicStructureBuilder[_ <: DynamicStructure], dpid: ULong, msg: DynamicStructure): Try[Array[DynamicStructure]] = Try {
    handleMessage(new Builder(api), dpid, new Structure(msg)) map { i => api.build(i.input).get }
  }

  def handleMessage(api: Builder, dpid: ULong, msg: Structure): Array[Input]
}
