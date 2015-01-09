/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.symmetric

import org.flowforwarding.warp.driver_api.fixed.SpecificVersionMessageHandlersSet
import spire.math._

import org.flowforwarding.warp.driver_api.dynamic._
import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.Ofp13HeaderDescription
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.{Ofp13MessageInput, Ofp13Message, Ofp13MessageDescription}
import org.flowforwarding.warp.driver_api.fixed.text_view.BITextView

trait HelloElem extends BuilderInput

trait HelloElemVersionBitmap extends HelloElem {
  val bitmaps: Array[UInt]
}

trait Hello extends Ofp13Message{
  def elems: Array[HelloElem]
}

case class HelloInput(elems: Array[HelloElem]) extends Ofp13MessageInput

private[fixed] trait HelloHandler{
  def onHello(dpid: ULong, msg: Hello): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13HelloDescription extends Ofp13MessageDescription{
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlersSet[_, _] with HelloHandler] with Ofp13HeaderDescription =>

  protected[fixed] implicit object HelloElem extends FromDynamic[HelloElem] with ToDynamic[HelloElem]{

    val toDynamic: PartialFunction[HelloElem, DynamicBuilderInput] = {
      case h: HelloElemVersionBitmap => new HelloElemVersionBitmapBuilder toDynamicInput h
    }

    val fromDynamic: PartialFunction[DynamicStructure, HelloElem] = {
      case s if s.ofType[HelloElemVersionBitmap] =>
        new OfpStructure[HelloElemVersionBitmap](s) with HelloElemVersionBitmap {
          val bitmaps = primitivesSequence[UInt]("bitmaps")
        }
    }
  }

  private class HelloElemVersionBitmapBuilder extends OfpStructureBuilder[HelloElemVersionBitmap] {
    // Fills the underlying builder with the specified input.
    protected def applyInput(input: HelloElemVersionBitmap): Unit =  {
      setMember("bitmaps", input.bitmaps)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): HelloElemVersionBitmap =
      new HelloElemVersionBitmap { val bitmaps: Array[UInt] = "bitmaps" }
  }

  private class HelloBuilder extends OfpMessageBuilder[HelloInput]{
    // TODO: Implement in all subclasses
    override private[fixed] def inputFromTextView(implicit input: BITextView): HelloInput = HelloInput("elems")

    // Fills the underlying builder with the specified input.
    protected override def applyInput(input: HelloInput): Unit = {
      setMember("elems", input.elems)
    }
  }

  private class HelloStructure(s: DynamicStructure) extends OfpMessage[Hello](s) with Hello {
    def elems: Array[HelloElem] = structuresSequence("elements")
  }

  protected abstract override def builderClasses = classOf[HelloBuilder] :: classOf[HelloElemVersionBitmapBuilder] :: super.builderClasses
  protected abstract override def messageClasses = classOf[HelloStructure] :: super.messageClasses
}