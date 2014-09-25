/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.managers.sal

import spire.math.{UInt, UShort}

case class Flow(cookie: Option[Long],
                name: Option[String],
                installInHw: Boolean,
                priority: Option[UShort],
                idleTimeout: Option[UInt],
                hardTimeout: Option[UInt],
                matchFields: Set[MatchField],
                actions: List[Action])