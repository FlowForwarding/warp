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
//trait QueueStatisticsReply extends Ofp13MultipartMultiValueMessage[QueueStatistics]
//
//trait QueueStatistics{
//  val portNumber: PortNumber
//  val queueId: UInt
//  val transmittedPackets: ULong    /* Number of transmitted packets. */
//  val transmittedBytes: ULong      /* Number of transmitted bytes. */
//  val transmittedErrors: ULong     /* Number of transmit errors. This is a super-set
//                                     of more specific transmit errors and should be
//                                     greater than or equal to the sum of all
//                                     tx_*_err values (none currently defined.) */
//  val durationSeconds: UInt        /* Time port has been alive in seconds. */
//  val durationNanoseconds: UInt    /* Time port has been alive in nanoseconds beyond duration_sec. */
//}
//
//case class QueueStatisticsRequestBody(portNumber: PortNumber, queueId: UInt) extends BuilderInput
//
//case class QueueStatisticsRequestInput(reqMore: Boolean, body: QueueStatisticsRequestBody)
//  extends MultipartMessageWithBodyRequestInput[QueueStatisticsRequestBody]
//
//trait QueueStatisticsReplyHandler{
//  def onQueueStatisticsReply(dpid: ULong, msg: QueueStatisticsReply): Array[BuilderInput] = Array.empty[BuilderInput]
//}
//
//private[fixed] trait Ofp13QueueStatisticsDescription extends Ofp13MultipartMessageDescription {
//  apiProvider: MessagesDescriptionHelper[_ <: SpecificVersionMessageHandlers[_, _] with QueueStatisticsReplyHandler] with Ofp13HeaderDescription =>
//
//  implicit object QueueStatisticsRequestBody extends ToDynamic[QueueStatisticsRequestBody]{
//    def toDynamic: PartialFunction[QueueStatisticsRequestBody, DynamicBuilderInput] = {
//      case b => new QueueStatisticsRequestBodyBuilder toDynamicInput b
//    }
//  }
//
//  implicit object QueueStatistics extends FromDynamic[QueueStatistics]{
//    val fromDynamic: PartialFunction[DynamicStructure, QueueStatistics] = {
//      case s if s.ofType[QueueStatistics] => new OfpStructure[QueueStatistics](s) with QueueStatistics{
//        val portNumber           = PortNumber(primitiveField[UInt]("port_no"))
//        val queueId              = primitiveField[UInt]("queue_id")
//        val transmittedPackets   = primitiveField[ULong]("tx_packets")
//        val transmittedBytes     = primitiveField[ULong]("tx_bytes")
//        val transmittedErrors    = primitiveField[ULong]("tx_errors")
//        val durationSeconds      = primitiveField[UInt]("duration_sec")
//        val durationNanoseconds  = primitiveField[UInt]("duration_nsec")
//      }
//    }
//  }
//
//  private class QueueStatisticsRequestBodyBuilder extends OfpStructureBuilder[QueueStatisticsRequestBody]{
//    // Fills the underlying builder with the specified input.
//    protected def applyInput(input: QueueStatisticsRequestBody): Unit = {
//      setMember("port_no", input.portNumber.number)
//      setMember("queue_id", input.queueId)
//    }
//
//    override private[fixed] def inputFromTextView(implicit input: BITextView): QueueStatisticsRequestBody = new QueueStatisticsRequestBody(PortNumber("port_no"), "queue_id")
//  }
//
//  class QueueStatisticsRequestBuilder
//    extends Ofp13MultipartMessageWithBodyRequestBuilder[QueueStatisticsRequestBody, QueueStatisticsRequestInput]{
//    override private[fixed] def inputFromTextView(implicit input: BITextView): QueueStatisticsRequestInput =
//      QueueStatisticsRequestInput("flags", structure[QueueStatisticsRequestBody]("body"))
//  }
//
//  case class QueueStatisticsReplyStructure(s: DynamicStructure)
//    extends OfpMultipartMultiValueMessage[QueueStatisticsReply, QueueStatistics](s) with QueueStatisticsReply
//
//  abstract override def builderClasses = classOf[QueueStatisticsRequestBuilder] :: classOf[QueueStatisticsRequestBodyBuilder] :: super.builderClasses
//  abstract override def messageClasses = classOf[QueueStatisticsReplyStructure] :: super.messageClasses
//}