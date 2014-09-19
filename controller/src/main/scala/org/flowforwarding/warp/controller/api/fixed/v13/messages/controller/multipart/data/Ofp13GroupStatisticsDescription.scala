///*
// * © 2013 FlowForwarding.Org
// * All Rights Reserved.  Use is subject to license terms.
// *
// * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
// */
//package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart
//
//import spire.math._
//
//import org.flowforwarding.warp.controller.api.dynamic._
//import org.flowforwarding.warp.controller.api.fixed._
//import org.flowforwarding.warp.controller.api.fixed.util._
//import org.flowforwarding.warp.controller.api.fixed.v13.structures._
//import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView
//
//trait GroupStatisticsReply extends Ofp13MultipartMultiValueMessage[GroupStatistics]
//
//trait GroupStatistics{
//  val groupId: GroupId                  /* Group identifier. */
//  val refCount: UInt                     /* Number of flows or groups that directly forward to this group. */
//  val packetCount: ULong                 /* Number of packets processed by group. */
//  val byteCount: ULong                   /* Number of bytes processed by group. */
//  val durationSeconds: ULong             /* Time group has been alive in seconds. */
//  val durationNanoseconds: ULong         /* Time group has been alive in nanoseconds beyond duration_sec. */
//  val bucketStats: Array[BucketCounter] /* One counter set per bucket. */
//}
//
//trait BucketCounter {
//  val packetCount: ULong
//  val byteCount: ULong
//}
//
//case class GroupStatisticsRequestBody(groupId: GroupId) extends BuilderInput
//
//case class GroupStatisticsRequestInput(reqMore: Boolean, body: GroupStatisticsRequestBody)
//  extends MultipartMessageWithBodyRequestInput[GroupStatisticsRequestBody]
//
//trait GroupStatisticsReplyHandler{
//  def onGroupStatisticsReply(dpid: ULong, msg: GroupStatisticsReply): Array[BuilderInput] = Array.empty[BuilderInput]
//}
//
//private[fixed] trait Ofp13GroupStatisticsDescription extends Ofp13MultipartMessageDescription {
//  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with GroupStatisticsReplyHandler] with Ofp13HeaderDescription =>
//
//  implicit object GroupStatisticsRequestBody extends ToDynamic[GroupStatisticsRequestBody]{
//    def toDynamic: PartialFunction[GroupStatisticsRequestBody, DynamicBuilderInput] = {
//      case b => new GroupStatisticsRequestBodyBuilder toDynamicInput b
//    }
//  }
//
//  implicit object BucketCounter extends FromDynamic[BucketCounter]{
//    def fromDynamic: PartialFunction[DynamicStructure, BucketCounter] = {
//      case s if s.ofType[BucketCounter] => new OfpStructure[BucketCounter](s) with BucketCounter {
//        val byteCount: ULong = primitiveField[ULong]("byte_count")
//        val packetCount: ULong = primitiveField[ULong]("packet_count")
//      }
//    }
//  }
//
//  private class GroupStatisticsRequestBodyBuilder extends OfpStructureBuilder[GroupStatisticsRequestBody]{
//    // Fills the underlying builder with the specified input.
//    protected def applyInput(input: GroupStatisticsRequestBody): Unit = {
//      setMember("group_id", input.groupId.id)
//    }
//
//    override private[fixed] def inputFromTextView(implicit input: BITextView) = new GroupStatisticsRequestBody(GroupId("group_id"))
//  }
//
//  implicit object GroupStatistics extends FromDynamic[GroupStatistics]{
//    val fromDynamic: PartialFunction[DynamicStructure, GroupStatistics] = {
//      case s if s.ofType[GroupStatistics] => new OfpStructure[GroupStatistics](s) with GroupStatistics{
//        val groupId             = GroupId(primitiveField[UInt]("group_id"))
//        val refCount            = primitiveField[UInt]("ref_count")
//        val packetCount         = primitiveField[ULong]("packet_count")
//        val byteCount           = primitiveField[ULong]("byte_count")
//        val durationSeconds     = primitiveField[ULong]("duration_sec")
//        val durationNanoseconds = primitiveField[ULong]("duration_nsec")
//        val bucketStats         = structuresSequence[BucketCounter]("bucket_stats")
//      }
//    }
//  }
//
//  class GroupStatisticsRequestBuilder
//    extends Ofp13MultipartMessageWithBodyRequestBuilder[GroupStatisticsRequestBody, GroupStatisticsRequestInput]{
//    override private[fixed] def inputFromTextView(implicit input: BITextView): GroupStatisticsRequestInput =
//      GroupStatisticsRequestInput("flags", structure[GroupStatisticsRequestBody]("body"))
//  }
//
//  case class GroupStatisticsReplyStructure(s: DynamicStructure)
//    extends OfpMultipartMultiValueMessage[GroupStatisticsReply, GroupStatistics](s) with GroupStatisticsReply
//
//  abstract override def builderClasses = classOf[GroupStatisticsRequestBuilder] :: classOf[GroupStatisticsRequestBodyBuilder] :: super.builderClasses
//  abstract override def messageClasses = classOf[GroupStatisticsReplyStructure] :: super.messageClasses
//}