/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.structures

import spire.math._

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

trait Header {
  val xid: UInt
}

case class HeaderInput(xid: UInt) extends BuilderInput

private[fixed] trait Ofp13HeaderDescription extends StructureDescription{
  apiProvider: StructuresDescriptionHelper =>

  protected[fixed] implicit object Header extends FromDynamic[Header] with ToDynamic[HeaderInput]{

    val fromDynamic: PartialFunction[DynamicStructure, Header] = {
      case s if s.ofType[Header] => new OfpStructure[Header](s) with Header {
        val xid = primitiveField[UInt]("xid")
      }
    }

    val toDynamic: PartialFunction[HeaderInput, DynamicBuilderInput] = { case h => new HeaderBuilder toDynamicInput h }
  }

  private class HeaderBuilder extends OfpStructureBuilder[HeaderInput]{
    protected def applyInput(input: HeaderInput): Unit = {
      setMember("xid", input.xid)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): HeaderInput = HeaderInput("xid")
  }

  protected abstract override def builderClasses = classOf[HeaderBuilder] :: super.builderClasses
}
