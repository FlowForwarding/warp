/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.structures.table_features

import org.flowforwarding.warp.controller.api.dynamic.{DynamicStructure, DynamicBuilderInput}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView
import org.flowforwarding.warp.controller.api.fixed.v13.structures.oxm_tlv.OxmHeader.OxmData
import spire.math.{UInt, UByte}

import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.oxm_tlv._, OxmClass._, OxmMatchFields._

trait TableFeatureProperty extends BuilderInput
trait TableMiss

trait TableFeaturePropertyInstructions extends TableFeatureProperty{
  val instructions: Array[InstructionId]
}
object TableFeaturePropertyInstructions{
  def apply(_instructions: Array[InstructionId]) = new TableFeaturePropertyInstructions{
    val instructions = _instructions
  }
}

trait TableFeaturePropertyInstructionsMiss extends TableFeaturePropertyInstructions with TableMiss
object TableFeaturePropertyInstructionsMiss{
  def apply(_instructions: Array[InstructionId]) = new TableFeaturePropertyInstructionsMiss{
    val instructions = _instructions
  }
}

trait TableFeaturePropertyNextTables extends TableFeatureProperty{
  val nextTableIds: Array[UByte]
}
object TableFeaturePropertyNextTables{
  def apply(_nextTableIds: Array[UByte]) = new TableFeaturePropertyNextTables{
    val nextTableIds = _nextTableIds
  }
}

trait TableFeaturePropertyNextTablesMiss extends TableFeaturePropertyNextTables with TableMiss
object TableFeaturePropertyNextTablesMiss{
  def apply(_nextTableIds: Array[UByte]) = new TableFeaturePropertyNextTablesMiss{
    val nextTableIds = _nextTableIds
  }
}

trait TableFeaturePropertyWriteActions extends TableFeatureProperty{
  val actions: Array[ActionId]
}
object TableFeaturePropertyWriteActions{
  def apply(_actions: Array[ActionId]) = new TableFeaturePropertyWriteActions{
    val actions = _actions
  }
}

trait TableFeaturePropertyWriteActionsMiss extends TableFeaturePropertyWriteActions with TableMiss
object TableFeaturePropertyWriteActionsMiss{
  def apply(_actions: Array[ActionId]) = new TableFeaturePropertyWriteActionsMiss{
    val actions = _actions
  }
}

trait TableFeaturePropertyApplyActions extends TableFeatureProperty{
  val actions: Array[ActionId]
}
object TableFeaturePropertyApplyActions{
  def apply(_actions: Array[ActionId]) = new TableFeaturePropertyApplyActions{
    val actions = _actions
  }
}

trait TableFeaturePropertyApplyActionsMiss extends TableFeaturePropertyApplyActions with TableMiss
object TableFeaturePropertyApplyActionsMiss{
  def apply(_actions: Array[ActionId]) = new TableFeaturePropertyApplyActionsMiss{
    val actions = _actions
  }
}

trait TableFeaturePropertyMatch extends TableFeatureProperty{
  val oxm: Array[OxmData]
}
object TableFeaturePropertyMatch{
  def apply(_oxm: Array[OxmData]) = new TableFeaturePropertyMatch{
    val oxm = _oxm
  }
}

trait TableFeaturePropertyWildcards extends TableFeatureProperty{
  val oxm: Array[OxmData]
}
object TableFeaturePropertyWildcards{
  def apply(_oxm: Array[OxmData]) = new TableFeaturePropertyWildcards{
    val oxm = _oxm
  }
}

trait TableFeaturePropertyWriteSetField extends TableFeatureProperty{
  val oxm: Array[OxmData]
}
object TableFeaturePropertyWriteSetField{
  def apply(_oxm: Array[OxmData]) = new TableFeaturePropertyWriteSetField{
    val oxm = _oxm
  }
}

trait TableFeaturePropertyWriteSetFieldMiss extends TableFeaturePropertyWriteSetField with TableMiss
object TableFeaturePropertyWriteSetFieldMiss{
  def apply(_oxm: Array[OxmData]) = new TableFeaturePropertyWriteSetFieldMiss{
    val oxm = _oxm
  }
}

trait TableFeaturePropertyApplySetField extends TableFeatureProperty{
  val oxm: Array[OxmData]
}
object TableFeaturePropertyApplySetField{
  def apply(_oxm: Array[OxmData]) = new TableFeaturePropertyApplySetField{
    val oxm = _oxm
  }
}

trait TableFeaturePropertyApplySetFieldMiss extends TableFeaturePropertyApplySetField with TableMiss
object TableFeaturePropertyApplySetFieldMiss{
  def apply(_oxm: Array[OxmData]) = new TableFeaturePropertyApplySetFieldMiss{
    val oxm = _oxm
  }
}

trait TableFeaturePropertyExperimenter extends TableFeatureProperty{
  val experimenter: UInt
  val expType: UInt
  val data: Array[Byte]
}
object TableFeaturePropertyExperimenter{
  def apply(_experimenter: UInt, _expType: UInt, _data: Array[Byte]) = new TableFeaturePropertyExperimenter{
    val experimenter = _experimenter
    val expType = _expType
    val data = _data
  }
}

trait TableFeaturePropertyExperimenterMiss extends TableFeaturePropertyExperimenter with TableMiss
object TableFeaturePropertyExperimenterMiss{
  def apply(_experimenter: UInt, _expType: UInt, _data: Array[Byte]) = new TableFeaturePropertyExperimenterMiss{
    val experimenter = _experimenter
    val expType = _expType
    val data = _data
  }
}

private[fixed] trait Ofp13TableFeaturePropertyDescription extends StructureDescription {
  apiProvider: StructuresDescriptionHelper with Ofp13InstructionIdDescription with Ofp13ActionIdDescription =>

  protected[fixed] implicit object TableFeatureProperty extends FromDynamic[TableFeatureProperty] with ToDynamic[TableFeatureProperty] {
    val toDynamic: PartialFunction[TableFeatureProperty, DynamicBuilderInput] = {
      case i: TableFeaturePropertyInstructions => new TableFeaturePropertyInstructionsBuilder toDynamicInput i
      case i: TableFeaturePropertyInstructionsMiss => new TableFeaturePropertyInstructionsMissBuilder toDynamicInput i
      case i: TableFeaturePropertyNextTables => new TableFeaturePropertyNextTablesBuilder toDynamicInput i
      case i: TableFeaturePropertyNextTablesMiss => new TableFeaturePropertyNextTablesMissBuilder toDynamicInput i
      case i: TableFeaturePropertyWriteActions => new TableFeaturePropertyWriteActionsBuilder toDynamicInput i
      case i: TableFeaturePropertyWriteActionsMiss => new TableFeaturePropertyWriteActionsMissBuilder toDynamicInput i
      case i: TableFeaturePropertyApplyActions => new TableFeaturePropertyApplyActionsBuilder toDynamicInput i
      case i: TableFeaturePropertyApplyActionsMiss => new TableFeaturePropertyApplyActionsMissBuilder toDynamicInput i
      case i: TableFeaturePropertyMatch => new TableFeaturePropertyMatchBuilder toDynamicInput i
      case i: TableFeaturePropertyWildcards => new TableFeaturePropertyWildcardsBuilder toDynamicInput i
      case i: TableFeaturePropertyWriteSetField => new TableFeaturePropertyWriteSetFieldBuilder toDynamicInput i
      case i: TableFeaturePropertyWriteSetFieldMiss => new TableFeaturePropertyWriteSetFieldMissBuilder toDynamicInput i
      case i: TableFeaturePropertyApplySetField => new TableFeaturePropertyApplySetFieldBuilder toDynamicInput i
      case i: TableFeaturePropertyApplySetFieldMiss => new TableFeaturePropertyApplySetFieldMissBuilder toDynamicInput i
      case i: TableFeaturePropertyExperimenter => new TableFeaturePropertyExperimenterBuilder toDynamicInput i
      case i: TableFeaturePropertyExperimenterMiss => new TableFeaturePropertyExperimenterMissBuilder toDynamicInput i
    }

    def fromDynamic: PartialFunction[DynamicStructure, TableFeatureProperty] = {
      case s if s.ofType[TableFeaturePropertyInstructions] => new OfpStructure[TableFeaturePropertyInstructions](s) with TableFeaturePropertyInstructions {
        val instructions: Array[InstructionId] = structuresSequence("instruction_ids")
      }
      case s if s.ofType[TableFeaturePropertyInstructionsMiss] => new OfpStructure[TableFeaturePropertyInstructionsMiss](s) with TableFeaturePropertyInstructionsMiss {
        val instructions: Array[InstructionId] = structuresSequence("instruction_ids")
      }
      case s if s.ofType[TableFeaturePropertyNextTables] => new OfpStructure[TableFeaturePropertyNextTables](s) with TableFeaturePropertyNextTables {
        val nextTableIds: Array[UByte] = primitivesSequence[UByte]("next_table_ids")
      }
      case s if s.ofType[TableFeaturePropertyNextTablesMiss] => new OfpStructure[TableFeaturePropertyNextTablesMiss](s) with TableFeaturePropertyNextTablesMiss {
        val nextTableIds: Array[UByte] = primitivesSequence[UByte]("next_table_ids")
      }
      case s if s.ofType[TableFeaturePropertyWriteActions] => new OfpStructure[TableFeaturePropertyWriteActions](s) with TableFeaturePropertyWriteActions {
        val actions: Array[ActionId] = structuresSequence("action_ids")
      }
      case s if s.ofType[TableFeaturePropertyWriteActionsMiss] => new OfpStructure[TableFeaturePropertyWriteActionsMiss](s) with TableFeaturePropertyWriteActionsMiss {
        val actions: Array[ActionId] = structuresSequence("action_ids")
      }
      case s if s.ofType[TableFeaturePropertyApplyActions] => new OfpStructure[TableFeaturePropertyApplyActions](s) with TableFeaturePropertyApplyActions {
        val actions: Array[ActionId] = structuresSequence("action_ids")
      }
      case s if s.ofType[TableFeaturePropertyApplyActionsMiss] => new OfpStructure[TableFeaturePropertyApplyActionsMiss](s) with TableFeaturePropertyApplyActionsMiss {
        val actions: Array[ActionId] = structuresSequence("action_ids")
      }
      case s if s.ofType[TableFeaturePropertyMatch] => new OfpStructure[TableFeaturePropertyMatch](s) with TableFeaturePropertyMatch {
        val oxm: Array[OxmData] = primitivesSequence[UInt]("oxm_ids") map OxmHeader.deconstruct
      }
      case s if s.ofType[TableFeaturePropertyWildcards] => new OfpStructure[TableFeaturePropertyWildcards](s) with TableFeaturePropertyWildcards {
        val oxm: Array[OxmData] = primitivesSequence[UInt]("oxm_ids") map OxmHeader.deconstruct
      }
      case s if s.ofType[TableFeaturePropertyWriteSetField] => new OfpStructure[TableFeaturePropertyWriteSetField](s) with TableFeaturePropertyWriteSetField {
        val oxm: Array[OxmData] = primitivesSequence[UInt]("oxm_ids") map OxmHeader.deconstruct
      }
      case s if s.ofType[TableFeaturePropertyWriteSetFieldMiss] => new OfpStructure[TableFeaturePropertyWriteSetFieldMiss](s) with TableFeaturePropertyWriteSetFieldMiss {
        val oxm: Array[OxmData] = primitivesSequence[UInt]("oxm_ids") map OxmHeader.deconstruct
      }
      case s if s.ofType[TableFeaturePropertyApplySetField] => new OfpStructure[TableFeaturePropertyApplySetField](s) with TableFeaturePropertyApplySetField {
        val oxm: Array[OxmData] = primitivesSequence[UInt]("oxm_ids") map OxmHeader.deconstruct
      }
      case s if s.ofType[TableFeaturePropertyApplySetFieldMiss] => new OfpStructure[TableFeaturePropertyApplySetFieldMiss](s) with TableFeaturePropertyApplySetFieldMiss {
        val oxm: Array[OxmData] = primitivesSequence[UInt]("oxm_ids") map OxmHeader.deconstruct
      }
      case s if s.ofType[TableFeaturePropertyExperimenter] => new OfpStructure[TableFeaturePropertyExperimenter](s) with TableFeaturePropertyExperimenter {
        val experimenter: UInt = primitiveField[UInt]("experimenter")
        val expType: UInt = primitiveField[UInt]("experimenter_type")
        val data: Array[Byte] = bytes("experimenter_data")
      }
      case s if s.ofType[TableFeaturePropertyExperimenterMiss] => new OfpStructure[TableFeaturePropertyExperimenterMiss](s) with TableFeaturePropertyExperimenterMiss {
        val experimenter: UInt = primitiveField[UInt]("experimenter")
        val expType: UInt = primitiveField[UInt]("experimenter_type")
        val data: Array[Byte] = bytes("experimenter_data")
      }
    }
  }

  private class TableFeaturePropertyInstructionsBuilder extends OfpStructureBuilder[TableFeaturePropertyInstructions] {
    protected def applyInput(input: TableFeaturePropertyInstructions): Unit = {
      setMember("instruction_ids", input.instructions)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturePropertyInstructions = new TableFeaturePropertyInstructions {
      override val instructions: Array[InstructionId] = structures[InstructionId]("instruction_ids")
    }
  }

  private class TableFeaturePropertyInstructionsMissBuilder extends OfpStructureBuilder[TableFeaturePropertyInstructionsMiss] {
    protected def applyInput(input: TableFeaturePropertyInstructionsMiss): Unit = {
      setMember("instruction_ids", input.instructions)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturePropertyInstructionsMiss = new TableFeaturePropertyInstructionsMiss {
      override val instructions: Array[InstructionId] = structures[InstructionId]("instruction_ids")
    }
  }

  private class TableFeaturePropertyNextTablesBuilder extends OfpStructureBuilder[TableFeaturePropertyNextTables] {
    protected def applyInput(input: TableFeaturePropertyNextTables): Unit = {
      setMember("action_ids", input.nextTableIds map { _.toByte })
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturePropertyNextTables = new TableFeaturePropertyNextTables {
      override val nextTableIds: Array[UByte] = bytes("next_table_ids") map UByte.apply
    }
  }

  private class TableFeaturePropertyNextTablesMissBuilder extends OfpStructureBuilder[TableFeaturePropertyNextTablesMiss] {
    protected def applyInput(input: TableFeaturePropertyNextTablesMiss): Unit = {
      setMember("action_ids", input.nextTableIds map { _.toByte })
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturePropertyNextTablesMiss = new TableFeaturePropertyNextTablesMiss {
      override val nextTableIds: Array[UByte] = bytes("next_table_ids") map UByte.apply
    }
  }

  private class TableFeaturePropertyWriteActionsBuilder extends OfpStructureBuilder[TableFeaturePropertyWriteActions] {
    protected def applyInput(input: TableFeaturePropertyWriteActions): Unit = {
      setMember("action_ids", input.actions)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturePropertyWriteActions = new TableFeaturePropertyWriteActions {
      override val actions: Array[ActionId] = structures[ActionId]("action_ids")
    }
  }

  private class TableFeaturePropertyWriteActionsMissBuilder extends OfpStructureBuilder[TableFeaturePropertyWriteActionsMiss] {
    protected def applyInput(input: TableFeaturePropertyWriteActionsMiss): Unit = {
      setMember("action_ids", input.actions)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturePropertyWriteActionsMiss = new TableFeaturePropertyWriteActionsMiss {
      override val actions: Array[ActionId] = structures[ActionId]("action_ids")
    }
  }

  private class TableFeaturePropertyApplyActionsBuilder extends OfpStructureBuilder[TableFeaturePropertyApplyActions] {
    protected def applyInput(input: TableFeaturePropertyApplyActions): Unit = {
      setMember("action_ids", input.actions)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturePropertyApplyActions = new TableFeaturePropertyApplyActions {
      override val actions: Array[ActionId] = structures[ActionId]("action_ids")
    }
  }

  private class TableFeaturePropertyApplyActionsMissBuilder extends OfpStructureBuilder[TableFeaturePropertyApplyActionsMiss] {
    protected def applyInput(input: TableFeaturePropertyApplyActionsMiss): Unit = {
      setMember("action_ids", input.actions)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturePropertyApplyActionsMiss = new TableFeaturePropertyApplyActionsMiss {
      override val actions: Array[ActionId] = structures[ActionId]("action_ids")
    }
  }

  private class TableFeaturePropertyMatchBuilder extends OfpStructureBuilder[TableFeaturePropertyMatch] {
    protected def applyInput(input: TableFeaturePropertyMatch): Unit = {
      setMember("oxm_ids", input.oxm map OxmHeader.construct)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturePropertyMatch = new TableFeaturePropertyMatch {
      override val oxm: Array[OxmData] = ints("oxm_ids") map OxmHeader.deconstruct
    }
  }

  private class TableFeaturePropertyWildcardsBuilder extends OfpStructureBuilder[TableFeaturePropertyWildcards] {
    protected def applyInput(input: TableFeaturePropertyWildcards): Unit = {
      setMember("oxm_ids", input.oxm map OxmHeader.construct)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturePropertyWildcards = new TableFeaturePropertyWildcards {
      override val oxm: Array[OxmData] = ints("oxm_ids") map OxmHeader.deconstruct
    }
  }

  private class TableFeaturePropertyWriteSetFieldBuilder extends OfpStructureBuilder[TableFeaturePropertyWriteSetField] {
    protected def applyInput(input: TableFeaturePropertyWriteSetField): Unit = {}

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturePropertyWriteSetField = new TableFeaturePropertyWriteSetField {
      override val oxm: Array[OxmData] = ints("oxm_ids") map OxmHeader.deconstruct
    }
  }

  private class TableFeaturePropertyWriteSetFieldMissBuilder extends OfpStructureBuilder[TableFeaturePropertyWriteSetFieldMiss] {
    protected def applyInput(input: TableFeaturePropertyWriteSetFieldMiss): Unit = {
      setMember("oxm_ids", input.oxm map OxmHeader.construct)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturePropertyWriteSetFieldMiss = new TableFeaturePropertyWriteSetFieldMiss {
      override val oxm: Array[OxmData] = ints("oxm_ids") map OxmHeader.deconstruct
    }
  }

  private class TableFeaturePropertyApplySetFieldBuilder extends OfpStructureBuilder[TableFeaturePropertyApplySetField] {
    protected def applyInput(input: TableFeaturePropertyApplySetField): Unit = {
      setMember("oxm_ids", input.oxm map OxmHeader.construct)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturePropertyApplySetField = new TableFeaturePropertyApplySetField {
      override val oxm: Array[OxmData] = ints("oxm_ids") map OxmHeader.deconstruct
    }
  }

  private class TableFeaturePropertyApplySetFieldMissBuilder extends OfpStructureBuilder[TableFeaturePropertyApplySetFieldMiss] {
    protected def applyInput(input: TableFeaturePropertyApplySetFieldMiss): Unit = {
      setMember("oxm_ids", input.oxm map OxmHeader.construct)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturePropertyApplySetFieldMiss = new TableFeaturePropertyApplySetFieldMiss {
      override val oxm: Array[OxmData] = ints("oxm_ids") map OxmHeader.deconstruct
    }
  }

  private class TableFeaturePropertyExperimenterBuilder extends OfpStructureBuilder[TableFeaturePropertyExperimenter] {
    protected def applyInput(input: TableFeaturePropertyExperimenter): Unit = {
      setMember("experimenter", input.experimenter)
      setMember("exp_type", input.expType)
      setMember("experimenter_data", input.data)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturePropertyExperimenter = new TableFeaturePropertyExperimenter {
      override val experimenter: UInt = "experimenter"
      override val expType: UInt = "exp_type"
      override val data: Array[Byte] = "experimenter_data"
    }
  }

  private class TableFeaturePropertyExperimenterMissBuilder extends OfpStructureBuilder[TableFeaturePropertyExperimenterMiss] {
    protected def applyInput(input: TableFeaturePropertyExperimenterMiss): Unit = {
      setMember("experimenter", input.experimenter)
      setMember("exp_type", input.expType)
      setMember("experimenter_data", input.data)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturePropertyExperimenterMiss = new TableFeaturePropertyExperimenterMiss {
      override val experimenter: UInt = "experimenter"
      override val expType: UInt = "exp_type"
      override val data: Array[Byte] = "experimenter_data"
    }
  }

  protected abstract override def builderClasses = classOf[TableFeaturePropertyInstructionsBuilder] ::
                                                   classOf[TableFeaturePropertyInstructionsMissBuilder] ::
                                                   classOf[TableFeaturePropertyNextTablesBuilder] ::
                                                   classOf[TableFeaturePropertyNextTablesMissBuilder] ::
                                                   classOf[TableFeaturePropertyWriteActionsBuilder] ::
                                                   classOf[TableFeaturePropertyWriteActionsMissBuilder] ::
                                                   classOf[TableFeaturePropertyApplyActionsBuilder] ::
                                                   classOf[TableFeaturePropertyApplyActionsMissBuilder] ::
                                                   classOf[TableFeaturePropertyMatchBuilder] ::
                                                   classOf[TableFeaturePropertyWildcardsBuilder] ::
                                                   classOf[TableFeaturePropertyWriteSetFieldBuilder] ::
                                                   classOf[TableFeaturePropertyWriteSetFieldMissBuilder] ::
                                                   classOf[TableFeaturePropertyApplySetFieldBuilder] ::
                                                   classOf[TableFeaturePropertyApplySetFieldMissBuilder] ::
                                                   classOf[TableFeaturePropertyExperimenterBuilder] ::
                                                   classOf[TableFeaturePropertyExperimenterMissBuilder] ::
                                                   super.builderClasses
}
