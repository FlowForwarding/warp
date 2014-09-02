import sbt._
import Keys._

import sbtassembly.Plugin._
import AssemblyKeys._

object Build extends Build{
  import sbtassembly.Plugin._
  import Dependencies._

  val SCALA_MAJOR_VERSION = "2.11"
  val SCALA_MINOR_VERSION = "1"
  val SCALA_VERSION = SCALA_MAJOR_VERSION + "." + SCALA_MINOR_VERSION

  val WARP_VERSION = "0.1"
  val SCALAVRO_VERSION = "0.6.2-patched"

  lazy val warpCommonSettings = Defaults.defaultSettings ++ Seq(
    organization  := "org.flowforwarding",
    version       := "0.5",
    scalaVersion  := SCALA_VERSION,
    scalacOptions := Seq("-deprecation", "-unchecked", "-encoding", "utf8"),
    resolvers     ++= Dependencies.resolutionRepos
  )

  lazy val of_driver_assemblySettings = sbtassembly.Plugin.assemblySettings ++ Seq(mainClass := Some("org.flowforwarding.warp.jcontroller.JController"))

  lazy val of_driver = Project("of_driver", file("./of_driver"), settings = warpCommonSettings ++ of_driver_assemblySettings)
    .settings(
      libraryDependencies ++= compile(akka, slf4j, logback_core, logback, netty, jackson_core_asl, jackson_mapper_asl)
    )

  lazy val controller = Project("controller", file("./controller"), settings = warpCommonSettings ++ baseAssemblySettings)
    .settings(
      unmanagedJars in Compile  <++=
        baseDirectory map { base =>
          val format = (s: String) => base / ".." / s / "target" / s"scala-$SCALA_MAJOR_VERSION" / s"$s-assembly-$WARP_VERSION.jar"
          val jars = Seq("sdriver", "jdriver", "idriver", "demo") map format
          jars.foldLeft(PathFinder.empty) { _ +++ _ } classpath
        },
      mergeStrategy in assembly <<= (mergeStrategy in assembly) { old => {
          case s if s contains "org\\flowforwarding\\warp\\controller" => MergeStrategy.first
          case x => old(x)
        }
      },
      libraryDependencies ++= compile(akka, spray_json, spray_http, spray_can, spray_routing, spire) ++ test(scalatest)
    )

//  lazy val jdriver = Project("jdriver", file("./jdriver"), settings = warpCommonSettings ++ assemblySettings)
//    .settings(
//      unmanagedBase <<= baseDirectory { base => base / "lib" }
//    ).dependsOn(controller)

  lazy val sdriver = Project("sdriver", file("./sdriver"), settings = warpCommonSettings ++ assemblySettings)
    .settings(
      libraryDependencies ++= compile(scala_reflect, scala_compiler)
    ).dependsOn(util, core, controller)


//  lazy val demo = Project("demo", file("./demo"), settings = warpCommonSettings ++ assemblySettings)
//    .settings(
//      libraryDependencies ++= compile(spray_client, spray_json, spray_httpx)
//    )  
//    .dependsOn(controller, sdriver)

//  lazy val idriver = Project("idriver", file("./idriver"), settings = warpCommonSettings ++ assemblySettings)
//  .settings(
//      libraryDependencies ++= compile(scala_pc)
//    )

  //////////////////////////////////////////////////////////////////////////////
  // SCALAVRO PROJECT INFO
  //////////////////////////////////////////////////////////////////////////////

  //////////////////////////////////////////////////////////////////////////////
  // SUB-PROJECTS
  //////////////////////////////////////////////////////////////////////////////

  def scalavroSubProject(name: String) = "%s-%s".format("scalavro", name)

  lazy val core = Project(
    id = scalavroSubProject("core"),
    base = file("./scalavro/core"),
    settings = commonSettings
  ) settings (
    // use patched version for now, the actual one hides it and breaks jdriver functionality
    unmanagedBase <<= baseDirectory { base => base / ".." / "jdriver" / "lib" },
    libraryDependencies ++= Seq(spray_json, scala_xml, avro)
    //libraryDependencies ++= Seq(spray_json, jackson_core_asl, jackson_mapper_asl, avro)
  ) dependsOn util

  lazy val util = Project(
    id = scalavroSubProject("util"),
    base = file("./scalavro/util"),
    settings = commonSettings
  ) settings(
    libraryDependencies ++= Seq(reflections, config)
  )

  //////////////////////////////////////////////////////////////////////////////
  // SHARED SETTINGS
  //////////////////////////////////////////////////////////////////////////////

  lazy val commonSettings = Project.defaultSettings ++
    basicSettings ++
    publishSettings

  lazy val basicSettings = Seq(
    version := SCALAVRO_VERSION,
    organization := "com.gensler",
    scalaVersion := SCALA_VERSION,
    resolvers ++= Dependencies.resolutionRepos,
    libraryDependencies ++= Seq(scala_reflect, slf4j, logback % "runtime", scalatest % "test"),
    scalacOptions in Compile ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature"
    ),
    parallelExecution in Test := false,
    fork in Test := true
  )

  lazy val publishSettings = Seq(
    publishMavenStyle := true,
    pomIncludeRepository := { _ => false },
    licenses := Seq(
      "BSD-style" -> url("http://opensource.org/licenses/BSD-2-Clause")
    ),
    homepage := Some(url("http://genslerappspod.github.io/scalavro/")),
    pomExtra :=
      <scm>
        <url>git@github.com:GenslerAppsPod/scalavro.git</url>
        <connection>scm:git:git@github.com:GenslerAppsPod/scalavro.git</connection>
      </scm>
        <developers>
          <developer>
            <id>ConnorDoyle</id>
            <name>Connor Doyle</name>
            <url>http://gensler.com</url>
          </developer>
        </developers>,
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    }
  )
}
