package org.flowforwarding.warp.controller.api.dynamic

abstract class DynamicMessageHandler[DriverType <: DynamicDriver[_, StructureType], StructureType <: DynamicStructure[StructureType]] {
  def supportsVersion(versionCode: Short) = supportedVersions.contains(versionCode)
  def supportedVersions: Array[Short]
  def onDynamicMessage(driver: DriverType, dpid: Long, msg: StructureType): Array[StructureType]
}
