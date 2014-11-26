/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView
import org.flowforwarding.warp.controller.api.fixed.util._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.data._
import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.{Ofp13MessageInput, Ofp13Message, Ofp13MessageDescription}
import spire.math.{ULong, UShort}

import scala.reflect.ClassTag

abstract class MultipartRequestBodyInput[T: ClassTag] extends BuilderInput
abstract class EmptyMultipartRequestBodyInput extends MultipartRequestBodyInput[Nothing] { def structures = Array.empty }
abstract class SingletonMultipartRequestBodyInput[T <: BuilderInput: ClassTag] extends MultipartRequestBodyInput[T] { def structure: T  }
abstract class ArrayMultipartRequestBodyInput[T <: BuilderInput: ClassTag] extends MultipartRequestBodyInput[T] { def structures: Array[T] }

case class MultipartRequestInput(body: MultipartRequestBodyInput[_ <: BuilderInput], reqMore: Boolean = false) extends Ofp13MessageInput

trait MultipartReplyBody[T]{ def value: T }

trait MultipartReply extends Ofp13Message{
  def reqMore: Boolean
  def body: MultipartReplyBody[_]
}

trait MultipartReplyHandlers {
  handlers: AggregateFlowStatisticsReplyHandler
       with GroupFeaturesReplyHandler
       with GroupStatisticsReplyHandler
       with IndividualFlowStatisticsReplyHandler
       with MeterConfigReplyHandler
       with MeterFeaturesReplyHandler
       with MeterStatisticsReplyHandler
       with PortDescriptionReplyHandler
       with PortStatisticsReplyHandler
       with QueueStatisticsReplyHandler
       with SwitchDescriptionReplyHandler
       with TableFeaturesReplyHandler
       with TableStatisticsReplyHandler
       with MultipartExperimenterReplyHandler =>

  def onMultipartReply(dpid: ULong, msg: MultipartReply): Array[BuilderInput] =
    msg.body match {
      case body: AggregateFlowStatisticsReplyBody  => handlers.onAggregateFlowStatisticsReply(dpid, body.value)
      case body: GroupFeaturesReplyBody            => handlers.onGroupFeaturesReply(dpid, body.value)
      case body: GroupStatisticsReplyBody          => handlers.onGroupStatisticsReply(dpid, body.value)
      case body: IndividualFlowStatisticsReplyBody => handlers.onIndividualFlowStatisticsReply(dpid, body.value)
      case body: MeterConfigReplyBody              => handlers.onMeterConfigReply(dpid, body.value)
      case body: MeterFeaturesReplyBody            => handlers.onMeterFeaturesReply(dpid, body.value)
      case body: MeterStatisticsReplyBody          => handlers.onMeterStatisticsReply(dpid, body.value)
      case body: PortDescriptionReplyBody          => handlers.onPortDescriptionReply(dpid, body.value)
      case body: PortStatisticsReplyBody           => handlers.onPortStatisticsReply(dpid, body.value)
      case body: QueueStatisticsReplyBody          => handlers.onQueueStatisticsReply(dpid, body.value)
      case body: SwitchDescriptionReplyBody        => handlers.onSwitchDescriptionReply(dpid, body.value)
      case body: TableFeaturesReplyBody            => handlers.onTableFeaturesReply(dpid, body.value)
      case body: TableStatisticsReplyBody          => handlers.onTableStatisticsReply(dpid, body.value)
      case body: MultipartExperimenterReplyBody    => handlers.onMultipartExperimenterReply(dpid, body.value)
    }
}

private[fixed] trait Ofp13MultipartDescription extends Ofp13MessageDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with MultipartReplyHandlers]
          with Ofp13HeaderDescription
          with Ofp13AggregateFlowStatisticsDescription
          with Ofp13GroupFeaturesDescription
          with Ofp13GroupStatisticsDescription
          with Ofp13IndividualFlowStatisticsDescription
          with Ofp13MeterConfigDescription
          with Ofp13MeterFeaturesDescription
          with Ofp13MeterStatisticsDescription
          with Ofp13PortDescriptionDescription
          with Ofp13PortStatisticsDescription
          with Ofp13QueueStatisticsDescription
          with Ofp13SwitchDescriptionDescription
          with Ofp13TableFeaturesDescription
          with Ofp13TableStatisticsDescription
          with Ofp13MultipartExperimenterDescription

          with Ofp13PortDescription =>

  protected[fixed] implicit object MultipartRequestBodyInput extends ToDynamic[MultipartRequestBodyInput[_ <: BuilderInput]] {
    val toDynamic: PartialFunction[MultipartRequestBodyInput[_ <: BuilderInput], DynamicBuilderInput] = {
      case input: AggregateFlowStatisticsRequestBodyInput  => new AggregateFlowStatisticsRequestBodyInputBuilder  toDynamicInput input
      case input: GroupFeaturesRequestBodyInput            => new GroupFeaturesRequestBodyInputBuilder            toDynamicInput input
      case input: GroupStatisticsRequestBodyInput          => new GroupStatisticsRequestBodyInputBuilder          toDynamicInput input
      case input: IndividualFlowStatisticsRequestBodyInput => new IndividualFlowStatisticsRequestBodyInputBuilder toDynamicInput input
      case input: MeterConfigRequestBodyInput              => new MeterConfigRequestBodyInputBuilder              toDynamicInput input
      case input: MeterFeaturesRequestBodyInput            => new MeterFeaturesRequestBodyInputBuilder            toDynamicInput input
      case input: MeterStatisticsRequestBodyInput          => new MeterStatisticsRequestBodyInputBuilder          toDynamicInput input
      case input: PortDescriptionRequestBodyInput          => new PortDescriptionRequestBodyInputBuilder          toDynamicInput input
      case input: PortStatisticsRequestBodyInput           => new PortStatisticsRequestBodyInputBuilder           toDynamicInput input
      case input: QueueStatisticsRequestBodyInput          => new QueueStatisticsRequestBodyInputBuilder          toDynamicInput input
      case input: SwitchDescriptionRequestBodyInput        => new SwitchDescriptionRequestBodyInputBuilder        toDynamicInput input
      case input: TableFeaturesRequestBodyInput            => new TableFeaturesRequestBodyInputBuilder            toDynamicInput input
      case input: TableStatisticsRequestBodyInput          => new TableStatisticsRequestBodyInputBuilder          toDynamicInput input
      case input: MultipartExperimenterRequestBodyInput    => new MultipartExperimenterRequestBodyInputBuilder    toDynamicInput input
    }
  }

  protected[fixed] implicit object MultipartReplyBody extends FromDynamic[MultipartReplyBody[_]] {
    val fromDynamic: PartialFunction[DynamicStructure, MultipartReplyBody[_]] = {
      case s if s.ofType[AggregateFlowStatisticsReplyBody] =>
        new OfpMessage[AggregateFlowStatisticsReplyBody](s) with AggregateFlowStatisticsReplyBody {
          val value = structureField[AggregateFlowStatistics]("value")
        }
      case s if s.ofType[GroupFeaturesReplyBody] =>
        new OfpMessage[GroupFeaturesReplyBody](s) with GroupFeaturesReplyBody {
          val value = structureField[GroupFeatures]("value")
        }
      case s if s.ofType[GroupStatisticsReplyBody] =>
        new OfpMessage[GroupStatisticsReplyBody](s) with GroupStatisticsReplyBody {
          val value = structuresSequence[GroupStatistics]("value")
        }
      case s if s.ofType[IndividualFlowStatisticsReplyBody] =>
        new OfpMessage[IndividualFlowStatisticsReplyBody](s) with IndividualFlowStatisticsReplyBody {
          val value = structuresSequence[IndividualFlowStatistics]("value")
        }
      case s if s.ofType[MeterConfigReplyBody] =>
        new OfpMessage[MeterConfigReplyBody](s) with MeterConfigReplyBody {
          val value = structuresSequence[MeterConfig]("value")
        }
      case s if s.ofType[MeterFeaturesReplyBody] =>
        new OfpMessage[MeterFeaturesReplyBody](s) with MeterFeaturesReplyBody {
          val value = structureField[MeterFeatures]("value")
        }
      case s if s.ofType[MeterStatisticsReplyBody] =>
        new OfpMessage[MeterStatisticsReplyBody](s) with MeterStatisticsReplyBody {
          val value = structuresSequence[MeterStatistics]("value")
        }
      case s if s.ofType[PortDescriptionReplyBody] =>
        new OfpMessage[PortDescriptionReplyBody](s) with PortDescriptionReplyBody {
          val value = structuresSequence[Port]("value")
        }
      case s if s.ofType[PortStatisticsReplyBody]  =>
        new OfpMessage[PortStatisticsReplyBody](s) with PortStatisticsReplyBody {
          val value = structureField[PortStatistics]("value")
        }
      case s if s.ofType[QueueStatisticsReplyBody] =>
        new OfpMessage[QueueStatisticsReplyBody](s) with QueueStatisticsReplyBody {
          val value = structuresSequence[QueueStatistics]("value")
        }
      case s if s.ofType[SwitchDescriptionReplyBody] =>
        new OfpMessage[SwitchDescriptionReplyBody](s) with SwitchDescriptionReplyBody {
          val value = structureField[SwitchDescription]("value")
        }
      case s if s.ofType[TableFeaturesReplyBody] =>
        new OfpMessage[TableFeaturesReplyBody](s) with TableFeaturesReplyBody {
          val value = structuresSequence[TableFeatures]("value")
        }
      case s if s.ofType[TableStatisticsReplyBody] =>
        new OfpMessage[TableStatisticsReplyBody](s) with TableStatisticsReplyBody {
          val value = structuresSequence[TableStatistics]("value")
        }
      case s if s.ofType[MultipartExperimenterReplyBody] =>
        new OfpMessage[MultipartExperimenterReplyBody](s) with MultipartExperimenterReplyBody {
          val value = structureField[MultipartExperimenter]("value")
        }
    }
  }

  class MultipartRequestBuilder extends OfpMessageBuilder[MultipartRequestInput] {
    override protected def applyInput(input: MultipartRequestInput): Unit = {
      super.applyInput(input)
      setMember("flags", if (input.reqMore) ULong(1) else ULong(0))
      setMember("body", input.body)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): MultipartRequestInput =
      MultipartRequestInput(structure[MultipartRequestBodyInput[_ <: BuilderInput]]("body"), "flags")
  }

  class Ofp13MultipartReplyStructure(s: DynamicStructure) extends OfpMessage[MultipartReply](s) with MultipartReply {
    val reqMore: Boolean = primitiveField[ULong]("flags") == ULong(1)
    val body: MultipartReplyBody[_] = structureField[MultipartReplyBody[_]]("body")
  }

  protected abstract override def builderClasses = classOf[MultipartRequestBuilder] :: super.builderClasses
  protected abstract override def messageClasses = classOf[Ofp13MultipartReplyStructure] :: super.messageClasses
}