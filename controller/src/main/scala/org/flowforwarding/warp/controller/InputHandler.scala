/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller

import java.io.{FileNotFoundException, File}
import java.util.concurrent.TimeUnit
import java.net.{URL, URLClassLoader, InetSocketAddress}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout

private class InputHandler(controller: ActorRef, instructionsPath: Option[String]) extends Actor with ActorLogging{

  def prompt() = { self ! StdIn.readLine("warp> ") }
  def readLine() = { self ! StdIn.readLine() }

  override def preStart() = {
    instructionsPath foreach { path =>
      println(s"Controller started with initial instructions source: file $path.")
      readFile(path)
    }
    prompt()
  }

  import org.flowforwarding.warp.controller.ModuleManager._

  object Port {
    def unapply(input: String): Option[Int] = Try { input.toInt } filter { i => i >= 0 && i <= 65535 } toOption
  }

  def addPath(s: String) {
    val urlClassLoader = Thread.currentThread.getContextClassLoader.asInstanceOf[URLClassLoader]
    val urlClass = classOf[URLClassLoader]
    val method = urlClass.getDeclaredMethod("addURL", classOf[URL])
    method.setAccessible(true)
    method.invoke(urlClassLoader, new File(s).toURI.toURL)
  }

  object ControllerCommand {
    def unapply(input: String): Option[Any] = input.split(' ') match {
      case Array("start", ip, Port(tcpPort)) =>
        Some(Start(new InetSocketAddress(ip, tcpPort)))  // TODO: Match IP
      case Array("set", "factory", factoryClass, "-p", args @ _*) =>
        Some(SetFactory(factoryClass, args.toArray))
      case Array("add", "module", moduleName, "of", "type", moduleClass, "-p", args @ _*) =>
        Some(AddModule(moduleName, moduleClass, args.toArray))
      case Array("rm", "module", moduleName) =>
        Some(RemoveModule(moduleName))
      case _ => None
    }
  }

  object NotCommand{
    def unapply(input: String): Boolean = input == "" | input.startsWith("#")
  }

  object ShowHelp {
    def unapply(input: String): Boolean = input == ":help"
  }

  object PasteMode {
    def unapply(input: String): Boolean = input == ":paste"
  }

  object ReadFile{
    def unapply(input: String): Option[String] = input.split(' ') match {
      case Array(":file", path) => Some(path)
      case _ => None
    }
  }

  def responseMessage: PartialFunction[Try[Any], String] = {
    case Success(Started(address, None)) =>
      s"Started at $address"
    case Success(Started(address, Some(failure))) =>
      s"Start failed: ${failure.getMessage}"

    case Success(SetFactoryResponse(None)) =>
      s"Factory was set successfully."
    case Success(SetFactoryResponse(Some(failure))) =>
      log.error(failure, "Set factory error.")
      s"Unable to set factory."

    case Success(AddModuleResponse(moduleName, None)) =>
      s"Module $moduleName added"
    case Success(AddModuleResponse(moduleName, Some(failure))) =>
      log.error(failure, "Add module error.")
      s"Unable to add module $moduleName"

    case Success(RemoveModuleResponse(moduleName, None)) =>
      s"Module $moduleName removed"
    case Success(RemoveModuleResponse(moduleName, Some(failure))) =>
      log.error(failure, "Remove module error.")
      s"Unable to remove module $moduleName"

    case Success(response) =>
      s"Response: $response"

    case Failure(th) =>
      log.error(th, "Command error.")
      s"Command request failed."
  }

  def helpString: String =
    """
      |:help                                                                             print this summary
      |:paste                                                                            enter the paste mode
      |:file <path_to_file>                                                              read instructions from a file
      |set factory <factory_class_name> -p [<param1>, <param2>, ...]                     load factory (reloading is not implemented yet)
      |add module <module_name> of type <module_class_name> -p [<param1>, <param2>, ...] load a module
      |rm module <module_name>                                                           remove a module
      |start <ip> <port>                                                                 start to accept incoming connections
    """.stripMargin

  def execute(cmd: Any) = {
    implicit val t = new Timeout(40, TimeUnit.SECONDS)
    val r = Try { Await.result(controller ? cmd, t.duration) }
    println(responseMessage(r))
  }
  
  
  def interpretCommands(lines: Iterable[String], fileName: String, noCommandsMessage: String, executingMessage: String) = {
    val commandLines = lines.zipWithIndex
                            .filterNot { case (line, _) => NotCommand.unapply(line) }

    val finishMessage = if(commandLines.isEmpty) noCommandsMessage
                        else executingMessage

    println(finishMessage)

    commandLines foreach {
      case (ControllerCommand(cmd), _) =>
        execute(cmd)
      case (line, i) =>
        println(s"$fileName:$i: Invalid command: $line")
    }
  }

  def pasteMode(lines: List[String]): Receive = {
    case s: String if s == "" && lines.headOption == Some("") =>
      interpretCommands(lines.reverse, "<console>", "// No commands pasted.", "// Exiting paste mode, now executing commands.")
      context become normalMode
      prompt()
    case s: String =>
      context become pasteMode(s :: lines)
      readLine()
  }
  
  def readFile(path: String) = {
    try {
      val lines = scala.io.Source.fromFile(path).getLines()
      println("// File contents: ")
      lines foreach println
      interpretCommands(scala.io.Source.fromFile(path).getLines().toIterable, path, "// No commands found.", "// Executing commands.")
    }
    catch {
      case e: FileNotFoundException => println("File not found.")
      case e: Throwable => println("Error: " + e.getMessage)
    }
  }

  def normalMode: Receive = {
    case ControllerCommand(cmd) =>
      execute(cmd)
      prompt()
    case ShowHelp() =>
      println(helpString)
      prompt()
    case PasteMode() =>
      context become pasteMode(List.empty)
      println("// Entering paste mode (post two empty lines to finish)")
      readLine()
    case ReadFile(path) =>
      readFile(path)
      prompt()
    case NotCommand() =>
      prompt()
    case line =>
      println("Invalid command: " + line)
      prompt()
  }

  def receive = normalMode
}
