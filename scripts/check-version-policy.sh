#!/usr/bin/env bash

set -euo pipefail

release_mode=0
print_version=0

while [ "$#" -gt 0 ]; do
  case "$1" in
    --release)
      release_mode=1
      ;;
    --print-version)
      print_version=1
      ;;
    *)
      echo "Unknown argument: $1" >&2
      exit 2
      ;;
  esac
  shift
done

repo_root="$(git rev-parse --show-toplevel)"
cd "$repo_root"

version="$(
  awk -F '"' '/^[[:space:]]*version[[:space:]]*:=[[:space:]]*"/ { print $2; exit }' build.sbt
)"

if [ -z "$version" ]; then
  echo "Could not extract version from build.sbt" >&2
  exit 1
fi

if [ "$print_version" -eq 1 ]; then
  printf '%s\n' "$version"
  exit 0
fi

status=0

if ! printf '%s\n' "$version" | grep -Eq '^[0-9]+[.][0-9]+[.][0-9]+(-SNAPSHOT)?$'; then
  echo "build.sbt version must be semantic X.Y.Z or X.Y.Z-SNAPSHOT: $version" >&2
  status=1
fi

if printf '%s\n' "$version" | grep -q -- '-SNAPSHOT$'; then
  echo "Snapshot versions are not allowed for this Maven Central release repo: $version" >&2
  status=1
fi

readme_versions="$(
  grep -E 'menger-common' README.md |
    grep -Eo '[0-9]+[.][0-9]+[.][0-9]+(-SNAPSHOT)?' |
    sort -u || true
)"

if [ -z "$readme_versions" ]; then
  echo "README.md does not contain a menger-common dependency version" >&2
  status=1
else
  while IFS= read -r readme_version; do
    if [ "$readme_version" != "$version" ]; then
      echo "README.md menger-common version $readme_version does not match build.sbt $version" >&2
      status=1
    fi
  done <<< "$readme_versions"
fi

if ! grep -qF "## [$version]" CHANGELOG.md; then
  echo "CHANGELOG.md is missing entry '## [$version]'" >&2
  status=1
fi

if ! grep -qF "[$version]:" CHANGELOG.md; then
  echo "CHANGELOG.md is missing link reference '[$version]:'" >&2
  status=1
fi

main_version="$(
  git show origin/main:build.sbt 2>/dev/null |
    awk -F '"' '/^[[:space:]]*version[[:space:]]*:=[[:space:]]*"/ { print $2; exit }' || true
)"

version_bumped=0
if [ -n "$main_version" ] && [ "$version" != "$main_version" ]; then
  version_bumped=1
fi

if [ "$version_bumped" -eq 1 ] &&
  ! grep -qF "## [$version] - $(date +%Y-%m-%d)" CHANGELOG.md; then
  echo "CHANGELOG.md entry for new version $version must use today's date $(date +%Y-%m-%d)" >&2
  status=1
fi

local_tag_exists=0
if git rev-parse -q --verify "refs/tags/$version" >/dev/null; then
  local_tag_exists=1
fi

remote_tag_exists=0
if git ls-remote --exit-code --tags origin "refs/tags/$version" >/dev/null 2>&1; then
  remote_tag_exists=1
fi

if [ "$release_mode" -eq 1 ]; then
  if [ "$local_tag_exists" -eq 1 ]; then
    echo "Release tag already exists locally: $version" >&2
    status=1
  fi
  if [ "$remote_tag_exists" -eq 1 ]; then
    echo "Release tag already exists on origin: $version" >&2
    status=1
  fi
elif [ "$version_bumped" -eq 1 ]; then
  if [ "$local_tag_exists" -eq 1 ] || [ "$remote_tag_exists" -eq 1 ]; then
    echo "Version $version is already tagged; bump build.sbt before release work" >&2
    status=1
  fi
fi

if [ "$status" -eq 0 ]; then
  if [ "$release_mode" -eq 1 ]; then
    echo "Version policy passed for release $version"
  else
    echo "Version policy passed for $version"
  fi
fi

exit "$status"
