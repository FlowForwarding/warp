package org.flowforwarding.warp.controller.api.fixed.v13

import scala.util.Try
import scala.reflect.ClassTag

import org.flowforwarding.warp.controller.api.dynamic.{DynamicDriver, DynamicStructureBuilder, DynamicStructure}
import org.flowforwarding.warp.controller.api.fixed._
import scala.util.Success
import scala.util.Failure

trait Ofp13DriverApi[BuilderType <: DynamicStructureBuilder[BuilderType, StructureType], StructureType <: DynamicStructure[StructureType]]
  extends DriverApiHelper            [BuilderType, StructureType] with
          Ofp13HelloDescription      [BuilderType, StructureType] with
          Ofp13FeaturesDescription   [BuilderType, StructureType] with
          Ofp13EchoReplyDescription  [BuilderType, StructureType] with
          Ofp13EchoRequestDescription[BuilderType, StructureType]
{ driver: DynamicDriver[BuilderType, StructureType] => }

abstract class Ofp13MessageHandler[BuilderType <: DynamicStructureBuilder[BuilderType, StructureType],
                                   StructureType <: DynamicStructure[StructureType]](structureClass: Class[StructureType])
  extends SpecificVersionMessageHandler[Ofp13DriverApi[BuilderType, StructureType], StructureType]()(
    ClassTag(classOf[Ofp13DriverApi[BuilderType, StructureType]]),
    ClassTag(structureClass)){

  val versionCode: Short = 4

  type Api = Ofp13DriverApi[BuilderType, StructureType]

  private implicit val structureTag: ClassTag[StructureType] = ClassTag(structureClass)

  protected[api] def onCommonMessage(apiProvider: Api, dpid: Long, msg: StructureType): Try[Array[StructureType]] = Try {
    apiProvider.getConcreteStructure(msg) match {
      case Success(m: Hello) => onHello(dpid, m)
      case Success(m: FeaturesReply) => onFeaturesReply(dpid, m)
      case Success(m: EchoRequest) => onEchoRequest(dpid, m)
      case Success(m: EchoReply) => onEchoReply(dpid, m)
      case Success(_) => ??? // etc...
      case Failure(t) => throw t
    }
  } map { _.map(apiProvider.buildInput) }

  protected def onHello(dpid: Long, msg: Hello): Array[BuilderInput] = Array.empty[BuilderInput]
  protected def onFeaturesReply(dpid: Long, msg: FeaturesReply): Array[BuilderInput] = Array.empty[BuilderInput]
  protected def onEchoRequest(dpid: Long, msg: EchoRequest): Array[BuilderInput] = Array.empty[BuilderInput]
  protected def onEchoReply(dpid: Long, msg: EchoReply): Array[BuilderInput] = Array.empty[BuilderInput]
  // etc
}