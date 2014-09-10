/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.dynamic

import scala.util.Try
import org.flowforwarding.warp.controller.driver_interface._

trait DynamicStructure extends OFMessage{
  def primitiveField(name: String): Long
  def structureField(name: String): DynamicStructure
  def primitivesSequence(name: String): Array[Long]
  def structuresSequence(name: String): Array[DynamicStructure]
  def isTypeOf(typeName: String): Boolean
}

sealed trait DynamicMember
case class DynamicPrimitive(v: Long) extends DynamicMember
case class DynamicPrimitives(v: Array[Long]) extends DynamicMember
case class DynamicStructureInput(v: DynamicBuilderInput) extends DynamicMember
case class DynamicStructureInputs(v: Array[DynamicBuilderInput]) extends DynamicMember

trait DynamicBuilderInput{
  val structureName: String
  def getMembers: Map[String, DynamicMember]
  def setMember(memberName: String, value: DynamicMember): DynamicBuilderInput
}

trait DynamicStructureBuilder[+E <: DynamicStructure] extends OfpVersionSupport{
  def build(input: DynamicBuilderInput): Try[E]

  protected class MapBasedDynamicBuilderInput(val structureName: String) extends DynamicBuilderInput{
    private var args = Map[String, DynamicMember]()

    override def setMember(memberName: String, value: DynamicMember): DynamicBuilderInput = {
      val kv = (memberName, value)
      args += kv
      this
    }

    override def getMembers: Map[String, DynamicMember] = args
  }

  def newBuilderInput(msgType: String): DynamicBuilderInput = new MapBasedDynamicBuilderInput(msgType)
}

trait DynamicDriver[E <: DynamicStructure] extends MessageDriver[E] with DynamicStructureBuilder[E]

trait DynamicMessageDriverFactory[E <: DynamicStructure] extends MessageDriverFactory[E]
