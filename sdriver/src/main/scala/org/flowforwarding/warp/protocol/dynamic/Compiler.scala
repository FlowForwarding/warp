/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.protocol.dynamic

import scala.util.{Success, Failure, Try}

import scala.tools.nsc.{Global, Settings}
import scala.tools.nsc.io._
import scala.tools.nsc.reporters.StoreReporter
import scala.reflect.internal.util.{BatchSourceFile, AbstractFileClassLoader}


/* This class is a wrapper over Scala Compiler API
   which has simple interface just accepting the source code string.

   Compiles the source code assuming that it is a .scala source file content.
   It used a classpath of the environment that called the `Compiler` class.
 */
object Compiler {
  def compile(sourceFiles: Seq[java.io.File]): Try[Iterable[Class[_]]] = {
    // prepare the code you want to compile
    val sources = sourceFiles map { f =>
      new BatchSourceFile(f.toString, scala.io.Source.fromFile(f).mkString)
    }

    val settings = new Settings

    /* Take classpath from currently running scala environment. */
    settings.usejavacp.value = true

    /* Save class files for compiled classes into a virtual directory in memory. */
    val directory = new VirtualDirectory("(memory)", None)
    settings.outputDirs.setSingleOutput(directory)

    val reporter = new StoreReporter()
    val compiler = new Global(settings, reporter)
    new compiler.Run() compileSources sources.toList

    if (reporter.hasErrors) {
      val messages = reporter.infos groupBy { _.pos.source.file.name } mapValues { infos => infos.map(i => (i.pos.line, i.msg)).toMap }
      Failure(new CompilationFailedException(messages))
    } else {
      // Each time new `AbstractFileClassLoader` is created for loading classes
      // it gives an opportunity to treat same name classes loading well.
      val classLoader =  new AbstractFileClassLoader(directory, this.getClass.getClassLoader)
      val classes = collectFiles(directory) collect {
        case file if !isInnerClass(file) => classLoader loadClass fullQualifiedName(file)
      }
      Success(classes)
    }
  }

  private def collectFiles(f: AbstractFile): Seq[AbstractFile] = {
    val these = f.iterator.toSeq
    these.filterNot(_.isDirectory) ++ these.filter(_.isDirectory).flatMap(collectFiles)
  }

  /* Each file name is being constructed from a path in the virtual directory. */
  def fullQualifiedName(classFile: AbstractFile) = {
    val path = classFile.path
    path.substring(path.indexOf('/') + 1, path.lastIndexOf('.')).replace("/",".")
  }

  def isInnerClass(classFile: AbstractFile) = classFile.name.contains('$')
}

/* Compilation exception

   Compilation exception is defined this way.
   It contains program was compiling and error positions with messages
   of what went wrong during compilation.
  */
class CompilationFailedException(val messages: Map[String, Map[Int, String]])
  extends Exception(s"Compilation completed with ${messages.values.map(_.size).sum} errors.") {

  private val errorsByFile = messages map {
    case (name, errors) => errors.toSeq.sortBy(_._1).foldLeft(name + "\n") { case (s, (line, msg)) => s + s"$line. $msg\n" }
  }

  val errorsToString = errorsByFile mkString "\n"
}