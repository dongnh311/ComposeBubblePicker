package com.dongnh.bubblepicker.physics

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object CirclePacker {

    private const val TWO_PI = (2.0 * Math.PI).toFloat()
    private const val OVERLAP_EPSILON = 1e-4f
    private const val MAX_ANGLE_STEPS = 20_000

    fun pack(items: List<Pair<Long, Float>>): List<Bubble> {
        if (items.isEmpty()) return emptyList()

        val sorted = items.sortedWith(
            compareByDescending<Pair<Long, Float>> { it.second }.thenBy { it.first }
        )

        val placed = ArrayList<Bubble>(sorted.size)
        for ((id, radius) in sorted) {
            val position = findSpiralPosition(placed, radius)
            placed.add(Bubble(id, position, radius))
        }

        center(placed)

        val original = HashMap<Long, Int>(items.size)
        items.forEachIndexed { index, pair -> original[pair.first] = index }
        return placed.sortedBy { original[it.id] ?: Int.MAX_VALUE }
    }

    private fun findSpiralPosition(placed: List<Bubble>, radius: Float): Vec2 {
        if (placed.isEmpty()) return Vec2.ZERO
        if (placed.size == 1) {
            val other = placed[0]
            return Vec2(other.position.x + other.radius + radius, other.position.y)
        }

        val spiralB = radius * 0.5f
        val angleStep = 0.15f

        var theta = 0f
        var steps = 0
        while (steps < MAX_ANGLE_STEPS) {
            val r = spiralB * theta
            val candidate = Vec2(r * cos(theta), r * sin(theta))
            if (!overlapsAny(candidate, radius, placed)) return candidate
            theta += angleStep
            steps++
        }
        val r = spiralB * theta
        return Vec2(r * cos(theta), r * sin(theta))
    }

    private fun overlapsAny(position: Vec2, radius: Float, placed: List<Bubble>): Boolean {
        for (bubble in placed) {
            val dx = position.x - bubble.position.x
            val dy = position.y - bubble.position.y
            val minDist = radius + bubble.radius - OVERLAP_EPSILON
            if (dx * dx + dy * dy < minDist * minDist) return true
        }
        return false
    }

    private fun center(bubbles: MutableList<Bubble>) {
        if (bubbles.isEmpty()) return
        var sumX = 0f
        var sumY = 0f
        for (b in bubbles) {
            sumX += b.position.x
            sumY += b.position.y
        }
        val cx = sumX / bubbles.size
        val cy = sumY / bubbles.size
        if (cx == 0f && cy == 0f) return
        for (b in bubbles) {
            val shifted = Vec2(b.position.x - cx, b.position.y - cy)
            b.position = shifted
            b.prevPosition = shifted
        }
    }

    fun boundingRadius(bubbles: List<Bubble>): Float {
        var max = 0f
        for (b in bubbles) {
            val d = sqrt(b.position.x * b.position.x + b.position.y * b.position.y) + b.radius
            if (d > max) max = d
        }
        return max
    }
}
