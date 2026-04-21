package com.dongnh.bubblepicker.physics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PhysicsWorldMutationTest {

    private val dt = 1f / 60f

    @Test
    fun add_during_running_appears_in_list_and_does_not_jolt_others() {
        val world = PhysicsWorld()
        world.add(Bubble(1L, Vec2(0f, 0f), 1f))
        repeat(10) { world.step(dt) }
        val existingPosition = world.bubbles[0].position

        world.addWithAutoPlacement(2L, 1f)

        assertEquals(2, world.bubbles.size)
        val existing = world.bubbles.first { it.id == 1L }
        assertEquals(existingPosition.x, existing.position.x, 1e-5f)
        assertEquals(existingPosition.y, existing.position.y, 1e-5f)
    }

    @Test
    fun remove_during_running_removes_bubble_and_step_ignores_it() {
        val world = PhysicsWorld()
        world.add(Bubble(1L, Vec2(0f, 0f), 1f))
        world.add(Bubble(2L, Vec2(3f, 0f), 1f))

        world.remove(1L)
        world.step(dt)

        assertEquals(1, world.bubbles.size)
        assertEquals(2L, world.bubbles[0].id)
        assertNull(world.bubbles.firstOrNull { it.id == 1L })
    }

    @Test
    fun clear_empties_list_and_step_becomes_noop() {
        val world = PhysicsWorld()
        world.add(Bubble(1L, Vec2(1f, 1f), 1f))
        world.add(Bubble(2L, Vec2(-1f, -1f), 1f))

        world.clear()
        assertTrue(world.bubbles.isEmpty())

        world.step(dt)
        assertTrue(world.bubbles.isEmpty())
    }

    @Test
    fun auto_placement_does_not_initially_overlap_existing_bubbles() {
        val world = PhysicsWorld(PhysicsConfig(seed = 123L))
        world.add(Bubble(1L, Vec2(0f, 0f), 1f))
        world.add(Bubble(2L, Vec2(2.1f, 0f), 1f))
        world.add(Bubble(3L, Vec2(-2.1f, 0f), 1f))

        val added = world.addWithAutoPlacement(4L, 1f)

        for (bubble in world.bubbles) {
            if (bubble.id == added.id) continue
            val d = (added.position - bubble.position).length()
            val minD = added.radius + bubble.radius
            assertTrue(
                "new bubble overlaps ${bubble.id}: dist=$d minDist=$minD",
                d >= minD - 1e-3f
            )
        }
    }
}
