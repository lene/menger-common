#!/bin/sh
# Suite: coverage (Sprint 36 B1). CI-only (the local pre-push hook does not run coverage
# today — see the repo's docs/ENFORCEMENT.md open issue: measured but not ratcheted).
# Extracted verbatim from the ci.yml unit-tests job.
set -u
. ./standards/hooks/lib.sh

echo "=== Coverage ==="
if sbt coverage test coverageReport coverageAggregate; then
  echo "Coverage: OK"
  suite_pass coverage
else
  echo "Coverage: FAILED"
  suite_fail coverage 1 "coverage-run"
  exit 1
fi
