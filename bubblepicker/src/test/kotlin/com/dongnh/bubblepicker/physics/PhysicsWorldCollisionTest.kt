package com.dongnh.bubblepicker.physics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhysicsWorldCollisionTest {

    private val dt = 1f / 60f

    @Test
    fun overlapping_pair_separates_after_one_step() {
        val world = PhysicsWorld(PhysicsConfig(centerAttraction = 0f, drag = 0f))
        world.add(Bubble(1L, Vec2(-0.2f, 0f), 1f))
        world.add(Bubble(2L, Vec2(0.2f, 0f), 1f))

        world.step(dt)

        val a = world.bubbles[0]
        val b = world.bubbles[1]
        val dist = (a.position - b.position).length()
        val minDist = a.radius + b.radius
        assertTrue("expected separation, got dist=$dist minDist=$minDist", dist >= minDist * 0.99f)
    }

    @Test
    fun concentric_pair_separates_without_producing_nan() {
        val world = PhysicsWorld(PhysicsConfig(centerAttraction = 0f, drag = 0f))
        world.add(Bubble(1L, Vec2(0f, 0f), 1f))
        world.add(Bubble(2L, Vec2(0f, 0f), 1f))

        world.step(dt)

        for (b in world.bubbles) {
            assertTrue("NaN position", !b.position.x.isNaN() && !b.position.y.isNaN())
            assertTrue("Infinity position", b.position.x.isFinite() && b.position.y.isFinite())
        }
        val dist = (world.bubbles[0].position - world.bubbles[1].position).length()
        assertTrue("still concentric", dist > 0f)
    }

    @Test
    fun far_apart_pair_does_not_converge_from_collision_alone() {
        val world = PhysicsWorld(PhysicsConfig(centerAttraction = 0f, drag = 0f))
        world.add(Bubble(1L, Vec2(-10f, 0f), 1f))
        world.add(Bubble(2L, Vec2(10f, 0f), 1f))

        val initialDistance = (world.bubbles[0].position - world.bubbles[1].position).length()
        repeat(60) { world.step(dt) }
        val finalDistance = (world.bubbles[0].position - world.bubbles[1].position).length()

        assertEquals(initialDistance, finalDistance, 1e-3f)
    }
}
