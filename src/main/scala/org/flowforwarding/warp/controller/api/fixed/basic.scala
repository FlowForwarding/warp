package org.flowforwarding.warp.controller.api.fixed

import scala.util.Try
import scala.reflect.ClassTag

import com.typesafe.config.Config

import org.flowforwarding.warp.controller.api.dynamic.{DynamicDriver, DynamicStructure, DynamicStructureBuilder}


trait BuilderInput

object StructureName {
  def unapply(c: Class[_]): Option[String] = Some(c.getSimpleName.stripSuffix("Builder").stripSuffix("Structure"))
}

object Utils{
  implicit class ConfigExt(namesConfig: Config){
    def getTypeName[T: ClassTag]: String = {
      val StructureName(name) = implicitly[ClassTag[T]].runtimeClass
      namesConfig.getConfig(name).getString("type_name")
    }
  }

  implicit class LongExt(l: Long){
    def testBit(bit: Byte): Boolean = (l & (1 << bit)) != 0
  }
}

import Utils._

trait ConfigurableStructure{
  protected val namesConfig: Config

  def getFieldName[T: ClassTag](fieldKey: String): String = {
    val StructureName(name) = implicitly[ClassTag[T]].runtimeClass
    namesConfig.getConfig(name).getString(fieldKey)
  }
}

trait ConcreteStructure extends ConfigurableStructure{
  val namesConfig: Config
  val underlyingStructure: DynamicStructure[_ <: DynamicStructure[_]]

  def getPrimitive(fieldKey: String) = underlyingStructure.primitiveField(getFieldName(fieldKey)(ClassTag(this.getClass)))
}

abstract class ConcreteStructureBuilder[Input <: BuilderInput,
                                        BuilderType <: DynamicStructureBuilder[BuilderType, StructureType],
                                        StructureType <: DynamicStructure[StructureType]] extends ConfigurableStructure{
  protected val dynamicBuilder: BuilderType
  // Fills the underlying builder with the specified input.
  protected def applyInput(input: Input): Unit
  def build(input: Input): StructureType = {
    applyInput(input)
    dynamicBuilder.build
  }
}

abstract class SpecificVersionMessageHandler[StaticApiProvider: ClassTag, StructureType <: DynamicStructure[StructureType]: ClassTag]{

  def handle(driver: DynamicDriver[_, StructureType], dpid: Long, msg: StructureType) =
    driverAsApiProvider(driver) map { provider => onCommonMessage(provider, dpid, msg) }

  def versionCode: Short

  protected[api] def driverAsApiProvider(driver: DynamicDriver[_, StructureType]): Option[StaticApiProvider] = {
    val apiInterface = implicitly[ClassTag[StaticApiProvider]].runtimeClass
    if(driver.getClass.getInterfaces.contains(apiInterface))
      Some(driver.asInstanceOf[StaticApiProvider])
    else
      None
  }

  protected def onCommonMessage(apiProvider: StaticApiProvider, dpid: Long, msg: StructureType): Try[Array[StructureType]]
}

trait DriverApiHelper[BuilderType <: DynamicStructureBuilder[BuilderType, StructureType],
                      StructureType <: DynamicStructure[StructureType]]{
  driver: DynamicDriver[BuilderType, StructureType] =>

  protected[api] val namesConfig: Config

  private[api] val builderClasses: List[Class[_ <: ConcreteStructureBuilder[_, BuilderType, StructureType]]] = List()
  private[api] val structureClasses: List[Class[_ <: ConcreteStructure]] = List()

  private[api] def getConcreteStructure(dynamic: StructureType): Try[ConcreteStructure] = Try {
    structureClasses collectFirst {
      case c if dynamic.isTypeOf(namesConfig.getTypeName(ClassTag(c))) =>
        // Every XXXStructure type is inner class, so its constructor must get reference to outer class
        c.getConstructors.head.newInstance(this, dynamic).asInstanceOf[ConcreteStructure]
    } match {
      case Some(structure) => structure
      case None => throw new RuntimeException("Undefined type of structure.")
    }
  }

  private[api] def getDynamicBuilder(c: Class[_]): Try[BuilderType] = Try { getBuilder(namesConfig.getTypeName(ClassTag(c))) }

  def firstGenericParameter(c: Class[_]): Option[Class[_]] = Try {
    c.getGenericSuperclass
     .asInstanceOf[java.lang.reflect.ParameterizedType]
     .getActualTypeArguments
     .head
     .asInstanceOf[Class[_]]
  }.toOption

  def buildInput[X <: BuilderInput](i: X): StructureType = {
    builderClasses.collectFirst { case b if firstGenericParameter(b) == Some(i.getClass) => b }
                  .get
                  .getConstructors
                  .head
                  .newInstance(this)
                  .asInstanceOf[ConcreteStructureBuilder[X, BuilderType, StructureType]]  // could be cached
                  .build(i)
  }
}

