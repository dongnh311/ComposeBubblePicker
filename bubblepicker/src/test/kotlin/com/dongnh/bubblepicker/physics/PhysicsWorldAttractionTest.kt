package com.dongnh.bubblepicker.physics

import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class PhysicsWorldAttractionTest {

    private val dt = 1f / 60f

    @Test
    fun single_bubble_off_center_moves_toward_origin() {
        val world = PhysicsWorld()
        val start = Vec2(100f, 0f)
        world.add(Bubble(1L, start, 1f))

        world.step(dt)

        val bubble = world.bubbles[0]
        assertTrue(
            "expected x < ${start.x}, got ${bubble.position.x}",
            bubble.position.x < start.x
        )
    }

    @Test
    fun bubble_at_origin_stays_near_origin() {
        val world = PhysicsWorld()
        world.add(Bubble(1L, Vec2.ZERO, 1f))

        repeat(100) { world.step(dt) }

        val bubble = world.bubbles[0]
        assertTrue("drifted to ${bubble.position}", abs(bubble.position.x) < 1e-3f)
        assertTrue("drifted to ${bubble.position}", abs(bubble.position.y) < 1e-3f)
    }
}
