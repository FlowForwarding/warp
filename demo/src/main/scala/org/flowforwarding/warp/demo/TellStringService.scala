/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.demo

import org.flowforwarding.warp.controller.modules.rest.RestApiService
import org.flowforwarding.warp.controller.bus.ControllerBus

class TellStringService(protected val bus: ControllerBus, serverPrefix: String, responseString: String) extends RestApiService(serverPrefix){
  override val servicePrefix = "string"
  override def route = path("tell") { get { complete(responseString) } }
}