# Changelog

All notable changes to this project are documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/)
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] — Unreleased

Full rewrite inspired by [`igalata/Bubble-Picker`](https://github.com/igalata/Bubble-Picker).
No code is shared with the original; the UX concept and visual language are
the only inheritance.

### Added
- Composable public API: `BubblePicker`, `rememberBubblePickerState`,
  `BubbleItem`, `BubbleStyle`, `BubbleGradient`, `BubbleGradientOrientation`.
- Pure-Kotlin Position-Based Dynamics (PBD) physics engine with zero Android
  dependencies, at `com.dongnh.bubblepicker.physics.*` and covered by JUnit
  unit tests.
- Gradient fills (`VERTICAL` / `HORIZONTAL` / `DIAGONAL`) and
  `backgroundImageUrl` loaded via Coil 3.
- Runtime mutation API on `BubblePickerState`: `addItem`, `addItems`,
  `removeItem`, `clear`, plus selection helpers `toggle`, `select`,
  `deselect`, `deselectAll` and `wake` to resume the physics loop.
- Battery-friendly frame loop that suspends when the world is at rest.
- Legacy compatibility layer under `com.dongnh.bubblepicker.legacy.*`:
  `BubblePickerView` (an `AbstractComposeView`), `BubblePickerAdapter`,
  `BubblePickerListener`, and `PickerItem`, with XML styleable attributes
  `backgroundColor` / `strokeColor` / `strokeWidth` / `minRadius` / `maxRadius`.
- Sample app with a Compose demo screen (brand picker) and a legacy XML demo
  screen, wired through a `NavHost`.

### Changed
- Rendering moved from OpenGL ES 2.0 + `GLSurfaceView` to Compose `Canvas`.
- Physics moved from JBox2D to a custom PBD solver (no third-party physics
  dependency).
- Package renamed from `com.igalata.bubblepicker` to `com.dongnh.bubblepicker`.
- Minimum SDK raised from 16 to 21. `compileSdk` and `targetSdk` raised to 35.
- JVM target raised to 17.
- Legacy `BubbleGradient` now takes Compose `Color` instead of `@ColorInt Int`.
  Consumers migrating from the fork must wrap raw colors with `Color(argb)`.
- Legacy `PickerItem` fields `icon`, `iconOnTop`, `overlayAlpha`, `typeface`,
  `textSize`, `showImageOnUnSelected`, `isViewBorderSelected`,
  `colorBorderSelected`, `strokeWidthBorder`, `customData` are kept for source
  compatibility but are **not rendered** in v1.0.0. They are reserved for
  future releases.

### Removed
- JBox2D dependency.
- OpenGL renderer, shaders, and `GLSurfaceView` usage.
- `com.dongnh.bubblepicker.model.BubbleGradient` (the `Int`-based version).
- `shot.gif` and other assets tied to the OpenGL demo.

### Known limitations
- Rotating the device preserves `selectedIds` (via `rememberSaveable` on the
  caller side) but not the settled physics positions.
- Accessibility (TalkBack content descriptions, keyboard nav) is not wired up.
  Tracked for a future release.
- RTL layout is not supported. LTR only.
- Android Lint is currently disabled in the library module due to a known
  `NoClassDefFoundError` in AGP 8.7's `UnrememberedStateDetector`. The code
  still compiles and tests still run; lint will be re-enabled once the upstream
  tool is fixed.
