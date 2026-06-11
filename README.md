# menger-common

Common Scala 3 types and utilities used by the Menger renderer projects.

## Dependency

```scala
libraryDependencies += "io.github.lene" %% "menger-common" % "0.1.1"
```

Artifacts are published to Maven Central under `io.github.lene:menger-common_3`.

## CI and releases

Every branch push and pull request runs:

- quality on Java 21 and 25: version policy, sbt launcher check, Scalafix, tests,
  package, and Scaladoc
- coverage with an 80% hard floor, 90% target, and at most 1% drop while below
  target
- MiMa binary compatibility against the version in `.mima_previous_version`
- Java consumer smoke test against the locally published Maven artifact

Pushes to `main` only create a release tag after all gates pass and the merged PR
title does not contain `NORELEASE`. Release tags must match `build.sbt`, point to a
commit reachable from `origin/main`, and publish to Maven Central before the
post-publish Java smoke test runs against the public artifact.

Maven Central publication requires these GitHub Actions secrets:
`SONATYPE_USERNAME`, `SONATYPE_PASSWORD`, `PGP_SECRET`, and `PGP_PASSPHRASE`.

## Code standards

`.scalafix.conf` and the `standards/` hook scripts are **canonical in the
[menger](https://gitlab.com/lilacashes/menger) repository**. Do not edit them
here directly. To propagate updates from menger:

```bash
# from your menger checkout:
./scripts/sync-standards.sh /path/to/menger-common
# review the diff, then commit and push in menger-common
```

A scheduled CI job in menger checks that these files are byte-identical across
all three repos and fails if they diverge.
