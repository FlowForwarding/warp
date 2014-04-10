package org.flowforwarding.warp.controller.api.dynamic

import org.flowforwarding.warp.controller.session.MessageDriver

trait DynamicDriver[BuilderType <: DynamicStructureBuilder[BuilderType, StructureType],
                    StructureType <: DynamicStructure[StructureType]] extends MessageDriver[StructureType]{
  def getBuilder(msgType: String): BuilderType
}