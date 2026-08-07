# Enforcement Audit: Policy → Mechanism Map

Living document. Reviewed at sprint close. Every ❌ row has an open issue;
resolve by implementing a gate or consciously accepting the gap. Follows the
format established in `menger/docs/ENFORCEMENT.md` (Sprint 36 A4).

**Status legend:**
- ✅ Enforced — structural gate; violations are rejected automatically
- ⚠️ Partial — enforced with known gaps
- 🤖 AI policy — behavioural instruction; mechanically unenforceable by design
- ❌ Unenforced — open gap, no gate yet

---

## Commit hygiene

| Policy | Source | Mechanism | Status |
|--------|--------|-----------|--------|
| No commits directly on `main` | CLAUDE.md §Critical rules | `standards/hooks/check-branch.sh` (pre-commit) | ✅ |
| No conflict markers or whitespace errors in staged diff | CLAUDE.md §Critical rules | `standards/hooks/check-staged-hygiene.sh` (pre-commit) | ✅ |
| No files > 5 MB staged | CLAUDE.md §Critical rules | `standards/hooks/check-staged-hygiene.sh` (pre-commit) | ✅ |
| Never `git add -A` | CLAUDE.md §Critical rules | 🤖 AI policy | 🤖 |
| Never push without explicit user confirmation | CLAUDE.md §Critical rules | 🤖 AI policy | 🤖 |

---

## Test discipline

| Policy | Source | Mechanism | Status |
|--------|--------|-----------|--------|
| Tests must pass before commit | CLAUDE.md §Critical rules | pre-commit: `sbt test` | ✅ |
| Tests must pass before push | CLAUDE.md §Critical rules | pre-push: `sbt test` | ✅ |
| Modified/deleted test files require `Test-Change:` trailer | CLAUDE.md §Critical rules (shared) | `standards/hooks/check-test-justification.sh` (pre-push) | ✅ |
| ArchUnit architecture rules | — | CI `archunit` job: `sbt "testOnly *ArchUnit*"` | ✅ |
| Coverage measured | — | CI `unit-tests` job: `sbt coverage test coverageReport coverageAggregate` | ✅ |
| Coverage floor / ratchet | — | Measured but not gated — no `.coverage_baseline` or CI check compares against a floor | ❌ |

---

## Code quality — Scala

| Policy | Source | Mechanism | Status |
|--------|--------|-----------|--------|
| Scalafix passes (`OrganizeImports`, `DisableSyntax`) | CLAUDE.md §Code style (shared) | pre-commit + pre-push: `sbt "scalafix --check"`; CI `scalafix` job | ✅ |

---

## Version & release

| Policy | Source | Mechanism | Status |
|--------|--------|-----------|--------|
| Changelog entry exists, dated, linked for the declared version | CLAUDE.md §Release workflow | pre-push Changelog & Version check; CI `changelog-updated` job (PRs) | ✅ |
| Git tag not already used by a different version | — | pre-push version check; CI `version-not-tagged` job | ✅ |
| Release triggers on merge to `main` | — | CI `create-tag` job (push to main) → tag → `publish` job; self-heals in-run when a tag exists but was never released (a CI-pushed tag cannot trigger a tag-ref run) | ✅ |
| Published artifact resolvable from Maven Central | — | CI `post-publish-smoke`: polls Central, runs a Java smoke test against the published artifact | ✅ |
| Maven Central incident protocol documented (artifacts are permanent) | CLAUDE.md §Maven Central incident protocol | Documentation only | 🤖 |

---

## Cross-repo standards

| Policy | Source | Mechanism | Status |
|--------|--------|-----------|--------|
| Shared configs byte-identical across menger / menger-common / optix-jni | workspace `shared/standards/` | Scheduled workspace CI: `scripts/check-standards-drift.sh` (daily, `check-drift.yml`) | ✅ |
| Local standards parity before every commit | workspace `shared/standards/` | pre-commit: `scripts/check-standards-drift.sh --local` | ✅ |
| Standards sync to sibling repos reviewed like any code change | workspace `shared/standards/README.md` | Manual: `scripts/sync-standards.sh` (no silent cross-repo writes) | ⚠️ |

---

## Agentic workflow (AI-only policies)

These policies govern AI agent behaviour and are structurally unenforceable by
a gate. Listed for completeness and to confirm the gap is consciously
accepted.

| Policy | Source | Status |
|--------|--------|--------|
| Never infer values the user should provide (version numbers, branch names, paths) | CLAUDE.md §Critical rules | 🤖 |
| Never delete data without explicit user confirmation | CLAUDE.md §Critical rules | 🤖 |
| Never rewrite a test to make it pass without investigation | CLAUDE.md §Critical rules | 🤖 |
| When a skill says "confirm with user", it is a hard stop | CLAUDE.md §Critical rules | 🤖 |
| Always monitor CI pipeline after push | CLAUDE.md §Critical rules | 🤖 |

---

## Open issues

| Policy gap | Action |
|-----------|--------|
| Coverage measured but not gated | Adopt menger's `.coverage_baseline` ratchet pattern (≥80%, max 1% drop) — this is the smallest, purest-Scala repo, a good place to pilot it before Sprint 36 Phase F extends ratchets elsewhere |
