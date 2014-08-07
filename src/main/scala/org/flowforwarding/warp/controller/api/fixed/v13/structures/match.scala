package org.flowforwarding.warp.controller.api.fixed.v13.structures

import spire.math._

import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.dynamic.{DynamicBuilderInput, DynamicStructure}
import org.flowforwarding.warp.controller.api.fixed.v13.structures.oxm_tlv.{Ofp13OxmTlvDescription, OxmTlv}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

trait Match{
  val oxm: Boolean
  val fields: Array[OxmTlv[_]]
  val standard = !oxm
}

case class MatchInput(oxm: Boolean, fields: Array[OxmTlv[_]]) extends BuilderInput

private[fixed] trait Ofp13MatchDescription extends StructureDescription{
  apiProvider: StructuresDescriptionHelper with Ofp13OxmTlvDescription =>

  protected[fixed] implicit object Match extends FromDynamic[Match] with ToDynamic[MatchInput]{

    val fromDynamic: PartialFunction[DynamicStructure, Match] = {
      case s if s.ofType[Match] => new OfpStructure[Match](s) with Match {
        val oxm: Boolean = primitiveField[UShort]("oxm") != UShort(0)
        val fields: Array[OxmTlv[_]] = structuresSequence("fields")
      }
    }

    val toDynamic: PartialFunction[MatchInput, DynamicBuilderInput] = { case h => new MatchBuilder toDynamicInput h }
  }

  private class MatchBuilder extends OfpStructureBuilder[MatchInput]{
    protected def applyInput(input: MatchInput): Unit = {
      setMember("oxm", UShort(if (input.oxm) 1 else 0))
      setMember("fields", input.fields)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): MatchInput = MatchInput("oxm", "fields")
  }

  protected abstract override def builderClasses = classOf[MatchBuilder] :: super.builderClasses
}
