#!/bin/sh
# Suite: mima (menger-common#32). Binary compatibility against the last published release (the
# highest version tag, build.sbt `mimaPreviousArtifacts`); an intentional break is a filter there,
# with its reason. optix-jni and menger link against this library's published jars.
set -u
. ./standards/hooks/lib.sh

echo "=== MiMa ==="
LAST=$(git tag --list '[0-9]*.[0-9]*.[0-9]*' --sort=-v:refname | head -1)
if [ -z "$LAST" ]; then
  echo "MiMa: no release tag in this clone (fetch the tags: git fetch --tags)"
  suite_fail mima 1 "no release tag"
  exit 1
fi
echo "MiMa: against the last published release $LAST"
if sbt -batch mimaReportBinaryIssues; then
  echo "MiMa: OK"
  suite_pass mima
else
  echo "MiMa: FAILED (unfiltered binary incompatibility with $LAST)"
  suite_fail mima 1 "mimaReportBinaryIssues"
  exit 1
fi
