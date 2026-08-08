# AGENTS.md

Guidance for AI coding agents working in this repository.

This is a pure Scala library of common types and utilities for the Menger ray tracer: domain primitives, configuration types, rendering enums. No native code, no CUDA. Published to Maven Central as `io.github.lene:menger-common_3`.

---

<!-- BEGIN shared rules (synced from menger-toplevel — edit there) -->
## Critical rules

These are non-negotiable. Violating any of them causes real harm.

1. **Never commit directly to `main`.** Work on a feature branch and open a PR/MR to merge in. If currently on `main`, switch to (or create) a feature branch first.
2. **Never push without explicit user confirmation.** Commit locally, show the diff, wait for "push."
3. **Always monitor the CI pipeline after pushing.** If any failures occur, fix them.
4. **Never `git add -A`.** Add files explicitly.
5. **Never commit failing tests.** Hooks enforce this; do not bypass them.
6. **Never rewrite a test to make it pass without investigation.** Failing tests usually catch real bugs.
7. **Never delete data without explicit user confirmation.** This includes generated artifacts, caches, and reference images.
8. **Never infer values the user should provide** (version numbers, branch names, paths). Ask.
9. **When a skill or instruction says "confirm with user," it is a hard stop.** A prior message in the conversation does not satisfy a fresh checkpoint — ask again.

## Shared conventions

- **Alpha channel:** `0.0` = fully transparent (no opacity, no absorption), `1.0` = fully opaque. This holds everywhere alpha appears — OptiX shaders, Beer-Lambert absorption, `Color`, tests. Getting it inverted is a recurring, cross-repo bug.
- **The pre-push hook is the Definition-of-Done gate.** A task is done when its repo's pre-push hook passes on the change — not when a hand-picked subset of checks does. Don't assemble a substitute for it.
- **Delete-on-resolve:** when a finding in `ARCHITECTURE_REVIEW.md` or `CODE_IMPROVEMENTS.md` is resolved, strike it through (`~~text~~`) and add a `**✅ Resolved (sprint/task):**` note with the sprint reference — do not delete the entry outright (the audit trail matters). Each repo that carries a `CODE_IMPROVEMENTS.md` is its own ledger; cross-repo findings (optix-jni, menger-common) should be seeded there when they accumulate.
<!-- END shared rules -->

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

## Pointers

| Where | What |
|---|---|
| `docs/ENFORCEMENT.md` | Policy → mechanism map; open enforcement gaps |
| `../docs/QA_INCIDENTS.md` | Cross-repo QA incident log (workspace repo) |

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
gh run rerun <run-id> --failed   # re-run only the failed jobs — the standard retry, not a full re-push
```
