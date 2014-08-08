package org.flowforwarding.warp.controller.api.dynamic.java_api

import scala.util.Try

import spire.math.ULong

import org.flowforwarding.warp.controller.driver_interface.MessageHandlers
import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.bus.ControllerBus

class Builder private[java_api] (builder: DynamicStructureBuilder[_ <: DynamicStructure]){
  def newBuilderInput(msgType: String): Input = new Input(builder.newBuilderInput(msgType))
}

class Input private[java_api] (private[java_api] val input: DynamicBuilderInput) {
  def setMember(name: String, value: Long)          = { input.setMember(name, DynamicPrimitive(value)); this }
  def setMember(name: String, value: Input)         = { input.setMember(name, DynamicStructureInput(value.input)); this }
  def setMember(name: String, values: Array[Long])  = { input.setMember(name, DynamicPrimitives(values)); this }
  def setMember(name: String, values: Array[Input]) = { input.setMember(name, DynamicStructureInputs(values.map(_.input))); this }
}

class Structure private[java_api] (ds: DynamicStructure){
  def primitiveField(name: String): Long                        = ds.primitiveField(name)
  def structureField(name: String): DynamicStructure            = ds.structureField(name)
  def primitivesSequence(name: String): Array[Long]             = ds.primitivesSequence(name)
  def structuresSequence(name: String): Array[DynamicStructure] = ds.structuresSequence(name)
  def isTypeOf(typeName: String): Boolean                       = ds.isTypeOf(typeName)
}

abstract class DynamicMessageHandlers(controllerBus: ControllerBus) extends MessageHandlers[DynamicStructure, DynamicStructureBuilder[_ <: DynamicStructure]](controllerBus){
  final override def handleMessage(api: DynamicStructureBuilder[_ <: DynamicStructure], dpid: ULong, msg: DynamicStructure): Try[Array[DynamicStructure]] = Try {
    handleMessage(new Builder(api), dpid.signed, new Structure(msg)) map { i => api.build(i.input).get }
  }

  def handleMessage(api: Builder, dpid: Long, msg: Structure): Array[Input]
}
