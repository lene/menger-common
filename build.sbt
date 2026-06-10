import xerial.sbt.Sonatype.sonatypeCentralHost

name := "menger-common"
version := "0.1.1"
scalaVersion := "3.8.3"

organization := "io.github.lene"
description := "Common types and utilities for Menger ray tracer"
homepage := Some(url("https://github.com/lene/menger-common"))
licenses := Seq("Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0"))
scmInfo := Some(ScmInfo(
  url("https://github.com/lene/menger-common"),
  "scm:git:git@github.com:lene/menger-common.git"
))
developers := List(
  Developer("lene", "Lene Preuss", "lene.preuss@gmail.com", url("https://github.com/lene"))
)

sonatypeCredentialHost := sonatypeCentralHost
publishTo := sonatypePublishToBundle.value
publishMavenStyle := true
pomIncludeRepository := { _ => false }
versionScheme := Some("semver-spec")
pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toArray)

scalacOptions ++= Seq("-deprecation", "-explain", "-feature", "-Wunused:imports")
Compile / doc / scalacOptions := (Compile / scalacOptions).value.filterNot(
  _.startsWith("-Wunused")
)

Compile / semanticdbEnabled := true

coverageMinimumStmtTotal := 80.0
coverageFailOnMinimum := true
coverageHighlighting := true

Compile / wartremoverErrors ++= Seq(
  Wart.Var,
  Wart.While,
  Wart.AsInstanceOf,
  Wart.IsInstanceOf,
  Wart.Throw
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.20" % Test,
  "org.scalatestplus" %% "scalacheck-1-19" % "3.2.20.0" % Test,
  "com.tngtech.archunit" % "archunit" % "1.3.0" % Test
)

mimaPreviousArtifacts := {
  val previousVersion = IO.read(baseDirectory.value / ".mima_previous_version").trim
  Set(organization.value %% moduleName.value % previousVersion)
}
