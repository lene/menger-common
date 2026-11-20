name := "menger-common"
version := "0.4.1"
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

publishTo := sonatypePublishToBundle.value

sonatypeCredentialHost := "central.sonatype.com"
publishMavenStyle := true
pgpPassphrase := sys.env.get("PGP_PASSPHRASE").map(_.toArray)

scalacOptions ++= Seq("-deprecation", "-explain", "-feature", "-Wunused:imports")

Compile / semanticdbEnabled := true

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

// MiMa: binary compatibility against the last published release, run by the `mima` suite (pre-push and CI;
// menger-common#32: 0.4.1 removed RenderConfig.shadows, which optix-jni 0.5.7 calls, and nothing checked it).
// The previous release is the highest version tag other than the version being built (its tag can
// exist before its publish finishes), so it cannot go stale the way a fixed version does;
// without tags (a shallow clone) the set is empty and scripts/suites/mima.sh refuses to run.
// Every intentional break is a filter (mimaBinaryIssueFilters) with its reason, and the CHANGELOG lists it.
mimaPreviousArtifacts := {
  val tags = scala.util.Try(
    scala.sys.process.Process(Seq("git", "tag", "--list", "[0-9]*.[0-9]*.[0-9]*", "--sort=-v:refname")).!!
  ).getOrElse("")
  tags.linesIterator.filterNot(_ == version.value).toList.headOption.map(organization.value %% name.value % _).toSet
}
import com.typesafe.tools.mima.core._
mimaBinaryIssueFilters ++= Seq(
  // 0.4.1 (lene/menger#196): RenderConfig lost its `shadows` field (shadows are always on), so its
  // constructor, apply, copy and product accessors changed; optix-jni 0.5.8 is built against it.
  // Remove when the previous release is 0.4.1 or later.
  ProblemFilters.exclude[Problem]("menger.common.RenderConfig.*")
)
