package com.dongnh.bubblepicker.physics

data class PhysicsConfig(
    val constraintIterations: Int = 6,
    val centerAttraction: Float = 0.015f,
    val drag: Float = 0.92f,
    val restVelocityThreshold: Float = 0.05f,
    val maxStepDt: Float = 1f / 30f,
    val seed: Long = 0L,
)
