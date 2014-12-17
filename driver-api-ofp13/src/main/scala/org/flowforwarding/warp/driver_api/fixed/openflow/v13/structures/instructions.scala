/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.instructions

import spire.math._

import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.dynamic.{DynamicBuilderInput, DynamicStructure}
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.actions.{Ofp13ActionsDescription, Action}
import org.flowforwarding.warp.driver_api.fixed.text_view.BITextView

trait Instruction extends BuilderInput

case class InstructionGotoTable(tableId: UByte) extends Instruction

case class InstructionWriteMetadata(metadata: ULong, mask: ULong) extends Instruction

trait InstructionActions extends Instruction {
  def actions: Array[Action]
}

case class InstructionWriteActions(actions: Array[Action]) extends InstructionActions

case class InstructionApplyActions(actions: Array[Action]) extends InstructionActions

case class InstructionClearActions() extends InstructionActions {
  val actions = Array.empty[Action]
}

case class InstructionMeter(meterId: UInt) extends Instruction

case class InstructionExperimenter(experimenter: UInt, data: Array[Byte] = Array.empty) extends Instruction

private[fixed] trait Ofp13InstructionsDescription extends StructureDescription{
  apiProvider: StructuresDescriptionHelper with Ofp13ActionsDescription =>

  protected implicit object Instruction extends ToDynamic[Instruction]{
    val toDynamic: PartialFunction[Instruction, DynamicBuilderInput] = {
      case i: InstructionGotoTable     => new InstructionGotoTableBuilder toDynamicInput i
      case i: InstructionWriteMetadata => new InstructionWriteMetadataBuilder toDynamicInput i
      case i: InstructionWriteActions  => new InstructionWriteActionsBuilder toDynamicInput i
      case i: InstructionApplyActions  => new InstructionApplyActionsBuilder toDynamicInput i
      case i: InstructionClearActions  => new InstructionClearActionsBuilder toDynamicInput i
      case i: InstructionMeter         => new InstructionMeterBuilder toDynamicInput i
      case i: InstructionExperimenter  => new InstructionExperimenterBuilder toDynamicInput i
    }
  }

  private class InstructionGotoTableBuilder extends OfpStructureBuilder[InstructionGotoTable]{
    protected def applyInput(input: InstructionGotoTable): Unit = {
      setMember("table_id", input.tableId)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): InstructionGotoTable = InstructionGotoTable("table_id")
  }

  private class InstructionWriteMetadataBuilder extends OfpStructureBuilder[InstructionWriteMetadata]  {
    protected def applyInput(input: InstructionWriteMetadata): Unit = {
      setMember("metadata", input.metadata)
      setMember("mask", input.mask)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): InstructionWriteMetadata = InstructionWriteMetadata("metadata", "mask")
  }

  private trait InstructionActionsBuilder[Input <: InstructionActions] extends OfpStructureBuilder[Input] {
    protected def applyInput(input: Input): Unit = {
      setMember("actions", input.actions)
    }
  }

  private class InstructionWriteActionsBuilder extends InstructionActionsBuilder[InstructionWriteActions] {
    override private[fixed] def inputFromTextView(implicit input: BITextView): InstructionWriteActions = InstructionWriteActions("actions")
  }

  private class InstructionApplyActionsBuilder extends InstructionActionsBuilder[InstructionApplyActions] {
    override private[fixed] def inputFromTextView(implicit input: BITextView): InstructionApplyActions = InstructionApplyActions("actions")
  }

  private class InstructionClearActionsBuilder extends InstructionActionsBuilder[InstructionClearActions] {
    override private[fixed] def inputFromTextView(implicit input: BITextView): InstructionClearActions = InstructionClearActions()
  }

  private class InstructionMeterBuilder extends OfpStructureBuilder[InstructionMeter] {
    protected def applyInput(input: InstructionMeter): Unit = {
      setMember("meter_id", input.meterId)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): InstructionMeter = InstructionMeter("meter_id")
  }

  private class InstructionExperimenterBuilder extends OfpStructureBuilder[InstructionExperimenter]{
    protected def applyInput(input: InstructionExperimenter): Unit = {
      setMember("experimenter", input.experimenter)
      setMember("data", input.data)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): InstructionExperimenter = InstructionExperimenter("experimenter")
  }

  protected abstract override def builderClasses = classOf[InstructionGotoTableBuilder]     ::
                                                   classOf[InstructionWriteMetadataBuilder] ::
                                                   classOf[InstructionWriteActionsBuilder]  ::
                                                   classOf[InstructionApplyActionsBuilder]  ::
                                                   classOf[InstructionClearActionsBuilder]  ::
                                                   classOf[InstructionMeterBuilder]         ::
                                                   classOf[InstructionExperimenterBuilder]  ::
                                                   super.builderClasses
}