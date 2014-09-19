/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.api.fixed.v13

import org.flowforwarding.warp.controller.api.fixed._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.symmetric._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.mod._
import org.flowforwarding.warp.controller.api.fixed.v13.messages.controller.multipart._


class Ofp13DriverApiTests extends MessageTestsSet[Ofp13DriverApi]
                             with Ofp13EchoTest
                             with Ofp13ExperimenterTest

                             with Ofp13FlowModTest
                             with Ofp13PortModTest
                             with Ofp13GroupModTest
                             with Ofp13MeterModTest

                             with Ofp13RoleTest
                             with Ofp13QueueGetConfigTest
                             with Ofp13FeaturesTest
                             with Ofp13ConfigTest
                             with Ofp13BarrierTest
                             with Ofp13AsyncTest
                             with Ofp13PacketOutTest

                             with Ofp13SwitchDescriptionTest
                             with Ofp13PortDescriptionTest

