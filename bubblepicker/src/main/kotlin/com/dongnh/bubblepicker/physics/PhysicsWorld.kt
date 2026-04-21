package com.dongnh.bubblepicker.physics

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class PhysicsWorld(val config: PhysicsConfig = PhysicsConfig()) {

    private val _bubbles: MutableList<Bubble> = ArrayList()
    private val bubblesById: HashMap<Long, Bubble> = HashMap()
    val bubbles: List<Bubble> get() = _bubbles

    private val random = Random(config.seed)

    fun add(bubble: Bubble) {
        _bubbles.add(bubble)
        bubblesById[bubble.id] = bubble
    }

    fun addWithAutoPlacement(id: Long, radius: Float): Bubble {
        if (_bubbles.isEmpty()) {
            return addAt(id, Vec2.ZERO, radius)
        }

        val clusterRadius = CirclePacker.boundingRadius(_bubbles)
        val gap = radius * 0.1f
        val placementRadius = clusterRadius + radius + gap

        var theta = random.nextFloat() * TWO_PI
        repeat(12) {
            val candidate = Vec2(cos(theta) * placementRadius, sin(theta) * placementRadius)
            if (!overlapsAny(candidate, radius)) {
                return addAt(id, candidate, radius)
            }
            theta += ANGLE_RETRY_STEP
        }

        val fallback = Vec2(cos(theta) * placementRadius, sin(theta) * placementRadius)
        return addAt(id, fallback, radius)
    }

    fun remove(id: Long) {
        val bubble = bubblesById.remove(id) ?: return
        _bubbles.remove(bubble)
    }

    fun clear() {
        _bubbles.clear()
        bubblesById.clear()
    }

    fun pin(id: Long, position: Vec2) {
        val bubble = bubblesById[id] ?: return
        bubble.pinned = true
        bubble.position = position
        bubble.prevPosition = position
    }

    fun updatePinned(id: Long, position: Vec2) {
        val bubble = bubblesById[id] ?: return
        if (!bubble.pinned) return
        bubble.position = position
        bubble.prevPosition = position
    }

    fun unpin(id: Long) {
        val bubble = bubblesById[id] ?: return
        bubble.pinned = false
        bubble.prevPosition = bubble.position
    }

    fun setRadius(id: Long, radius: Float) {
        bubblesById[id]?.radius = radius
    }

    fun isAtRest(): Boolean {
        val threshold = config.restVelocityThreshold * config.restVelocityThreshold
        for (b in _bubbles) {
            if (b.pinned) continue
            val dx = b.position.x - b.prevPosition.x
            val dy = b.position.y - b.prevPosition.y
            if (dx * dx + dy * dy > threshold) return false
        }
        return true
    }

    fun step(dt: Float) {
        if (_bubbles.isEmpty()) return
        val clampedDt = if (dt > config.maxStepDt) config.maxStepDt else dt
        if (clampedDt <= 0f) return

        integrate()
        applyCenterAttraction()
        repeat(config.constraintIterations) {
            resolveCollisions()
        }
    }

    private fun addAt(id: Long, position: Vec2, radius: Float): Bubble {
        val bubble = Bubble(id, position, radius)
        _bubbles.add(bubble)
        bubblesById[id] = bubble
        return bubble
    }

    private fun integrate() {
        val drag = config.drag
        for (b in _bubbles) {
            if (b.pinned) continue
            val vx = (b.position.x - b.prevPosition.x) * drag
            val vy = (b.position.y - b.prevPosition.y) * drag
            b.prevPosition = b.position
            b.position = Vec2(b.position.x + vx, b.position.y + vy)
        }
    }

    private fun applyCenterAttraction() {
        val pull = config.centerAttraction
        if (pull == 0f) return
        for (b in _bubbles) {
            if (b.pinned) continue
            b.position = Vec2(b.position.x * (1f - pull), b.position.y * (1f - pull))
        }
    }

    private fun resolveCollisions() {
        val n = _bubbles.size
        for (i in 0 until n) {
            val a = _bubbles[i]
            for (j in i + 1 until n) {
                val b = _bubbles[j]
                resolvePair(a, b)
            }
        }
    }

    private fun resolvePair(a: Bubble, b: Bubble) {
        if (a.pinned && b.pinned) return

        val dx = b.position.x - a.position.x
        val dy = b.position.y - a.position.y
        val minDist = a.radius + b.radius
        val distSq = dx * dx + dy * dy
        if (distSq >= minDist * minDist) return

        val dist = sqrt(distSq)
        val dirX: Float
        val dirY: Float
        if (dist < Vec2.EPSILON) {
            dirX = 1f
            dirY = 0f
        } else {
            dirX = dx / dist
            dirY = dy / dist
        }
        val overlap = minDist - dist

        when {
            a.pinned -> b.position = Vec2(b.position.x + dirX * overlap, b.position.y + dirY * overlap)
            b.pinned -> a.position = Vec2(a.position.x - dirX * overlap, a.position.y - dirY * overlap)
            else -> {
                val half = overlap * 0.5f
                a.position = Vec2(a.position.x - dirX * half, a.position.y - dirY * half)
                b.position = Vec2(b.position.x + dirX * half, b.position.y + dirY * half)
            }
        }
    }

    private fun overlapsAny(position: Vec2, radius: Float): Boolean {
        for (bubble in _bubbles) {
            val dx = position.x - bubble.position.x
            val dy = position.y - bubble.position.y
            val minDist = radius + bubble.radius
            if (dx * dx + dy * dy < minDist * minDist) return true
        }
        return false
    }

    companion object {
        private const val TWO_PI = (2.0 * Math.PI).toFloat()
        private const val ANGLE_RETRY_STEP = (Math.PI / 6.0).toFloat()
    }
}
