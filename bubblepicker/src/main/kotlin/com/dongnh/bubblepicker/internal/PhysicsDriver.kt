package com.dongnh.bubblepicker.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import com.dongnh.bubblepicker.BubblePickerState
import kotlinx.coroutines.isActive

@Composable
internal fun PhysicsDriver(state: BubblePickerState) {
    LaunchedEffect(state) {
        while (isActive) {
            var lastFrameNanos = -1L
            while (isActive && (lastFrameNanos == -1L || !state.world.isAtRest())) {
                val now = withFrameNanos { it }
                val dt = if (lastFrameNanos < 0L) {
                    FALLBACK_DT
                } else {
                    (now - lastFrameNanos) / NANOS_PER_SECOND
                }
                state.world.step(dt)
                state.bumpFrameTick()
                lastFrameNanos = now
            }
            if (!isActive) return@LaunchedEffect
            state.wakeChannel.receive()
        }
    }
}

private const val NANOS_PER_SECOND: Float = 1_000_000_000f
private const val FALLBACK_DT: Float = 1f / 60f
