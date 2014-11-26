/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.structures.table_features

import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView
import org.flowforwarding.warp.controller.api.dynamic.{DynamicBuilderInput, DynamicStructure}

trait ActionId extends BuilderInput
case class ActionOutputId() extends ActionId 
case class ActionGroupId() extends ActionId 
case class ActionSetQueueId() extends ActionId 
case class ActionSetMplsTtlId() extends ActionId 
case class ActionDecMplsTtlId() extends ActionId
case class ActionSetNwTtlId() extends ActionId 
case class ActionDecNwTtlId() extends ActionId
case class ActionCopyTtlOutId() extends ActionId
case class ActionCopyTtlInId() extends ActionId
case class ActionPushVlanId() extends ActionId 
case class ActionPushMplsId() extends ActionId 
case class ActionPopVlanId() extends ActionId
case class ActionPopMplsId() extends ActionId 
case class ActionSetFieldId() extends ActionId 
case class ActionPushPbbId() extends ActionId 
case class ActionPopPbbId() extends ActionId
case class ActionExperimenterId() extends ActionId  // TODO: experimenter data?

private[fixed] trait Ofp13ActionIdDescription extends StructureDescription{
  apiProvider: StructuresDescriptionHelper =>

  protected implicit object ActionId extends ToDynamic[ActionId] with FromDynamic[ActionId]{
    val toDynamic: PartialFunction[ActionId, DynamicBuilderInput] = {
      case i: ActionOutputId       => new ActionOutputIdBuilder       toDynamicInput i
      case i: ActionGroupId        => new ActionGroupIdBuilder        toDynamicInput i
      case i: ActionSetQueueId     => new ActionSetQueueIdBuilder     toDynamicInput i
      case i: ActionSetMplsTtlId   => new ActionSetMplsTtlIdBuilder   toDynamicInput i
      case i: ActionDecMplsTtlId   => new ActionDecMplsTtlIdBuilder   toDynamicInput i
      case i: ActionSetNwTtlId     => new ActionSetNwTtlIdBuilder     toDynamicInput i
      case i: ActionDecNwTtlId     => new ActionDecNwTtlIdBuilder     toDynamicInput i
      case i: ActionCopyTtlOutId   => new ActionCopyTtlOutIdBuilder   toDynamicInput i
      case i: ActionCopyTtlInId    => new ActionCopyTtlInIdBuilder    toDynamicInput i
      case i: ActionPushVlanId     => new ActionPushVlanIdBuilder     toDynamicInput i
      case i: ActionPushMplsId     => new ActionPushMplsIdBuilder     toDynamicInput i
      case i: ActionPopVlanId      => new ActionPopVlanIdBuilder      toDynamicInput i
      case i: ActionPopMplsId      => new ActionPopMplsIdBuilder      toDynamicInput i
      case i: ActionSetFieldId     => new ActionSetFieldIdBuilder     toDynamicInput i
      case i: ActionPushPbbId      => new ActionPushPbbIdBuilder      toDynamicInput i
      case i: ActionPopPbbId       => new ActionPopPbbIdBuilder       toDynamicInput i
      case i: ActionExperimenterId => new ActionExperimenterIdBuilder toDynamicInput i
    }

    val fromDynamic: PartialFunction[DynamicStructure, ActionId] = {
      case s if s.ofType[ActionOutputId]       => ActionOutputId()
      case s if s.ofType[ActionGroupId]        => ActionGroupId()
      case s if s.ofType[ActionSetQueueId]     => ActionSetQueueId()
      case s if s.ofType[ActionSetMplsTtlId]   => ActionSetMplsTtlId()
      case s if s.ofType[ActionDecMplsTtlId]   => ActionDecMplsTtlId()
      case s if s.ofType[ActionSetNwTtlId]     => ActionSetNwTtlId()
      case s if s.ofType[ActionDecNwTtlId]     => ActionDecNwTtlId()
      case s if s.ofType[ActionCopyTtlOutId]   => ActionCopyTtlOutId()
      case s if s.ofType[ActionCopyTtlInId]    => ActionCopyTtlInId()
      case s if s.ofType[ActionPushVlanId]     => ActionPushVlanId()
      case s if s.ofType[ActionPushMplsId]     => ActionPushMplsId()
      case s if s.ofType[ActionPopVlanId]      => ActionPopVlanId()
      case s if s.ofType[ActionPopMplsId]      => ActionPopMplsId()
      case s if s.ofType[ActionSetFieldId]     => ActionSetFieldId()
      case s if s.ofType[ActionPushPbbId]      => ActionPushPbbId()
      case s if s.ofType[ActionPopPbbId]       => ActionPopPbbId()
      case s if s.ofType[ActionExperimenterId] => ActionExperimenterId()
    }
  }

  private trait ActionIdBuilder[Input <: ActionId] extends OfpStructureBuilder[Input] {
    protected def applyInput(input: Input): Unit = { }
  }

  private class ActionOutputIdBuilder extends ActionIdBuilder[ActionOutputId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionOutputId = ActionOutputId()
  }

  private class ActionGroupIdBuilder extends ActionIdBuilder[ActionGroupId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionGroupId = ActionGroupId()
  }

  private class ActionSetQueueIdBuilder extends ActionIdBuilder[ActionSetQueueId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionSetQueueId = ActionSetQueueId()
  }

  private class ActionSetMplsTtlIdBuilder extends ActionIdBuilder[ActionSetMplsTtlId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionSetMplsTtlId = ActionSetMplsTtlId()
  }

  private class ActionDecMplsTtlIdBuilder extends ActionIdBuilder[ActionDecMplsTtlId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionDecMplsTtlId = ActionDecMplsTtlId()
  }

  private class ActionSetNwTtlIdBuilder extends ActionIdBuilder[ActionSetNwTtlId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionSetNwTtlId = ActionSetNwTtlId()
  }

  private class ActionDecNwTtlIdBuilder extends ActionIdBuilder[ActionDecNwTtlId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionDecNwTtlId = ActionDecNwTtlId()
  }

  private class ActionCopyTtlOutIdBuilder extends ActionIdBuilder[ActionCopyTtlOutId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionCopyTtlOutId = ActionCopyTtlOutId()
  }

  private class ActionCopyTtlInIdBuilder extends ActionIdBuilder[ActionCopyTtlInId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionCopyTtlInId = ActionCopyTtlInId()
  }

  private class ActionPushVlanIdBuilder extends ActionIdBuilder[ActionPushVlanId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionPushVlanId = ActionPushVlanId()
  }

  private class ActionPushMplsIdBuilder extends ActionIdBuilder[ActionPushMplsId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionPushMplsId = ActionPushMplsId()
  }

  private class ActionPopVlanIdBuilder extends ActionIdBuilder[ActionPopVlanId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionPopVlanId = ActionPopVlanId()
  }

  private class ActionPopMplsIdBuilder extends ActionIdBuilder[ActionPopMplsId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionPopMplsId = ActionPopMplsId()
  }

  private class ActionSetFieldIdBuilder extends ActionIdBuilder[ActionSetFieldId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionSetFieldId = ActionSetFieldId()
  }

  private class ActionPushPbbIdBuilder extends ActionIdBuilder[ActionPushPbbId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionPushPbbId = ActionPushPbbId()
  }

  private class ActionPopPbbIdBuilder extends ActionIdBuilder[ActionPopPbbId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionPopPbbId = ActionPopPbbId()
  }

  private class ActionExperimenterIdBuilder extends ActionIdBuilder[ActionExperimenterId]{
    override private[fixed] def inputFromTextView(implicit input: BITextView): ActionExperimenterId = ActionExperimenterId()
  }
  protected abstract override def builderClasses = classOf[ActionOutputIdBuilder]       ::
                                                   classOf[ActionGroupIdBuilder]        ::
                                                   classOf[ActionSetQueueIdBuilder]     ::
                                                   classOf[ActionSetMplsTtlIdBuilder]   ::
                                                   classOf[ActionDecMplsTtlIdBuilder]   ::
                                                   classOf[ActionSetNwTtlIdBuilder]     ::
                                                   classOf[ActionDecNwTtlIdBuilder]     ::
                                                   classOf[ActionCopyTtlOutIdBuilder]   ::
                                                   classOf[ActionCopyTtlInIdBuilder]    ::
                                                   classOf[ActionPushVlanIdBuilder]     ::
                                                   classOf[ActionPushMplsIdBuilder]     ::
                                                   classOf[ActionPopVlanIdBuilder]      ::
                                                   classOf[ActionPopMplsIdBuilder]      ::
                                                   classOf[ActionSetFieldIdBuilder]     ::
                                                   classOf[ActionPushPbbIdBuilder]      ::
                                                   classOf[ActionPopPbbIdBuilder]       ::
                                                   classOf[ActionExperimenterIdBuilder] ::
                                                   super.builderClasses
}