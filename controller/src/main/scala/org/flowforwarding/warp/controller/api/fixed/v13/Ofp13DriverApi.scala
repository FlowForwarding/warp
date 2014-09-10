/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13

import spire.math.UByte

import org.flowforwarding.warp.controller.bus.ControllerBus
import org.flowforwarding.warp.controller.api.dynamic.{DynamicStructure, DynamicStructureBuilder}

import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.async._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.symmetric._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod._

import org.flowforwarding.warp.controller.api.fixed.v13.structures._
import org.flowforwarding.warp.controller.api.fixed.v13.structures.instructions.Ofp13InstructionsDescription
import org.flowforwarding.warp.controller.api.fixed.v13.structures.oxm_tlv.Ofp13OxmTlvDescription
import org.flowforwarding.warp.controller.api.fixed.v13.structures.meter_bands.Ofp13MeterBandsDescription
import org.flowforwarding.warp.controller.api.fixed.v13.structures.actions.Ofp13ActionsDescription

trait Ofp13DriverApi extends MessagesDescriptionHelper[Ofp13MessageHandlers]
                     with Ofp13HeaderDescription
                     with Ofp13MatchDescription
                     with Ofp13InstructionsDescription
                     with Ofp13OxmTlvDescription
                     with Ofp13ActionsDescription
                     with Ofp13MeterBandsDescription
                     with Ofp13BucketDescription
                     with Ofp13PortDescription

                     with Ofp13HelloDescription
                     with Ofp13EchoDescription
                     with Ofp13ExperimenterDescription

                     with Ofp13ErrorDescription
                     with Ofp13PacketInDescription
                     with Ofp13PortStatusDescription

                     with Ofp13FlowModDescription
                     with Ofp13GroupModDescription
                     with Ofp13PortModDescription
                     with Ofp13MeterModDescription

                     with Ofp13RoleDescription
                     with Ofp13PacketOutDescription
                     with Ofp13FeaturesDescription
                     with Ofp13QueueGetConfigDescription
                     with Ofp13ConfigDescription
                     with Ofp13BarrierDescription
                     with Ofp13AsyncDescription
{ driver: DynamicStructureBuilder[_ <: DynamicStructure] => }

abstract class Ofp13MessageHandlers(controllerBus: ControllerBus)
                                    extends SpecificVersionMessageHandlers[Ofp13MessageHandlers, Ofp13DriverApi](controllerBus, UByte(4))
                                       with HelloHandler
                                       with FeaturesReplyHandler
                                       with EchoHandler
                                       with ExperimenterHandler
                                       with ErrorHandler
                                       with PacketInHandler
                                       with PortStatusHandler
                                       with RoleReplyHandler
                                       with QueueGetConfigReplyHandler
                                       with GetConfigReplyHandler
                                       with BarrierHandler
                                       with GetAsyncReplyHandler
