/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.table_features

import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.fixed.text_view.BITextView
import org.flowforwarding.warp.driver_api.dynamic.{DynamicStructure, DynamicBuilderInput}

trait InstructionId extends BuilderInput
case class InstructionGotoTableId() extends InstructionId
case class InstructionWriteMetadataId() extends InstructionId
case class InstructionWriteActionsId() extends InstructionId
case class InstructionApplyActionsId() extends InstructionId
case class InstructionClearActionsId() extends InstructionId
case class InstructionMeterId() extends InstructionId
case class InstructionExperimenterId() extends InstructionId // TODO: experimenter data?

private[fixed] trait Ofp13InstructionIdDescription extends StructureDescription{
  apiProvider: StructuresDescriptionHelper =>

  protected implicit object InstructionId extends ToDynamic[InstructionId] with FromDynamic[InstructionId]{
    val toDynamic: PartialFunction[InstructionId, DynamicBuilderInput] = {
      case i: InstructionGotoTableId     => new InstructionGotoTableIdBuilder     toDynamicInput i
      case i: InstructionWriteMetadataId => new InstructionWriteMetadataIdBuilder toDynamicInput i
      case i: InstructionWriteActionsId  => new InstructionWriteActionsIdBuilder  toDynamicInput i
      case i: InstructionApplyActionsId  => new InstructionApplyActionsIdBuilder  toDynamicInput i
      case i: InstructionClearActionsId  => new InstructionClearActionsIdBuilder  toDynamicInput i
      case i: InstructionMeterId         => new InstructionMeterIdBuilder         toDynamicInput i
      case i: InstructionExperimenterId  => new InstructionExperimenterIdBuilder  toDynamicInput i
    }

    val fromDynamic: PartialFunction[DynamicStructure, InstructionId] = {
      case s if s.ofType[InstructionGotoTableId]     => InstructionGotoTableId()
      case s if s.ofType[InstructionWriteMetadataId] => InstructionWriteMetadataId()
      case s if s.ofType[InstructionWriteActionsId]  => InstructionWriteActionsId()
      case s if s.ofType[InstructionApplyActionsId]  => InstructionApplyActionsId()
      case s if s.ofType[InstructionClearActionsId]  => InstructionClearActionsId()
      case s if s.ofType[InstructionMeterId]         => InstructionMeterId()
      case s if s.ofType[InstructionExperimenterId]  => InstructionExperimenterId()
    }
  }

  private trait InstructionIdBuilder[Input <: InstructionId] extends OfpStructureBuilder[Input] {
    protected def applyInput(input: Input): Unit = { }
  }

  private class InstructionGotoTableIdBuilder extends InstructionIdBuilder[InstructionGotoTableId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): InstructionGotoTableId = InstructionGotoTableId()
  }

  private class InstructionWriteMetadataIdBuilder extends InstructionIdBuilder[InstructionWriteMetadataId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): InstructionWriteMetadataId = InstructionWriteMetadataId()
  }

  private class InstructionWriteActionsIdBuilder extends InstructionIdBuilder[InstructionWriteActionsId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): InstructionWriteActionsId = InstructionWriteActionsId()
  }
  
  private class InstructionApplyActionsIdBuilder extends InstructionIdBuilder[InstructionApplyActionsId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): InstructionApplyActionsId = InstructionApplyActionsId()
  }
  
  private class InstructionClearActionsIdBuilder extends InstructionIdBuilder[InstructionClearActionsId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): InstructionClearActionsId = InstructionClearActionsId()
  }
  
  private class InstructionMeterIdBuilder extends InstructionIdBuilder[InstructionMeterId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): InstructionMeterId = InstructionMeterId()
  }
  
  private class InstructionExperimenterIdBuilder extends InstructionIdBuilder[InstructionExperimenterId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): InstructionExperimenterId = InstructionExperimenterId()
  }

  protected abstract override def builderClasses = classOf[InstructionGotoTableIdBuilder]     ::
                                                   classOf[InstructionWriteMetadataIdBuilder] ::
                                                   classOf[InstructionWriteActionsIdBuilder]  ::
                                                   classOf[InstructionApplyActionsIdBuilder]  ::
                                                   classOf[InstructionClearActionsIdBuilder]  ::
                                                   classOf[InstructionMeterIdBuilder]         ::
                                                   classOf[InstructionExperimenterIdBuilder]  ::
                                                   super.builderClasses
}