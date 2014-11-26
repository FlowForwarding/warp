/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.data

import spire.math._

import org.flowforwarding.warp.controller.api.dynamic.{DynamicBuilderInput, DynamicStructure}
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.table_features.{Ofp13TableFeaturePropertyDescription, TableFeatureProperty}
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.{ArrayMultipartRequestBodyInput, MultipartReplyBody}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

case class TableFeaturesRequestBodyInput(structures: Array[TableFeatures]) extends ArrayMultipartRequestBodyInput[TableFeatures]

trait TableFeatures extends BuilderInput{
  /** Identifier of table. Lower numbered tables are consulted first. */
  val tableId: UByte
  /** Name. */
  val name: String
  /** Bits of metadata table can match. */
  val metadataMatch: ULong
  /** Bits of metadata table can write. */
  val metadataWrite: ULong
  /** Bitmap of OFPTC_* values */
  val config: UInt
  /** Max number of entries supported. */
  val maxEntries: UInt
  /** Table Feature Property list */
  val properties: Array[TableFeatureProperty]
}

object TableFeatures {
  def apply(
    _tableId: UByte,
    _name: String,
    _metadataMatch: ULong,
    _metadataWrite: ULong,
    _config: UInt,
    _maxEntries: UInt,
    _properties: Array[TableFeatureProperty]): TableFeatures = {
    new TableFeatures {
      val tableId: UByte = _tableId
      val name: String = _name
      val metadataMatch: ULong = _metadataMatch
      val metadataWrite: ULong = _metadataWrite
      val maxEntries: UInt = _maxEntries
      val config: UInt = _config
      val properties: Array[TableFeatureProperty] = _properties
    }
  }
}

trait TableFeaturesReplyBody extends MultipartReplyBody[Array[TableFeatures]]

trait TableFeaturesReplyHandler{
  def onTableFeaturesReply(dpid: ULong, msg: Array[TableFeatures]): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13TableFeaturesDescription extends StructureDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with TableFeaturesReplyHandler]
  with Ofp13TableFeaturePropertyDescription =>

  class TableFeaturesBuilder extends OfpStructureBuilder[TableFeatures] {
    protected def applyInput(input: TableFeatures): Unit = {
      setMember("table_id", input.tableId)
      setMember("name", input.name)
      setMember("metadata_match", input.metadataMatch)
      setMember("metadata_write", input.metadataWrite)
      setMember("max_entries", input.maxEntries)
      setMember("config", input.config)
      setMember("properties", input.properties)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeatures =
      org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.data.TableFeatures(
          "table_id",
          string("name"),
          "metadata_match",
          "metadata_write",
          "max_entries",
          "config",
          structures[TableFeatureProperty]("properties"))
  }

  implicit object TableFeatures extends FromDynamic[TableFeatures] with ToDynamic[TableFeatures]{
    val fromDynamic: PartialFunction[DynamicStructure, TableFeatures] = {
      case s if s.ofType[SwitchDescription] => new OfpStructure[TableFeatures](s) with TableFeatures {
        val tableId = primitiveField[UByte]("table_id")
        val name = stringField("name")
        val maxEntries = primitiveField[UInt]("max_entries")
        val metadataWrite = primitiveField[ULong]("metadata_write")
        val config = primitiveField[UInt]("config")
        val properties = structuresSequence[TableFeatureProperty]("properties")
        val metadataMatch = primitiveField[ULong]("metadata_match")
      }
    }

    override def toDynamic: PartialFunction[TableFeatures, DynamicBuilderInput] = {
      case s => new TableFeaturesBuilder toDynamicInput s
    }
  }

  protected class TableFeaturesRequestBodyInputBuilder extends OfpStructureBuilder[TableFeaturesRequestBodyInput] {
    protected def applyInput(input: TableFeaturesRequestBodyInput): Unit = {
      setMember("structures", input.structures)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): TableFeaturesRequestBodyInput =
      TableFeaturesRequestBodyInput(structures[TableFeatures]("structures"))
  }

  abstract override def builderClasses = classOf[TableFeaturesRequestBodyInputBuilder] :: super.builderClasses
}