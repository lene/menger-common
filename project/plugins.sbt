// Test coverage (see https://www.baeldung.com/scala/sbt-scoverage-code-analysis)
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.4.4")
// Code quality and refactoring
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.14.6")
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.5.6")
addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "1.1.5")

// Publication
addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.3.1")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.12.2")
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
