package com.dongnh.bubblepicker.physics

data class PhysicsConfig(
    val constraintIterations: Int = 3,
    val centerAttraction: Float = 0.005f,
    val drag: Float = 0.88f,
    val restVelocityThreshold: Float = 0.5f,
    val maxStepDt: Float = 1f / 30f,
    val seed: Long = 0L,
)
