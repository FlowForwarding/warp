/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.structures.meter_bands

import spire.math._

import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.dynamic.{DynamicBuilderInput, DynamicStructure}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

// TODO: think about refactoring
private[fixed] trait MeterBandHeader extends BuilderInput {
  def rate: ULong
  def burstSize: ULong
}

private[fixed] object MeterBandHeader{
  def apply(_rate: ULong, _burstSize: ULong): MeterBandHeader = new MeterBandHeader{
    def rate = _rate
    def burstSize = _burstSize
  }
}

private[fixed] trait MeterBand extends BuilderInput {
  def rate: ULong
  def burstSize: ULong
  private[fixed] def header = MeterBandHeader(rate, burstSize)
}

trait MeterBandDrop extends MeterBand
object MeterBandDrop{
  def build(_rate: ULong, _burstSize: ULong) = new MeterBandDrop {
    val rate = _rate; val burstSize = _burstSize
  }
}

trait MeterBandDscpRemark extends MeterBand { val precLevel: UShort }
object MeterBandDscpRemark{
  def build(_rate: ULong, _burstSize: ULong, _precLevel: UShort) = new MeterBandDscpRemark {
    val rate = _rate; val burstSize = _burstSize; val precLevel = _precLevel
  }
}

trait MeterBandExperimenter extends MeterBand { val experimenter: UInt }
object MeterBandExperimenter{
  def build(_rate: ULong, _burstSize: ULong, _experimenter: UInt) = new MeterBandExperimenter {
    val rate = _rate; val burstSize = _burstSize; val experimenter = _experimenter
  }
}

private[fixed] trait Ofp13MeterBandsDescription extends StructureDescription{
  apiProvider: StructuresDescriptionHelper =>

  protected[fixed] implicit object MeterBandHeader extends FromDynamic[MeterBandHeader] with ToDynamic[MeterBandHeader]{
    def toDynamic: PartialFunction[MeterBandHeader, DynamicBuilderInput] = { case h => new MeterHeaderBuilder toDynamicInput h }

    def fromDynamic: PartialFunction[DynamicStructure, MeterBandHeader] = {
      case s if s.ofType[MeterBandHeader] => new OfpStructure[MeterBandHeader](s) with MeterBandHeader {
        def rate      = primitiveField[ULong]("rate")
        def burstSize = primitiveField[ULong]("burst_size")
      }
    }
  }

  private class MeterHeaderBuilder extends OfpStructureBuilder[MeterBandHeader]{
    protected def applyInput(input: MeterBandHeader): Unit = {
      setMember("rate", input.rate)
      setMember("burst_size", input.burstSize)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): MeterBandHeader =
      throw new UnsupportedOperationException("MeterBandHeader should not be created manually.")
  }

  protected[fixed] implicit object MeterBand extends FromDynamic[MeterBand] with ToDynamic[MeterBand]{

    val toDynamic: PartialFunction[MeterBand, DynamicBuilderInput] = {
      case drop: MeterBandDrop => new DropBuilder toDynamicInput drop
      case dscpRemark: MeterBandDscpRemark => new DscpRemarkBuilder toDynamicInput dscpRemark
      case experimenter: MeterBandExperimenter => new ExperimenterBuilder toDynamicInput experimenter
    }

    def fromDynamic: PartialFunction[DynamicStructure, MeterBand] = {
      case s if s.ofType[MeterBandDrop] => new OfpStructure[MeterBandDrop](s) with MeterBandDrop {
        override val header = structureField[MeterBandHeader]("header")
        override def rate      = header.rate
        override def burstSize = header.burstSize
      }
      case s if s.ofType[MeterBandDscpRemark] => new OfpStructure[MeterBandDscpRemark](s) with MeterBandDscpRemark {
        override val header = structureField[MeterBandHeader]("header")
        override def rate      = header.rate
        override def burstSize = header.burstSize

        val precLevel = primitiveField[UShort]("prec_level")
      }
      case s if s.ofType[MeterBandExperimenter] => new OfpStructure[MeterBandExperimenter](s) with MeterBandExperimenter {
        override val header = structureField[MeterBandHeader]("header")
        override def rate      = header.rate
        override def burstSize = header.burstSize

        val experimenter = primitiveField[UInt]("experimenter")
      }
    }
  }

  private class DropBuilder extends OfpStructureBuilder[MeterBandDrop]{
    protected def applyInput(input: MeterBandDrop): Unit = {
      setMember("header", input.header)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): MeterBandDrop = new MeterBandDrop{
      override def burstSize: ULong = "burst_size"

      override def rate: ULong = "rate"
    }
  }

  private class DscpRemarkBuilder extends OfpStructureBuilder[MeterBandDscpRemark]{
    protected def applyInput(input: MeterBandDscpRemark): Unit = {
      setMember("header", input.header)
      setMember("prec_level", input.precLevel)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): MeterBandDscpRemark = new MeterBandDscpRemark{
      override val precLevel: UShort = "prec_level"

      override def burstSize: ULong = "burst_size"

      override def rate: ULong = "rate"
    }
  }

  private class ExperimenterBuilder extends OfpStructureBuilder[MeterBandExperimenter]{
    protected def applyInput(input: MeterBandExperimenter): Unit = {
      setMember("header", input.header)
      setMember("experimenter", input.experimenter)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): MeterBandExperimenter = new MeterBandExperimenter{
      override val experimenter: UInt = "exprerimenter"

      override def burstSize: ULong = "burst_size"

      override def rate: ULong = "rate"
    }
  }

  protected abstract override def builderClasses = classOf[DropBuilder] :: classOf[DscpRemarkBuilder] :: classOf[ExperimenterBuilder] :: super.builderClasses
}
