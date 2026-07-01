# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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

[0.1.4]: https://github.com/lene/menger-common/compare/0.1.3...0.1.4
[0.1.3]: https://github.com/lene/menger-common/compare/0.1.2...0.1.3
[0.1.2]: https://github.com/lene/menger-common/compare/0.1.1...0.1.2
[0.1.1]: https://github.com/lene/menger-common/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/lene/menger-common/releases/tag/0.1.0
