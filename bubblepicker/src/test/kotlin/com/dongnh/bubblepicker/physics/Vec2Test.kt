package com.dongnh.bubblepicker.physics

import org.junit.Assert.assertEquals
import org.junit.Test

class Vec2Test {

    private val eps = 1e-5f

    @Test
    fun plus_adds_components() {
        val r = Vec2(1f, 2f) + Vec2(3f, 4f)
        assertEquals(4f, r.x, eps)
        assertEquals(6f, r.y, eps)
    }

    @Test
    fun minus_subtracts_components() {
        val r = Vec2(5f, 7f) - Vec2(2f, 3f)
        assertEquals(3f, r.x, eps)
        assertEquals(4f, r.y, eps)
    }

    @Test
    fun times_scales_components() {
        val r = Vec2(2f, -3f) * 2.5f
        assertEquals(5f, r.x, eps)
        assertEquals(-7.5f, r.y, eps)
    }

    @Test
    fun unary_minus_negates() {
        val r = -Vec2(1f, -2f)
        assertEquals(-1f, r.x, eps)
        assertEquals(2f, r.y, eps)
    }

    @Test
    fun length_of_3_4_is_5() {
        assertEquals(5f, Vec2(3f, 4f).length(), eps)
    }

    @Test
    fun length_squared_matches_length_squared() {
        val v = Vec2(3f, 4f)
        assertEquals(v.length() * v.length(), v.lengthSquared(), eps)
    }

    @Test
    fun normalized_of_3_4_has_length_one() {
        val n = Vec2(3f, 4f).normalized()
        assertEquals(1f, n.length(), eps)
    }

    @Test
    fun normalized_of_zero_returns_zero_without_nan() {
        val n = Vec2.ZERO.normalized()
        assertEquals(0f, n.x, eps)
        assertEquals(0f, n.y, eps)
    }
}
