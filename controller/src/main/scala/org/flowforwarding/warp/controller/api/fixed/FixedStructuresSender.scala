/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed

import scala.concurrent.Future

import spire.math.{ULong, UByte}

import org.flowforwarding.warp.controller.SwitchConnector.SendingResult
import org.flowforwarding.warp.controller.api.dynamic.DynamicStructure
import org.flowforwarding.warp.controller.api.fixed.text_view.BITextView
import org.flowforwarding.warp.controller.driver_interface.{MessagesSender, MessageDriver}

trait FixedStructuresSender extends MessagesSender[DynamicStructure]{

  private implicit def textViewToDynamic[T](implicit toMessage: T => BITextView) = (d: MessageDriver[_]) => (i: T) => {
    val dh =  d.asInstanceOf[MessagesDescriptionHelper[_]]
    dh.parseTextView(i).flatMap(dh.buildDynamic).get
  }

  private implicit def inputToDynamic[T](implicit toMessage: T => BuilderInput) = (d: MessageDriver[_]) => (i: T) => {
    val dh =  d.asInstanceOf[MessagesDescriptionHelper[_]]
    dh.buildDynamic(i).get
  }

  private implicit def fromDs[R](implicit fromMessage: FixedOfpMessage => R) =
    (d: MessageDriver[_]) => (ds: DynamicStructure) => {
      val dh =  d.asInstanceOf[MessagesDescriptionHelper[_]]
      fromMessage(dh.toConcreteMessage(ds).get)
    }

  def sendBuilderInput[T, R](version: UByte, dpid: ULong, value: T, needReply: Boolean)
                            (implicit toMessage: T => BuilderInput, fromMessage: FixedOfpMessage => R): Future[SendingResult[R]] =
    sendToSwitch(version, dpid, value, needReply)

  def sendBuilderInput(version: UByte, dpid: ULong, value: BuilderInput, needReply: Boolean): Future[SendingResult[FixedOfpMessage]] =
    sendToSwitch(version, dpid, value, needReply)(inputToDynamic(x => x), fromDs(x => x))

  def sendTextView[T, R](version: UByte, dpid: ULong, value: T, needReply: Boolean)
                        (implicit toMessage: T => BITextView, fromMessage: FixedOfpMessage => R): Future[SendingResult[R]] =
    sendToSwitch(version, dpid, value, needReply)

  def sendTextView(version: UByte, dpid: ULong, value: BITextView, needReply: Boolean): Future[SendingResult[FixedOfpMessage]] =
    sendToSwitch(version, dpid, value, needReply)(textViewToDynamic(x => x), fromDs(x => x))
}
