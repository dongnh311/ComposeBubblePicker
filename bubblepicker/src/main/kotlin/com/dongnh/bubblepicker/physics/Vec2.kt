package com.dongnh.bubblepicker.physics

import kotlin.math.sqrt

data class Vec2(val x: Float, val y: Float) {

    operator fun plus(other: Vec2): Vec2 = Vec2(x + other.x, y + other.y)

    operator fun minus(other: Vec2): Vec2 = Vec2(x - other.x, y - other.y)

    operator fun times(scalar: Float): Vec2 = Vec2(x * scalar, y * scalar)

    operator fun unaryMinus(): Vec2 = Vec2(-x, -y)

    fun lengthSquared(): Float = x * x + y * y

    fun length(): Float = sqrt(lengthSquared())

    fun normalized(): Vec2 {
        val len = length()
        return if (len < EPSILON) ZERO else Vec2(x / len, y / len)
    }

    companion object {
        val ZERO = Vec2(0f, 0f)
        const val EPSILON = 1e-6f
    }
}
