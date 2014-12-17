/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.actions

import spire.math._

import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.dynamic.{DynamicBuilderInput, DynamicStructure}
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.{Max, ControllerMaxLength}
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.oxm_tlv.{Ofp13OxmTlvDescription, OxmTlv}
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.{GroupId, PortNumber}
import org.flowforwarding.warp.driver_api.fixed.text_view.BITextView

trait Action extends BuilderInput

trait ActionOutput extends Action {
  val port: PortNumber
  val maxLength: ControllerMaxLength
}

trait ActionGroup extends Action {
  val group: GroupId
}

trait ActionSetQueue extends Action {
  val queueId: UInt
}

trait ActionSetMplsTtl extends Action {
  val mplsTtl: UByte
}

trait ActionDecMplsTtl extends Action

trait ActionSetNwTtl extends Action {
  val nwTtl: UByte
}

trait ActionDecNwTtl extends Action

trait ActionCopyTtlOut extends Action

trait ActionCopyTtlIn extends Action

trait ActionPushVlan extends Action {
  val ethertype: UShort
}

trait ActionPushMpls extends Action {
  val ethertype: UShort
}

trait ActionPopVlan extends Action

trait ActionPopMpls extends Action {
  val ethertype: UShort
}

trait ActionSetField extends Action {
  def field: OxmTlv[_]
}

trait ActionPushPbb extends Action {
  val ethertype: UShort
}

trait ActionPopPbb extends Action

trait ActionExperimenter extends Action {
  val experimenter: UInt
}

object Action {
  def output(_port: PortNumber, _maxLength: ControllerMaxLength = Max)
                                                                    = new ActionOutput       { val port = _port; val maxLength = _maxLength }
  def group(_groupId: GroupId)                                      = new ActionGroup        { val group = _groupId }
  def setQueue(_queueId: UInt)                                      = new ActionSetQueue     { val queueId = _queueId }

  def setMplsTtl(_mplsTtl: UByte)                                   = new ActionSetMplsTtl   { val mplsTtl = _mplsTtl }
  def decMplsTtl                                                    = new ActionDecMplsTtl   { }
  def setNwTtl(_nwTtl: UByte)                                       = new ActionSetNwTtl     { val nwTtl = _nwTtl }

  def decNwTtl                                                      = new ActionDecNwTtl     { }
  def copyTtlOut                                                    = new ActionCopyTtlOut   { }
  def copyTtlIn                                                     = new ActionCopyTtlIn    { }

  def pushVlan(_ethertype: UShort = UShort(33024))                  = new ActionPushVlan     { val ethertype = _ethertype }
  def pushMpls(_ethertype: UShort = UShort(34887))                  = new ActionPushMpls     { val ethertype = _ethertype }
  def popVlan                                                       = new ActionPopVlan      { }
  def pushPbb(_ethertype: UShort)                                   = new ActionPushPbb      { val ethertype = _ethertype }
  def popPbb                                                        = new ActionPopPbb       { }
  def popMpls(_ethertype: UShort = UShort(2048))                    = new ActionPopMpls      { val ethertype = _ethertype }
  def setField(_field: OxmTlv[_])                                   = new ActionSetField     { def field = _field }
  def experimenter(_experimenter: UInt)                             = new ActionExperimenter { val experimenter = _experimenter }
}

private[fixed] trait Ofp13ActionsDescription extends StructureDescription{
  apiProvider: StructuresDescriptionHelper with Ofp13OxmTlvDescription =>

  protected[fixed] implicit object Action extends FromDynamic[Action] with ToDynamic[Action]{

    val toDynamic: PartialFunction[Action, DynamicBuilderInput] = {
      case a: ActionOutput       => new ActionOutputBuilder toDynamicInput a        
      case a: ActionGroup        => new ActionGroupBuilder toDynamicInput a          
      case a: ActionSetQueue     => new ActionSetQueueBuilder toDynamicInput a       
      case a: ActionSetMplsTtl   => new ActionSetMplsTtlBuilder toDynamicInput a     
      case a: ActionDecMplsTtl   => new ActionDecMplsTtlBuilder toDynamicInput a   
      case a: ActionSetNwTtl     => new ActionSetNwTtlBuilder toDynamicInput a     
      case a: ActionDecNwTtl     => new ActionDecNwTtlBuilder toDynamicInput a     
      case a: ActionCopyTtlOut   => new ActionCopyTtlOutBuilder toDynamicInput a   
      case a: ActionCopyTtlIn    => new ActionCopyTtlInBuilder toDynamicInput a    
      case a: ActionPushVlan     => new ActionPushVlanBuilder toDynamicInput a     
      case a: ActionPushMpls     => new ActionPushMplsBuilder toDynamicInput a     
      case a: ActionPopVlan      => new ActionPopVlanBuilder toDynamicInput a      
      case a: ActionPopMpls      => new ActionPopMplsBuilder toDynamicInput a
      case a: ActionPopPbb       => new ActionPopPbbBuilder toDynamicInput a
      case a: ActionPushPbb      => new ActionPushPbbBuilder toDynamicInput a
      case a: ActionSetField     => new ActionSetFieldBuilder toDynamicInput a     
      case a: ActionExperimenter => new ActionExperimenterBuilder toDynamicInput a   
    }
    
    val fromDynamic: PartialFunction[DynamicStructure, Action] = {
      case s if s.ofType[ActionGroup]        => new OfpStructure[ActionGroup](s)        with ActionGroup        { val group = GroupId(primitiveField[UInt]("group_id")) }
      case s if s.ofType[ActionSetQueue]     => new OfpStructure[ActionSetQueue](s)     with ActionSetQueue     { val queueId = primitiveField[UInt]("queue_id") }
      case s if s.ofType[ActionSetMplsTtl]   => new OfpStructure[ActionSetMplsTtl](s)   with ActionSetMplsTtl   { val mplsTtl = primitiveField[UByte]("mpls_ttl") }
      case s if s.ofType[ActionDecMplsTtl]   => new OfpStructure[ActionDecMplsTtl](s)   with ActionDecMplsTtl
      case s if s.ofType[ActionSetNwTtl]     => new OfpStructure[ActionSetNwTtl](s)     with ActionSetNwTtl     { val nwTtl = primitiveField[UByte]("nw_ttl") }
      case s if s.ofType[ActionDecNwTtl]     => new OfpStructure[ActionDecNwTtl](s)     with ActionDecNwTtl
      case s if s.ofType[ActionCopyTtlOut]   => new OfpStructure[ActionCopyTtlOut](s)   with ActionCopyTtlOut
      case s if s.ofType[ActionCopyTtlIn]    => new OfpStructure[ActionCopyTtlIn](s)    with ActionCopyTtlIn
      case s if s.ofType[ActionPushVlan]     => new OfpStructure[ActionPushVlan](s)     with ActionPushVlan     { val ethertype = primitiveField[UShort]("ethertype") }
      case s if s.ofType[ActionPushMpls]     => new OfpStructure[ActionPushMpls](s)     with ActionPushMpls     { val ethertype = primitiveField[UShort]("ethertype") }
      case s if s.ofType[ActionPopVlan]      => new OfpStructure[ActionPopVlan](s)      with ActionPopVlan
      case s if s.ofType[ActionPopMpls]      => new OfpStructure[ActionPopMpls](s)      with ActionPopMpls      { val ethertype = primitiveField[UShort]("ethertype") }
      case s if s.ofType[ActionSetField]     => new OfpStructure[ActionSetField](s)     with ActionSetField     { val field = structureField[OxmTlv[_]]("field") }
      case s if s.ofType[ActionExperimenter] => new OfpStructure[ActionExperimenter](s) with ActionExperimenter { val experimenter = primitiveField[UInt]("experimenter") }
      case s if s.ofType[ActionPushPbb]      => new OfpStructure[ActionPushPbb](s)      with ActionPushPbb      { val ethertype = primitiveField[UShort]("ethertype") }
      case s if s.ofType[ActionPopPbb]       => new OfpStructure[ActionPopPbb](s)       with ActionPopPbb
      case s if s.ofType[ActionOutput]       => new OfpStructure[ActionOutput](s)       with ActionOutput       {
        val port = PortNumber(primitiveField[UInt]("port"))
        val maxLength =  ControllerMaxLength(primitiveField[UShort]("max_len"))
      }
    }
  }

  private class ActionOutputBuilder extends OfpStructureBuilder[ActionOutput]{
    protected def applyInput(input: ActionOutput): Unit = {
      setMember("port", input.port.number)
      setMember("max_len", input.maxLength.v)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionOutput = new ActionOutput{
      override val maxLength: ControllerMaxLength = ControllerMaxLength("max_len")
      override val port: PortNumber = PortNumber("port")
    }
  }

  private class ActionGroupBuilder extends OfpStructureBuilder[ActionGroup]{
    protected def applyInput(input: ActionGroup): Unit = {
      setMember("group_id", input.group.id)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionGroup = new ActionGroup{
      override val group: GroupId = GroupId("group_id")
    }
  }

  private class ActionSetQueueBuilder extends OfpStructureBuilder[ActionSetQueue]{
    protected def applyInput(input: ActionSetQueue): Unit = {
      setMember("queue_id", input.queueId)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionSetQueue = new ActionSetQueue{
      override val queueId: UInt = "queue_id"
    }
  }

  private class ActionSetMplsTtlBuilder extends OfpStructureBuilder[ActionSetMplsTtl]{
    protected def applyInput(input: ActionSetMplsTtl): Unit = {
      setMember("mpls_ttl", input.mplsTtl)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionSetMplsTtl = new ActionSetMplsTtl{
      override val mplsTtl: UByte = "mpls_ttl"
    }
  }

  private class ActionDecMplsTtlBuilder extends OfpStructureBuilder[ActionDecMplsTtl]{
    protected def applyInput(input: ActionDecMplsTtl): Unit = { }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionDecMplsTtl = new ActionDecMplsTtl { }
  }

  private class ActionSetNwTtlBuilder extends OfpStructureBuilder[ActionSetNwTtl]{
    protected def applyInput(input: ActionSetNwTtl): Unit = {
      setMember("nw_ttl", input.nwTtl)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionSetNwTtl = new ActionSetNwTtl {
      override val nwTtl: UByte = "nw_ttl"
    }
  }

  private class ActionDecNwTtlBuilder extends OfpStructureBuilder[ActionDecNwTtl]{
    protected def applyInput(input: ActionDecNwTtl): Unit = { }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionDecNwTtl = new ActionDecNwTtl { }
  }

  private class ActionCopyTtlOutBuilder extends OfpStructureBuilder[ActionCopyTtlOut]{
    protected def applyInput(input: ActionCopyTtlOut): Unit = { }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionCopyTtlOut = new ActionCopyTtlOut { }
  }

  private class ActionCopyTtlInBuilder extends OfpStructureBuilder[ActionCopyTtlIn]{
    protected def applyInput(input: ActionCopyTtlIn): Unit = { }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionCopyTtlIn = new ActionCopyTtlIn { }
  }

  private class ActionPushVlanBuilder extends OfpStructureBuilder[ActionPushVlan]{
    protected def applyInput(input: ActionPushVlan): Unit = {
      setMember("ethertype", input.ethertype)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionPushVlan = new ActionPushVlan{
      override val ethertype: UShort = "ethertype"
    }
  }

  private class ActionPushMplsBuilder extends OfpStructureBuilder[ActionPushMpls]{
    protected def applyInput(input: ActionPushMpls): Unit = {
      setMember("ethertype", input.ethertype)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionPushMpls = new ActionPushMpls{
      override val ethertype: UShort = "ethertype"
    }
  }

  private class ActionPopVlanBuilder extends OfpStructureBuilder[ActionPopVlan]{
    protected def applyInput(input: ActionPopVlan): Unit = { }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionPopVlan = new ActionPopVlan { }
  }

  private class ActionPopMplsBuilder extends OfpStructureBuilder[ActionPopMpls]{
    protected def applyInput(input: ActionPopMpls): Unit = {
      setMember("ethertype", input.ethertype)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionPopMpls = new ActionPopMpls{
      override val ethertype: UShort = "ethertype"
    }
  }

  private class ActionPushPbbBuilder extends OfpStructureBuilder[ActionPushPbb]{
    protected def applyInput(input: ActionPushPbb): Unit = {
      setMember("ethertype", input.ethertype)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionPushPbb = new ActionPushPbb{
      override val ethertype: UShort = "ethertype"
    }
  }
  private class ActionPopPbbBuilder extends OfpStructureBuilder[ActionPopPbb]{
    protected def applyInput(input: ActionPopPbb): Unit = { }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionPopPbb = new ActionPopPbb { }
  }

  private class ActionSetFieldBuilder extends OfpStructureBuilder[ActionSetField]{
    protected def applyInput(input: ActionSetField): Unit = {
      setMember("field", input.field)(OxmTlv)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionSetField = new ActionSetField{
      override val field: OxmTlv[_] = structure[OxmTlv[_]]("field")
    }
  }

  private class ActionExperimenterBuilder extends OfpStructureBuilder[ActionExperimenter]{
    protected def applyInput(input: ActionExperimenter): Unit = {
      setMember("experimenter", input.experimenter)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionExperimenter = new ActionExperimenter{
      override val experimenter: UInt = "experimenter"
    }
  }

  protected abstract override def builderClasses = classOf[ActionOutputBuilder]       ::
                                                   classOf[ActionGroupBuilder]        ::
                                                   classOf[ActionSetQueueBuilder]     ::
                                                   classOf[ActionSetMplsTtlBuilder]   ::
                                                   classOf[ActionDecMplsTtlBuilder]   ::
                                                   classOf[ActionSetNwTtlBuilder]     ::
                                                   classOf[ActionDecNwTtlBuilder]     ::
                                                   classOf[ActionCopyTtlOutBuilder]   ::
                                                   classOf[ActionCopyTtlInBuilder]    ::
                                                   classOf[ActionPushVlanBuilder]     ::
                                                   classOf[ActionPushMplsBuilder]     ::
                                                   classOf[ActionPopVlanBuilder]      ::
                                                   classOf[ActionPopMplsBuilder]      ::
                                                   classOf[ActionPopPbbBuilder]       ::
                                                   classOf[ActionPushPbbBuilder]      ::
                                                   classOf[ActionSetFieldBuilder]     ::
                                                   classOf[ActionExperimenterBuilder] ::
                                                   super.builderClasses
}

