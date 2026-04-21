package com.dongnh.bubblepicker.physics

import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import kotlin.random.Random

class PhysicsWorldStabilityTest {

    private val dt = 1f / 60f

    @Test
    fun twenty_packed_bubbles_reach_rest_within_two_seconds() {
        val world = buildWorld(seed = 1L, count = 20)
        repeat(120) { world.step(dt) }
        assertTrue("world did not reach rest after 120 steps", world.isAtRest())
    }

    @Test
    fun twenty_bubbles_produce_no_nan_or_infinity_over_one_thousand_steps() {
        val world = buildWorld(seed = 2L, count = 20)
        repeat(1_000) { world.step(dt) }
        for (b in world.bubbles) {
            assertTrue("NaN/inf position at ${b.id}: ${b.position}",
                b.position.x.isFinite() && b.position.y.isFinite())
        }
    }

    @Test
    fun twenty_bubbles_remain_non_overlapping_over_one_thousand_steps() {
        val world = buildWorld(seed = 3L, count = 20)
        repeat(1_000) { world.step(dt) }
        assertNoOverlap(world, tolerance = 1e-2f)
    }

    @Test
    fun fifty_bubbles_survive_random_pin_unpin_events_without_nan_or_hang() {
        val world = buildWorld(seed = 4L, count = 50)
        val rng = Random(seed = 4L)
        repeat(2_000) { frame ->
            if (frame % 37 == 0) {
                val victim = world.bubbles[rng.nextInt(world.bubbles.size)]
                world.pin(victim.id, victim.position + Vec2(rng.nextFloat(), rng.nextFloat()))
            }
            if (frame % 53 == 0) {
                val pinned = world.bubbles.firstOrNull { it.pinned }
                if (pinned != null) world.unpin(pinned.id)
            }
            world.step(dt)
        }
        for (b in world.bubbles) {
            assertTrue("NaN/inf position at ${b.id}",
                b.position.x.isFinite() && b.position.y.isFinite())
        }
    }

    @Ignore("Manual benchmark — enable locally to confirm <200ms on dev laptop")
    @Test
    fun benchmark_fifty_bubbles_one_thousand_steps() {
        val world = buildWorld(seed = 99L, count = 50)
        val start = System.nanoTime()
        repeat(1_000) { world.step(dt) }
        val elapsedMs = (System.nanoTime() - start) / 1_000_000.0
        println("50 bubbles x 1000 steps: ${"%.1f".format(elapsedMs)} ms")
        assertTrue("benchmark exceeded 200ms: $elapsedMs", elapsedMs < 200.0)
    }

    private fun buildWorld(seed: Long, count: Int): PhysicsWorld {
        val rng = Random(seed = seed)
        val items = (1L..count.toLong()).map { it to (0.3f + rng.nextFloat() * 0.7f) }
        val packed = CirclePacker.pack(items)
        val world = PhysicsWorld(PhysicsConfig(seed = seed))
        packed.forEach { world.add(it) }
        return world
    }

    private fun assertNoOverlap(world: PhysicsWorld, tolerance: Float) {
        val bubbles = world.bubbles
        for (i in bubbles.indices) {
            for (j in i + 1 until bubbles.size) {
                val a = bubbles[i]
                val b = bubbles[j]
                val d = (a.position - b.position).length()
                val minD = a.radius + b.radius
                assertTrue(
                    "bubble ${a.id}/${b.id} overlap dist=$d minDist=$minD",
                    d >= minD - tolerance
                )
            }
        }
    }
}
