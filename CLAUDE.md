# AGENTS.md

Guidance for AI coding agents working in this repository.

This is a pure Scala library of common types and utilities for the Menger ray tracer: domain primitives, configuration types, rendering enums. No native code, no CUDA. Published to Maven Central as `io.github.lene:menger-common_3`.

---

## Critical rules

1. **Never commit directly to `main`.** Always work on a feature branch. Open a PR to merge into main. If on `main`, switch to (or create) a feature branch first.
2. **Never push without explicit user confirmation.** Commit locally, show the diff, wait for "push."
3. **Always monitor the CI pipeline after pushing.** Fix any failures.
4. **Never `git add -A`.** Add files explicitly.
5. **Never commit failing tests.**

---

## Git hooks

Hooks live in `.git_hooks/` (tracked). On a fresh clone, activate them with:

```bash
git config core.hooksPath .git_hooks
```

| Hook | When | Checks |
|------|------|--------|
| pre-commit | on `git commit` | compile, tests, scalafix |
| pre-push | on `git push` | compile, tests, scalafix |

---

## Branch and PR workflow

```bash
git checkout -b fix/short-description   # always branch from main
# ... make changes, commit ...
git push origin fix/short-description
gh pr create --title "..." --body "..."
```

CI runs on every PR. All jobs must pass before merging.

---

## Maven Central incident protocol

Artifacts on Maven Central are **permanent — cannot be deleted**. If a defective artifact is published:
1. Open an issue documenting the defect
2. Fix the defect on a branch, bump patch version, publish new version
3. Add `**Note:** X.Y.Z is defective — use X.Y.Z+1` to CHANGELOG.md

---

## Common commands

```bash
sbt compile                    # Compile
sbt test                       # All tests
sbt "testOnly ClassName"       # Specific test
sbt "scalafix --check"         # Code style check
sbt publishLocal               # Local ivy publish
```

Pipeline monitoring:
```bash
gh run list --limit 5
gh run view <run-id>
```
