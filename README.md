# menger-common

Pure Scala 3 library of the domain primitives shared by the Menger renderer projects: colors,
vectors, image sizes, lights, materials and material presets, plane/fog specs, object-type
names, constants, and the Menger exception hierarchy. No native code, no CUDA, no third-party
runtime dependencies beyond the Scala standard library.

Consumers: [`optix-jni`](https://github.com/lene/optix-jni) (generic OptiX bindings) and the
[`menger`](https://github.com/lene/menger) application. All three are developed together in the
[`menger-toplevel`](https://github.com/lene/menger-toplevel) workspace; see its arc42 docs
(§5.2.1) for this library's role and layering rules.

## Dependency

Published to Maven Central:

```scala
libraryDependencies += "io.github.lene" %% "menger-common" % "0.2.0"
```

The current version is the newest entry in [CHANGELOG.md](CHANGELOG.md). Maven Central
artifacts are permanent; if a release is marked defective there, use the next patch version.

## Development

```bash
sbt compile
sbt test
sbt "scalafix --check"
git config core.hooksPath .git_hooks   # once per clone; pre-push is the quality gate
```

Agent/contributor rules: [CLAUDE.md](CLAUDE.md).
