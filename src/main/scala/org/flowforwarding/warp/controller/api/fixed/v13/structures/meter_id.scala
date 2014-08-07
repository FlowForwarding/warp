package org.flowforwarding.warp.controller.api.fixed.v13.structures

import spire.math.UInt

case class MeterId(id: UInt)
object MeterId{
  /** Meter for slow datapath. */
  val SlowpathMeter = MeterId(UInt(0xfffffffdL))

  /** Meter for controller connection. */
  val ControllerMeter = MeterId(UInt(0xfffffffeL))

  /** Represents all meters for stat requests commands. */
  val AllMeters = MeterId(UInt(0xffffffffL))

}