/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.driver_api.fixed.openflow.v13

import spire.math.UByte

import org.flowforwarding.warp.driver_api.dynamic.{DynamicStructure, DynamicStructureBuilder}

import org.flowforwarding.warp.driver_api.fixed._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.async._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.symmetric._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.mod._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.messages.controller.multipart.data._

import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures._
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.instructions.Ofp13InstructionsDescription
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.oxm_tlv.Ofp13OxmTlvDescription
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.meter_bands.Ofp13MeterBandsDescription
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.actions.Ofp13ActionsDescription
import org.flowforwarding.warp.driver_api.fixed.openflow.v13.structures.table_features.{Ofp13TableFeaturePropertyDescription, Ofp13ActionIdDescription, Ofp13InstructionIdDescription}

trait Ofp13DriverApi extends MessagesDescriptionHelper[Ofp13MessageHandlersSet]
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
                     with Ofp13FlowRemDescription

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

                     with Ofp13TableFeaturePropertyDescription
                     with Ofp13InstructionIdDescription
                     with Ofp13ActionIdDescription

                     with Ofp13MultipartDescription
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
{ driver: DynamicStructureBuilder[_ <: DynamicStructure] => }

trait Ofp13MessageHandlersSet extends SpecificVersionMessageHandlersSet[Ofp13MessageHandlersSet, Ofp13DriverApi]
                                 with HelloHandler
                                 with FeaturesReplyHandler
                                 with EchoHandler
                                 with ExperimenterHandler
                                 with ErrorHandler
                                 with PacketInHandler
                                 with PortStatusHandler
                                 with FlowRemHandler
                                 with RoleReplyHandler
                                 with QueueGetConfigReplyHandler
                                 with GetConfigReplyHandler
                                 with BarrierHandler
                                 with GetAsyncReplyHandler
                                 // multipart handlers
                                 with MultipartReplyHandlers
                                 with AggregateFlowStatisticsReplyHandler
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
                                 with MultipartExperimenterReplyHandler { val versionCode = UByte(4) }