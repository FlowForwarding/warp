/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart

// TODO: Implement
//
//import org.flowforwarding.warp.controller.api.dynamic._
//import org.flowforwarding.warp.controller.api.fixed._
//import org.flowforwarding.warp.controller.api.fixed.v13.structures._
//
//trait TableFeaturesReply extends Ofp13MultipartSingleValueMessage[TableFeatures]
//
//trait TableFeatures{
//
//}
//
//case class TableFeaturesRequestInput(xid: UInt, reqMore: Boolean) extends MultipartMessageEmptyBodyRequestInput
//
//trait TableFeaturesReplyHandler{
//  def onTableFeaturesReply(dpid: ULong, msg: TableFeaturesReply): Array[BuilderInput] = Array.empty[BuilderInput]
//}
//
//private[fixed] trait Ofp13DTableFeaturesDescription extends Ofp13MultipartMessageDescription {
//  apiProvider: MessagesDescriptionHelper[_ <: OfpEventHandlers with TableFeaturesReplyHandler] with Ofp13HeaderDescription =>
//
//  class TableFeaturesRequestBuilder extends Ofp13MultipartMessageEmptyBodyRequestBuilder[TableFeaturesRequestInput]
//
//  implicit object TableFeatures extends FromDynamic[TableFeatures]{
//    val fromDynamic: PartialFunction[DynamicStructure, TableFeatures] = {
//      case s if s.ofType[TableFeatures] => new OfpStructure[TableFeatures](s) with TableFeatures {
//      }
//    }
//  }
//
//  case class TableFeaturesReplyStructure(s: DynamicStructure)
//    extends OfpMultipartSingleValueMessage[TableFeaturesReply, TableFeatures](s) with TableFeaturesReply
//
//  abstract override def builderClasses = classOf[TableFeaturesRequestBuilder] :: super.builderClasses
//  abstract override def messageClasses = classOf[TableFeaturesReplyStructure] :: super.messageClasses
//}