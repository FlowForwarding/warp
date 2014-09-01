import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object Builds extends Build {
   import Dependencies._
   
   val SCALA_MAJOR_VERSION = "2.11"
   val SCALA_MINOR_VERSION = "1"
   val SCALA_VERSION = SCALA_MAJOR_VERSION + "." + SCALA_MINOR_VERSION
   val WARP_VERSION = "0.5"

   lazy val warpCommonSettings = Defaults.defaultSettings ++ Seq(
      organization  := "org.flowforwarding",
      version       := WARP_VERSION,
      scalaVersion  := SCALA_VERSION,
      scalacOptions := Seq("-deprecation", "-unchecked", "-encoding", "utf8")
   )

   lazy val assemblySettings = sbtassembly.Plugin.assemblySettings ++ Seq(mainClass := Some("org.flowforwarding.warp.jcontroller.JController"))
   
//   lazy val of_driver = Project("of_driver", file("./of_driver"), settings = warpCommonSettings ++ assemblySettings) settings()

   lazy val of_driver = Project("of_driver", file("./of_driver"), settings = warpCommonSettings ++ assemblySettings)
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
}

