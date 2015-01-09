/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart.data

import org.flowforwarding.warp.driver_api.fixed.SpecificVersionMessageHandlersSet
import spire.math._

import org.flowforwarding.warp.driver_api.dynamic.{DynamicBuilderInput, DynamicStructure}
import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.table_features.{Ofp13TableFeaturePropertyDescription, TableFeatureProperty}
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart.{SingletonMultipartRequestBodyInput, MultipartReplyBody}
import org.flowforwarding.warp.driver_api.fixed.text_view.BITextView

case class MultipartExperimenterRequestBodyInput(structure: MultipartExperimenter) extends SingletonMultipartRequestBodyInput[MultipartExperimenter]

trait MultipartExperimenter extends BuilderInput{
  /** Experimenter ID which takes the same form as in struct ofp_experimenter_header. */
  val experimenter: UInt
  /** Experimenter defined. */
  val expType: UInt
  /** Experimenter-defined arbitrary additional data. */
  val data: Array[Byte]
}

object MultipartExperimenter {
  def apply(_experimenter: UInt,
            _expType: UInt,
            _data: Array[Byte]) = {
    new MultipartExperimenter {
      val experimenter: UInt = _experimenter
      val expType: UInt = _expType
      val data: Array[Byte] = _data
    }
  }
}

trait MultipartExperimenterReplyBody extends MultipartReplyBody[MultipartExperimenter]

trait MultipartExperimenterReplyHandler{
  def onMultipartExperimenterReply(dpid: ULong, msg: MultipartExperimenter): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13MultipartExperimenterDescription extends StructureDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _] with MultipartExperimenterReplyHandler]
    with Ofp13TableFeaturePropertyDescription =>

  class MultipartExperimenterBuilder extends OfpStructureBuilder[MultipartExperimenter] {
    protected def applyInput(input: MultipartExperimenter): Unit = {
      setMember("experimenter", input.experimenter)
      setMember("exp_type", input.expType)
      setMember("data", input.data)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): MultipartExperimenter =
      org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart.data.MultipartExperimenter(
        "experimenter",
        "exp_type",
        "data")
  }

  implicit object MultipartExperimenter extends FromDynamic[MultipartExperimenter] with ToDynamic[MultipartExperimenter]{
    val fromDynamic: PartialFunction[DynamicStructure, MultipartExperimenter] = {
      case s if s.ofType[SwitchDescription] => new OfpStructure[MultipartExperimenter](s) with MultipartExperimenter {
        val experimenter = primitiveField[UInt]("experimenter")
        val expType = primitiveField[UInt]("exp_type")
        val data = bytes("data")
      }
    }

    override def toDynamic: PartialFunction[MultipartExperimenter, DynamicBuilderInput] = {
      case s => new MultipartExperimenterBuilder toDynamicInput s
    }
  }

  protected class MultipartExperimenterRequestBodyInputBuilder extends OfpStructureBuilder[MultipartExperimenterRequestBodyInput] {
    protected def applyInput(input: MultipartExperimenterRequestBodyInput): Unit = {
      setMember("structure", input.structure)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): MultipartExperimenterRequestBodyInput =
      MultipartExperimenterRequestBodyInput(structure[MultipartExperimenter]("structure"))
  }

  abstract override def builderClasses = classOf[MultipartExperimenterRequestBodyInputBuilder] :: super.builderClasses
}