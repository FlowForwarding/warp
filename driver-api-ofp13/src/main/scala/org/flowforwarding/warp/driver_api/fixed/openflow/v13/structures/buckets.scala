/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures

import spire.math.{UShort, UInt}

import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.dynamic.{DynamicBuilderInput, DynamicStructure}
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.actions.{Action, Ofp13ActionsDescription}
import org.flowforwarding.warp.driver_api.fixed.text_view.BITextView

trait Bucket extends BuilderInput {
  val weight: UShort
  val watchPort: PortNumber
  val watchGroup: GroupId
  val actions: Array[Action]
}

object Bucket{
  def build(_weight: UShort, _watchPort: PortNumber, _watchGroup: GroupId, _actions: Array[Action]) = new Bucket {
    val watchGroup: GroupId = _watchGroup
    val watchPort: PortNumber = _watchPort
    val weight: UShort = _weight
    val actions: Array[Action] = _actions
  }
}

private[fixed] trait Ofp13BucketDescription extends StructureDescription{
  apiProvider: StructuresDescriptionHelper with Ofp13ActionsDescription =>

  protected[fixed] implicit object Bucket extends FromDynamic[Bucket] with ToDynamic[Bucket]{

    def fromDynamic: PartialFunction[DynamicStructure, Bucket] = {
      case s if s.ofType[Bucket] => new OfpStructure[Bucket](s) with Bucket {
        val weight     = primitiveField[UShort]("weight")
        val watchPort  = PortNumber(primitiveField[UInt]("watch_port"))
        val watchGroup = GroupId(primitiveField[UInt]("watch_group"))
        val actions    = structuresSequence[Action]("actions")
      }
    }

    def toDynamic: PartialFunction[Bucket, DynamicBuilderInput] = { case b => new BucketBuilder toDynamicInput b }
  }

  private class BucketBuilder extends OfpStructureBuilder[Bucket]{
    // Fills the underlying builder with the specified input.
    protected def applyInput(input: Bucket): Unit = {
      setMember("weight", input.weight)
      setMember("watch_port", input.watchPort.number)
      setMember("watch_group", input.watchGroup.id)
      setMember("actions", input.actions)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): Bucket =
      new Bucket{
        override val actions: Array[Action] = "actions"
        override val watchGroup: GroupId = GroupId("watch_group")
        override val watchPort: PortNumber = PortNumber("watch_port")
        override val weight: UShort = "weight"
      }
  }

  protected abstract override def builderClasses = classOf[BucketBuilder] :: super.builderClasses
}

