package org.flowforwarding.warp.protocol.dynamic

import java.io.File

import scala.util.{Failure, Try, Success}

import org.flowforwarding.warp.protocol.{StaticDriver, OfpMsg}
import org.flowforwarding.warp.controller.api.dynamic.{DynamicDriver, DynamicMessageDriverFactory, DynamicStructure}
import org.flowforwarding.warp.protocol.ofp13.Ofp13Impl
import spire.math.UByte

class ReflectiveMessageDriverFactory(definitionPaths: Array[String]) extends DynamicMessageDriverFactory[ReflectiveStructure]{
  def this(definitionPaths: String*) = this(definitionPaths.toArray)

  def get(versionCode: UByte): ReflectiveMessageDriver =
    (definedDrivers collectFirst { case Success((d, classes)) if d.versionCode == versionCode => ReflectiveMessageDriver(d, classes) }).get


  def supportedVersions: Array[UByte] = definedDrivers collect { case Success((d, _)) => d.versionCode }

  private val definedDrivers = definitionPaths map loadDriver

  private def recursiveListFiles(f: File): Seq[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
  }

  type DriverInfo = (DriverWithReflectionSupport[_ <: OfpMsg[_, _]], Iterable[Class[_]])

  def loadDriver(path: String): Try[DriverInfo] = {
    val initial = new File(path)
    val sourceFiles = if(initial.isDirectory) recursiveListFiles(initial).filterNot(_.isDirectory) else Seq(initial)
    Compiler.compile(sourceFiles) match {
      case Success(classes) => driverInfo(classes)
      case Failure(t) => println(t); Failure(t)
    }
  }

  def driverInfo(classes: Iterable[Class[_]]): Try[DriverInfo] = Try { // TODO: more informative error messages (if it is necessary)
    val driverClass = classes find { _.getSuperclass == classOf[StaticDriver[_, _]] }
    val constructor = driverClass.get.getConstructors()(0)
    (constructor.newInstance().asInstanceOf[DriverWithReflectionSupport[_ <: OfpMsg[_, _]]], classes)
  }
}