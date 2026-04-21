package com.dongnh.bubblepicker.physics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhysicsWorldPinTest {

    private val dt = 1f / 60f

    @Test
    fun pinned_bubble_follows_update_pinned_exactly() {
        val world = PhysicsWorld()
        world.add(Bubble(1L, Vec2(0f, 0f), 1f))
        world.pin(1L, Vec2(5f, 3f))

        repeat(30) {
            world.updatePinned(1L, Vec2(5f + it, 3f - it))
            world.step(dt)
        }

        val bubble = world.bubbles[0]
        assertEquals(5f + 29f, bubble.position.x, 0f)
        assertEquals(3f - 29f, bubble.position.y, 0f)
    }

    @Test
    fun only_unpinned_bubble_moves_when_overlapping_pinned() {
        val world = PhysicsWorld(PhysicsConfig(centerAttraction = 0f, drag = 0f))
        world.add(Bubble(1L, Vec2(0f, 0f), 1f))
        world.add(Bubble(2L, Vec2(0.5f, 0f), 1f))
        world.pin(1L, Vec2(0f, 0f))

        val pinnedStart = world.bubbles[0].position
        world.step(dt)

        val pinnedAfter = world.bubbles[0].position
        val unpinnedAfter = world.bubbles[1].position
        assertEquals(pinnedStart.x, pinnedAfter.x, 0f)
        assertEquals(pinnedStart.y, pinnedAfter.y, 0f)
        assertTrue(
            "unpinned bubble should have moved, still at $unpinnedAfter",
            unpinnedAfter.x > 0.5f
        )
    }

    @Test
    fun unpinned_bubble_resumes_at_rest_without_leftover_velocity() {
        val world = PhysicsWorld(PhysicsConfig(centerAttraction = 0f))
        world.add(Bubble(1L, Vec2(10f, 0f), 1f))
        world.pin(1L, Vec2(10f, 0f))

        repeat(5) {
            world.updatePinned(1L, Vec2(10f + it, 0f))
            world.step(dt)
        }

        world.unpin(1L)
        val bubble = world.bubbles[0]
        assertEquals(bubble.position.x, bubble.prevPosition.x, 0f)
        assertEquals(bubble.position.y, bubble.prevPosition.y, 0f)
    }
}
