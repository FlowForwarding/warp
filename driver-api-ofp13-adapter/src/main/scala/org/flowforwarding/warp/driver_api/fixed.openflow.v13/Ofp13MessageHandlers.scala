package org.flowforwarding.warp.driver_api.fixed.openflow.v13

import org.flowforwarding.warp.controller.bus.ControllerBus
import org.flowforwarding.warp.controller.message_handlers.fixed.SpecificVersionMessageHandlers

abstract class Ofp13MessageHandlers(controllerBus: ControllerBus)
  extends SpecificVersionMessageHandlers[Ofp13MessageHandlersSet, Ofp13DriverApi](controllerBus)
  with Ofp13MessageHandlersSet