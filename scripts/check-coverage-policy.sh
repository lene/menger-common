#!/usr/bin/env bash

set -euo pipefail

coverage_floor="80"
coverage_target="90"
max_drop_below_target="1"
baseline_file=".coverage_baseline"

repo_root="$(git rev-parse --show-toplevel)"
cd "$repo_root"

shopt -s nullglob
reports=()
for candidate in target/scoverage-report/scoverage.xml target/scala-*/scoverage-report/scoverage.xml; do
  if [ -f "$candidate" ]; then
    reports+=("$candidate")
  fi
done
shopt -u nullglob

if [ "${#reports[@]}" -eq 0 ]; then
  echo "Coverage report not found under target/scoverage-report or target/scala-*/scoverage-report" >&2
  exit 1
fi

if [ ! -f "$baseline_file" ]; then
  echo "$baseline_file is missing; commit the measured statement coverage baseline" >&2
  exit 1
fi

report="${reports[0]}"
raw_rate_entry="$(grep -m 1 -o 'statement-rate="[^"]*"' "$report" || true)"
raw_rate="${raw_rate_entry#statement-rate=\"}"
raw_rate="${raw_rate%\"}"

if [ -z "$raw_rate" ]; then
  echo "Could not parse statement-rate from $report" >&2
  exit 1
fi

normalize_rate() {
  awk -v rate="$1" 'BEGIN {
    if (rate <= 1) {
      rate *= 100
    }
    printf "%.2f", rate
  }'
}

coverage_rate="$(normalize_rate "$raw_rate")"
baseline_rate="$(normalize_rate "$(tr -d '[:space:]' < "$baseline_file")")"

drop="$(
  awk -v baseline="$baseline_rate" -v current="$coverage_rate" 'BEGIN {
    diff = baseline - current
    if (diff < 0) {
      diff = 0
    }
    printf "%.2f", diff
  }'
)"

echo "Statement coverage: current ${coverage_rate}%, baseline ${baseline_rate}%, drop ${drop}%"

if awk -v current="$coverage_rate" -v floor="$coverage_floor" \
  'BEGIN { exit current < floor ? 0 : 1 }'; then
  echo "Coverage failed: ${coverage_rate}% is below the ${coverage_floor}% hard floor." >&2
  exit 1
fi

below_target=1
if awk -v current="$coverage_rate" -v target="$coverage_target" \
  'BEGIN { exit current < target ? 0 : 1 }'; then
  below_target=0
fi

drop_exceeds_limit=1
if awk -v drop="$drop" -v allowed="$max_drop_below_target" \
  'BEGIN { exit drop > allowed ? 0 : 1 }'; then
  drop_exceeds_limit=0
fi

if [ "$below_target" -eq 0 ] && [ "$drop_exceeds_limit" -eq 0 ]; then
  echo \
    "Coverage failed: drop ${drop}% exceeds ${max_drop_below_target}% while below ${coverage_target}%." >&2
  exit 1
fi

echo "Coverage policy passed."
