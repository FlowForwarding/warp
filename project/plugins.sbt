resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.3")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.12.0")