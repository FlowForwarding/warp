/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.data

import spire.math._

import org.flowforwarding.warp.controller.api.dynamic._
import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.util._
import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart.{MultipartReplyBody, SingletonMultipartRequestBodyInput}
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView

case class GroupStatisticsRequest(groupId: GroupId) extends BuilderInput

case class GroupStatisticsRequestBodyInput(structure: GroupStatisticsRequest) extends SingletonMultipartRequestBodyInput[GroupStatisticsRequest]

trait GroupStatistics{
  /** Group identifier. */
  val groupId: GroupId
  /** Number of flows or groups that directly forward to this group. */
  val refCount: UInt
  /** Number of packets processed by group. */
  val packetCount: ULong
  /** Number of bytes processed by group. */
  val byteCount: ULong
  /** Time group has been alive in seconds. */
  val durationSeconds: ULong
  /** Time group has been alive in nanoseconds beyond duration_sec. */
  val durationNanoseconds: ULong
  /** One counter set per bucket. */
  val bucketStats: Array[BucketCounter]
}

trait BucketCounter {
  val packetCount: ULong
  val byteCount: ULong
}

trait GroupStatisticsReplyBody extends MultipartReplyBody[Array[GroupStatistics]]


trait GroupStatisticsReplyHandler{
  def onGroupStatisticsReply(dpid: ULong, msg: Array[GroupStatistics]): Array[BuilderInput] = Array.empty[BuilderInput]
}

private[fixed] trait Ofp13GroupStatisticsDescription extends StructureDescription {
  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with GroupStatisticsReplyHandler] =>

  implicit object BucketCounter extends FromDynamic[BucketCounter]{
    def fromDynamic: PartialFunction[DynamicStructure, BucketCounter] = {
      case s if s.ofType[BucketCounter] => new OfpStructure[BucketCounter](s) with BucketCounter {
        val byteCount: ULong = primitiveField[ULong]("byte_count")
        val packetCount: ULong = primitiveField[ULong]("packet_count")
      }
    }
  }

  private class GroupStatisticsRequestBuilder extends OfpStructureBuilder[GroupStatisticsRequest]{
    // Fills the underlying builder with the specified input.
    protected def applyInput(input: GroupStatisticsRequest): Unit = {
      setMember("group_id", input.groupId.id)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView) = new GroupStatisticsRequest(GroupId("group_id"))
  }

  implicit object GroupStatistics extends FromDynamic[GroupStatistics]{
    val fromDynamic: PartialFunction[DynamicStructure, GroupStatistics] = {
      case s if s.ofType[GroupStatistics] => new OfpStructure[GroupStatistics](s) with GroupStatistics{
        val groupId             = GroupId(primitiveField[UInt]("group_id"))
        val refCount            = primitiveField[UInt]("ref_count")
        val packetCount         = primitiveField[ULong]("packet_count")
        val byteCount           = primitiveField[ULong]("byte_count")
        val durationSeconds     = primitiveField[ULong]("duration_sec")
        val durationNanoseconds = primitiveField[ULong]("duration_nsec")
        val bucketStats         = structuresSequence[BucketCounter]("bucket_stats")
      }
    }
  }

  protected[fixed] implicit object GroupStatisticsRequest extends ToDynamic[GroupStatisticsRequest]{
    val toDynamic: PartialFunction[GroupStatisticsRequest, DynamicBuilderInput] = {
      case s => new GroupStatisticsRequestBuilder toDynamicInput s
    }
  }

  protected class GroupStatisticsRequestBodyInputBuilder extends OfpStructureBuilder[GroupStatisticsRequestBodyInput] {
    protected def applyInput(input: GroupStatisticsRequestBodyInput): Unit = {
      setMember("structure", input.structure)
    }

    override private[fixed] def inputFromTextView(implicit input: BITextView): GroupStatisticsRequestBodyInput =
      GroupStatisticsRequestBodyInput(structure[GroupStatisticsRequest]("structure"))
  }

  abstract override def builderClasses = classOf[GroupStatisticsRequestBodyInputBuilder] :: super.builderClasses
}