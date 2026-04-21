# Benchmarks

## Physics solver (JVM)

Hardware: MacBook Pro (Apple Silicon, running the test on the JVM, no
Android emulator involved).

Workload: `PhysicsWorldStabilityTest.benchmark_fifty_bubbles_one_thousand_steps`.
The test packs 50 random-radius bubbles via `CirclePacker` and then calls
`PhysicsWorld.step(1f/60f)` one thousand times. `PhysicsConfig` defaults are
in effect (`constraintIterations = 6`, `centerAttraction = 0.015`,
`drag = 0.92`).

| Scenario | Time |
|---|---|
| 50 bubbles × 1000 steps | **~25 ms** |

Per-step amortised: ~25 µs for 50 bubbles. At 60 fps (16.6 ms per frame
budget) the pure physics work uses a negligible fraction of the frame,
with large headroom for rendering, text measurement, and input handling.

Scaling is O(n²) in the pairwise collision loop. Extrapolating linearly
to 100 bubbles gives ~100 µs/step, still well inside budget. Spatial
hashing is therefore **not implemented in v1.0.0** — the cost of the
complexity outweighs the savings for realistic bubble counts (≤ 100).

## Device-side rendering (pending user verification)

The 60 fps @ 100 bubbles goal on a Pixel 6 is a device-side check
involving Compose recomposition, `TextMeasurer`, `Canvas.drawImage`, and
the `withFrameNanos` loop — none of which this JVM benchmark exercises.
Numbers will be added here after the sample app is smoke-tested.

## Repro

```
./gradlew :bubblepicker:test --tests "*benchmark_fifty_bubbles_one_thousand_steps"
```

(Or simply `./gradlew :bubblepicker:test` — the benchmark is part of the
regular unit-test suite.)
