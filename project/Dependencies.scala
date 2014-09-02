import sbt._

object Dependencies {
  val resolutionRepos = Seq(
    "Typesafe releases"   at "http://repo.typesafe.com/typesafe/releases/",
    "Typesafe repository" at "http://repo.typesafe.com/typesafe/repo/",
    "spray"               at "http://repo.spray.io/"
  )

  def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")
  def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  object V {
    //val jackson       = "1.8.8" //not the latest version (required by patched avro)
    val akka          = "2.3.2"
    val spray         = "1.3.1-20140423" // nightly
    val spray_json    = "1.2.6"
    val reflections   = "0.9.9-RC1"
    val logback       = "1.1.2"
    val slf4j         = "1.7.7"
    val scala_version = "2.11.0"
    val scala_xml     = "1.0.1"
    val scala_pc      = "1.0.1"
    val netty         = "3.6.3.Final"
    val jackson       = "1.9.13"
    val avro          = "1.7.6"
    val config        = "1.0.2"
    val scalatest     = "2.1.6"
    val spire         = "0.7.5"
  }

  val akka               = "com.typesafe.akka"    %% "akka-actor"         % V.akka

  val netty              = "io.netty"             % "netty"               % V.netty
  val jackson_core_asl   = "org.codehaus.jackson" % "jackson-core-asl"    % V.jackson
  val jackson_mapper_asl = "org.codehaus.jackson" % "jackson-mapper-asl"  % V.jackson
  val config             = "com.typesafe"         %  "config"             % V.config
  val scalatest          = "org.scalatest"        %% "scalatest"          % V.scalatest

  val spray_json         = "io.spray"             %% "spray-json"         % V.spray_json
  val spray_routing      = "io.spray"             %% "spray-routing"      % V.spray
  val spray_http         = "io.spray"             %% "spray-http"         % V.spray
  val spray_httpx        = "io.spray"             %% "spray-httpx"        % V.spray
  val spray_can          = "io.spray"             %% "spray-can"          % V.spray
  val spray_client       = "io.spray"             %% "spray-client"       % V.spray

  val spire              = "org.spire-math"       %% "spire"              % V.spire

  val scala_reflect      = "org.scala-lang"       %  "scala-reflect"      % V.scala_version
  val scala_compiler     = "org.scala-lang"       %  "scala-compiler"     % V.scala_version
  val scala_xml          = "org.scala-lang.modules" %% "scala-xml"        % V.scala_xml
  val scala_pc           = "org.scala-lang.modules" %% "scala-parser-combinators" % V.scala_pc

  val slf4j              = "org.slf4j"            %  "slf4j-api"          % V.slf4j
  val reflections        = "org.reflections"      %  "reflections"        % V.reflections
  val logback_core       = "ch.qos.logback"       %  "logback-core"       % V.logback
  val logback            = "ch.qos.logback"       %  "logback-classic"    % V.logback

  //val jackson_core_asl   = "org.codehaus.jackson" %  "jackson-core-asl"   % V.jackson  // fix of the "class file ... is broken" error
  //val jackson_mapper_asl = "org.codehaus.jackson" %  "jackson-mapper-asl" % V.jackson  // same

  val avro               = "org.apache.avro"      %  "avro"               % V.avro
}
