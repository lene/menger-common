# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.0] - 2026-08-04

### Removed

- **BREAKING:** `ObjectType` (the geometry-type vocabulary: `VALID_TYPES`, the
  `SPONGE_TYPES`/`PROJECTED_4D_TYPES`/… subsets, and the `isValid`/`isSponge`/`normalize`
  predicates) moved out of menger-common into the menger app (Sprint 35 Phase 4, finding
  F6). The type vocabulary changes with every new menger geometry type, which forced a
  menger-common release per feature — a shared kernel must not version with the app's
  feature cadence. Consumers: take `ObjectType` from `menger-app` (same
  `menger.common.ObjectType` API, unchanged) and pin menger-common 0.2.0.

## [0.1.6] - 2026-07-08

### Fixed

- `RenderConfig.shadows` now defaults to `true` (was `false`). Scenes rendered through the
  DSL `--scene` path without explicit `RenderSettings` fall back to `RenderConfig.Default`,
  so they now enable shadow rays by default — matching the CLI default and the
  `RenderSettings.shadows` default. Previously these scenes silently rendered without
  shadows: the Sprint 33 "shadows default on" change shipped on the menger side but the
  `RenderConfig` half (7066109) was missing from 0.1.5.

## [0.1.5] - 2026-07-04

### Changed

- `CausticsConfig.initialRadius` now accepts `0.0` (`CausticsConfig.AutoRadius`) as an
  auto-derive sentinel: the native PPM pass derives the gather radius from scene geometry
  (optix-jni >= 0.1.13). Positive values remain explicit overrides. Previously `0.0` was
  rejected. Binary-compatible relaxation (Sprint 33.9).

### Added

- `CausticsConfig.AutoRadius` constant (`0.0f`) naming the auto-derive sentinel

## [0.1.4] - 2026-07-01

### Added

- `dispersion` (Abbe number) field on `Material` — enables wavelength-dependent IOR
- `cauchyCoefficients(ior, V_d)` helper — derives Cauchy model coefficients A, B
- `GlassDispersive` and `DiamondDispersive` material presets with correct physical values
  (Sprint 32 spectral dispersion)

## [0.1.3] - 2026-06-29

### Added

- `"lsystem"` added to `ObjectType.VALID_TYPES` — enables CLI `type=lsystem` path
  (Sprint 31)

## [0.1.2] - 2026-06-28

### Added

- `"curve"` added to `ObjectType.VALID_TYPES` — fixes CLI `type=curve` path
  silently rejecting curves while DSL path worked (Sprint 30 critical fix)
- `ObjectType.isCurve` predicate

## [0.1.1] - 2026-06-07

### Removed

- `RenderConfig.gpuProject4D` field — dead code after GPU 4D path was decoupled in Sprint 26

## [0.1.0] - 2026-06-04

### Added

- Initial release extracted from menger-app (Sprint 26)

[0.2.0]: https://github.com/lene/menger-common/compare/0.1.6...0.2.0
[0.1.6]: https://github.com/lene/menger-common/compare/0.1.5...0.1.6
[0.1.5]: https://github.com/lene/menger-common/compare/0.1.4...0.1.5
[0.1.4]: https://github.com/lene/menger-common/compare/0.1.3...0.1.4
[0.1.3]: https://github.com/lene/menger-common/compare/0.1.2...0.1.3
[0.1.2]: https://github.com/lene/menger-common/compare/0.1.1...0.1.2
[0.1.1]: https://github.com/lene/menger-common/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/lene/menger-common/releases/tag/0.1.0
