package org.flowforwarding.warp.controller.api.fixed.v13

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.Utils._

case class SwitchCapabilities(flowStats: Boolean, tableStats: Boolean, portStats: Boolean, groupStats: Boolean, ipReasm: Boolean, queueStats: Boolean, portBlocked: Boolean)

trait FeaturesReply extends Message{
  def datapathId: Long
  def buffersCount: Int
  def tablesCount: Byte
  def auxiliaryId: Byte
  def capabilities: SwitchCapabilities
}

case class FeaturesRequestInput(xid: Int) extends MessageInput{
  def length: Int = 8
}

trait Ofp13FeaturesDescription[BuilderType <: DynamicStructureBuilder[BuilderType, StructureType],
                               StructureType <: DynamicStructure[StructureType]] extends BasicStructuresDescription[BuilderType, StructureType] {
  apiProvider: DriverApiHelper[BuilderType, StructureType] =>

  class FeaturesRequestBuilder extends OfpMessageBuilder[FeaturesRequestInput]

  case class FeaturesReplyStructure(underlyingStructure: StructureType) extends OfpMessage with FeaturesReply {
    def datapathId: Long  = getPrimitive("datapath_id")
    def buffersCount: Int = getPrimitive("n_buffers").toInt //LONG?
    def tablesCount: Byte = getPrimitive("n_tables").toByte //BYTE?
    def auxiliaryId: Byte = getPrimitive("auxiliary_id").toByte //BYTE?
    def capabilities: SwitchCapabilities = {
      val c = getPrimitive("capabilities")
      SwitchCapabilities(c.testBit(0), c.testBit(1), c.testBit(2), c.testBit(3), c.testBit(5), c.testBit(6), c.testBit(8))
    }
  }

  abstract override def builderClasses = classOf[FeaturesRequestBuilder] :: super.builderClasses
  abstract override def structureClasses = classOf[FeaturesReplyStructure] :: super.structureClasses
}