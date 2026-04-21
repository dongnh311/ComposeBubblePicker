package com.dongnh.bubblepicker.physics

class Bubble(
    val id: Long,
    position: Vec2,
    val radius: Float,
    pinned: Boolean = false,
) {
    var position: Vec2 = position
        internal set

    var prevPosition: Vec2 = position
        internal set

    var pinned: Boolean = pinned
        internal set

    fun velocity(dt: Float): Vec2 =
        if (dt <= 0f) Vec2.ZERO else (position - prevPosition) * (1f / dt)
}
