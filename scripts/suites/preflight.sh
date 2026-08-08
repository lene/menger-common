#!/bin/sh
# Suite: preflight (Sprint 36 B1). Changelog entry + version/tag consistency,
# extracted verbatim from the pre-push hook's former inline check.
set -u
. ./standards/hooks/lib.sh

RED_TEXT='\e[38;5;196m'
GREEN_TEXT='\e[38;5;46m'
RESET_TEXT='\e[0m'

echo "=== Changelog & Version ==="
VERSION=$(grep 'version :=' build.sbt | cut -d'"' -f2)
if [ -z "$VERSION" ]; then
  echo "Changelog: ${RED_TEXT}FAILED${RESET_TEXT} (cannot read version from build.sbt)"
  suite_fail preflight 1 "version-unreadable"
  exit 1
fi

MAIN_VERSION=$(git show origin/main:build.sbt 2>/dev/null | grep 'version :=' | cut -d'"' -f2)
VERSION_BUMPED=0
[ "$VERSION" != "$MAIN_VERSION" ] && VERSION_BUMPED=1
# A version differing from main's is usually a release being prepared — but it is also
# exactly what an already-released branch looks like when merged back. Detect the
# latter: $VERSION is the most recent tag reachable from HEAD, so this branch IS the
# release that produced that tag being merged back — not a version bump in progress.
LATEST_TAG=$(git describe --tags --abbrev=0 2>/dev/null || echo "")
MERGE_BACK=0
if [ "$VERSION_BUMPED" -eq 1 ] && [ "${LATEST_TAG#v}" = "$VERSION" ]; then
  MERGE_BACK=1
fi

if ! grep -qF "## [$VERSION]" CHANGELOG.md; then
  echo "Changelog: ${RED_TEXT}FAILED${RESET_TEXT} (missing '## [$VERSION]' entry)"
  suite_fail preflight 1 "changelog-entry-missing"
  exit 1
elif ! grep -qF "[$VERSION]:" CHANGELOG.md; then
  echo "Changelog: ${RED_TEXT}FAILED${RESET_TEXT} (missing link '[$VERSION]:' at bottom)"
  suite_fail preflight 1 "changelog-link-missing"
  exit 1
elif [ "$VERSION_BUMPED" -eq 1 ] && [ "$MERGE_BACK" -eq 0 ] && ! grep -qF "## [$VERSION] - $(date +%Y-%m-%d)" CHANGELOG.md; then
  echo "Changelog: ${RED_TEXT}FAILED${RESET_TEXT} (new version $VERSION needs today's date $(date +%Y-%m-%d))"
  suite_fail preflight 1 "changelog-date"
  exit 1
elif [ "$VERSION_BUMPED" -eq 1 ] && [ "$MERGE_BACK" -eq 0 ] && git tag | grep -q "^${VERSION}$"; then
  echo "Version: ${RED_TEXT}FAILED${RESET_TEXT} (tag $VERSION already exists — bump version)"
  suite_fail preflight 1 "tag-exists"
  exit 1
elif [ "$MERGE_BACK" -eq 1 ]; then
  echo "Changelog & Version: ${GREEN_TEXT}OK${RESET_TEXT} (merging released $VERSION back into main)"
else
  echo "Changelog & Version: ${GREEN_TEXT}OK${RESET_TEXT}"
fi

suite_pass preflight
