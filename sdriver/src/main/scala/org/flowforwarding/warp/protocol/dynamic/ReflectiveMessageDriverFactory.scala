/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.dynamic

import java.io.File

import scala.util.{Failure, Try, Success}

import com.typesafe.scalalogging.StrictLogging
import spire.math.UByte

import org.flowforwarding.warp.protocol.{StaticDriver, OfpMsg}
import org.flowforwarding.warp.controller.api.dynamic.DynamicMessageDriverFactory

class ReflectiveMessageDriverFactory(definitionPaths: Array[String]) extends DynamicMessageDriverFactory[ReflectiveStructure] with StrictLogging{
  def this(definitionPaths: String*) = this(definitionPaths.toArray)

  def get(versionCode: UByte): ReflectiveMessageDriver =
    (definedDrivers collectFirst { case (d, classes) if d.versionCode == versionCode => ReflectiveMessageDriver(d, classes) }).get

  def supportedVersions: Array[UByte] = definedDrivers collect { case (d, _) => d.versionCode }

  private val definedDrivers = definitionPaths map { path =>
    val result = loadDriver(path)
    result match {
      case Failure(t: CompilationFailedException) =>
        val errorsData = t.errorsByFile.map { case (name, errors) =>
          errors.foldLeft(name + "\n") { case (s, (line, msg)) => s + s"$line. $msg\n" }
        }
        logger.error(errorsData.mkString(s"An error occurred while loading driver ($path)\n${t.getMessage}\n", "\n", ""))
      case Failure(t) =>
        logger.error(s"An error occurred while loading driver ($path)", t)
      case Success(d) =>
        logger.info(s"Driver defined at $path has been loaded successfully")
    }
    result
  } collect {
    case Success(driver) => driver
  }


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
      case Failure(t) => Failure(t)
    }
  }

  def driverInfo(classes: Iterable[Class[_]]): Try[DriverInfo] = Try { // TODO: more informative error messages (if it is necessary)
    val driverClass = classes find { _.getSuperclass == classOf[StaticDriver[_, _]] }
    val constructor = driverClass.get.getConstructors()(0)
    (constructor.newInstance().asInstanceOf[DriverWithReflectionSupport[_ <: OfpMsg[_, _]]], classes)
  }
}