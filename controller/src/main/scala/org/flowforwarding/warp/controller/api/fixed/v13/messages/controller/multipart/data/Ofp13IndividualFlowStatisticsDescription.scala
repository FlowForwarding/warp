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
//import org.flowforwarding.warp.controller.api.fixed.v13.messages.{Ofp13MessageInput, Ofp13Message, Ofp13MessageDescription}
//import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod.FlowModFlags
//import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView
//
//trait IndividualFlowStatisticsReply extends Ofp13MultipartMultiValueMessage[IndividualFlowStatistics]
//
//trait IndividualFlowStatistics{
//  val tableId: UShort      /* ID of table flow came from. */
//  val durationSeconds: ULong  /* Time flow has been alive in seconds. */
//  val durationNanoseconds: ULong /* Time flow has been alive in nanoseconds beyond duration_sec. */
//  val priority: UInt       /* Priority of the entry. */
//  val idleTimeout: UInt    /* Number of seconds idle before expiration. */
//  val hardTimeout: UInt    /* Number of seconds before expiration. */
//  val flags: FlowModFlags /* Bitmap of OFPFF_* flags. */
//  val cookie: ULong        /* Opaque controller-issued identifier. */
//  val packetCount: ULong   /* Number of packets in flow. */
//  val byteCount: ULong     /* Number of bytes in flow. */
//  val fMatch: Match       /* Description of fields. Variable size. */
//}
//
//case class IndividualFlowStatisticsRequestInput(reqMore: Boolean, body: FlowStatisticsRequestBody)
//  extends MultipartMessageWithBodyRequestInput[FlowStatisticsRequestBody]
//
//trait IndividualFlowStatisticsReplyHandler{
//  def onIndividualFlowStatisticsReply(dpid: ULong, msg: IndividualFlowStatisticsReply): Array[BuilderInput] = Array.empty[BuilderInput]
//}
//
//private[fixed] trait Ofp13IndividualFlowStatisticsDescription extends Ofp13FlowStatisticsDescription {
//  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with IndividualFlowStatisticsReplyHandler] with Ofp13HeaderDescription with Ofp13MatchDescription =>
//
//  implicit object IndividualFlowStatistics extends FromDynamic[IndividualFlowStatistics]{
//    val fromDynamic: PartialFunction[DynamicStructure, IndividualFlowStatistics] = {
//      case s if s.ofType[IndividualFlowStatistics] => new OfpStructure[IndividualFlowStatistics](s) with IndividualFlowStatistics{
//        val tableId             = primitiveField[UShort]("table_id")
//        val durationSeconds     = primitiveField[ULong]("duration_sec")
//        val durationNanoseconds = primitiveField[ULong]("duration_nsec")
//        val priority            = primitiveField[UInt]("priority")
//        val idleTimeout         = primitiveField[UInt]("idle_timeout")
//        val hardTimeout         = primitiveField[UInt]("hard_timeout")
//        val flags               = bitmapField("flags")
//        val cookie: ULong       = primitiveField[ULong]("cookie")
//        val packetCount         = primitiveField[ULong]("packet_count")
//        val byteCount           = primitiveField[ULong]("byte_count")
//        val fMatch              = structureField[Match]("match")
//      }
//    }
//  }
//
//  class IndividualFlowStatisticsRequestBuilder
//    extends Ofp13MultipartMessageWithBodyRequestBuilder[FlowStatisticsRequestBody, IndividualFlowStatisticsRequestInput]{
//    override private[fixed] def inputFromTextView(implicit input: BITextView): IndividualFlowStatisticsRequestInput =
//      IndividualFlowStatisticsRequestInput("flags", structure[FlowStatisticsRequestBody]("body"))
//  }
//
//  case class IndividualFlowStatisticsReplyStructure(s: DynamicStructure)
//    extends OfpMultipartMultiValueMessage[IndividualFlowStatisticsReply, IndividualFlowStatistics](s) with IndividualFlowStatisticsReply
//
//  abstract override def builderClasses = classOf[IndividualFlowStatisticsRequestBuilder] :: super.builderClasses
//  abstract override def messageClasses = classOf[IndividualFlowStatisticsReplyStructure] :: super.messageClasses
//}