package com.dongnh.bubblepicker.physics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt
import kotlin.random.Random

class CirclePackerTest {

    private val eps = 1e-3f

    @Test
    fun single_bubble_placed_at_origin() {
        val bubbles = CirclePacker.pack(listOf(1L to 1f))
        assertEquals(1, bubbles.size)
        assertEquals(0f, bubbles[0].position.x, eps)
        assertEquals(0f, bubbles[0].position.y, eps)
    }

    @Test
    fun two_equal_bubbles_are_two_radii_apart_and_symmetric() {
        val r = 1f
        val bubbles = CirclePacker.pack(listOf(1L to r, 2L to r))
        assertEquals(2, bubbles.size)
        val p1 = bubbles[0].position
        val p2 = bubbles[1].position
        val distance = (p1 - p2).length()
        assertEquals(2f * r, distance, eps)
        assertEquals(-p2.x, p1.x, eps)
        assertEquals(-p2.y, p1.y, eps)
    }

    @Test
    fun twenty_random_bubbles_do_not_overlap() {
        val rng = Random(seed = 42)
        val items = (1L..20L).map { it to (0.3f + rng.nextFloat()) }
        val bubbles = CirclePacker.pack(items)
        for (i in bubbles.indices) {
            for (j in i + 1 until bubbles.size) {
                val a = bubbles[i]
                val b = bubbles[j]
                val d = (a.position - b.position).length()
                val minD = a.radius + b.radius
                assertTrue(
                    "bubble ${a.id} and ${b.id} overlap: dist=$d minDist=$minD",
                    d >= minD - 1e-3f
                )
            }
        }
    }

    @Test
    fun all_centers_fit_within_expected_viewport() {
        val rng = Random(seed = 7)
        val items = (1L..20L).map { it to (0.3f + rng.nextFloat()) }
        val bubbles = CirclePacker.pack(items)
        val maxRadius = items.maxOf { it.second }
        val viewport = CirclePacker.boundingRadius(bubbles)
        val limit = viewport + maxRadius * 1.5f
        for (b in bubbles) {
            val dist = sqrt(b.position.x * b.position.x + b.position.y * b.position.y)
            assertTrue("bubble ${b.id} outside viewport: $dist > $limit", dist <= limit)
        }
    }

    @Test
    fun packing_is_deterministic_for_same_input() {
        val items = listOf(1L to 0.5f, 2L to 0.7f, 3L to 1f, 4L to 0.4f, 5L to 0.9f)
        val first = CirclePacker.pack(items)
        val second = CirclePacker.pack(items)
        assertEquals(first.size, second.size)
        for (i in first.indices) {
            assertEquals(first[i].id, second[i].id)
            assertEquals(first[i].position.x, second[i].position.x, 0f)
            assertEquals(first[i].position.y, second[i].position.y, 0f)
        }
    }
}
