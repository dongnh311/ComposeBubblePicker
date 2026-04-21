package com.dongnh.bubblepicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.lerp
import com.dongnh.bubblepicker.internal.ImageCacheLoader
import com.dongnh.bubblepicker.internal.PhysicsDriver
import com.dongnh.bubblepicker.internal.drawBubble
import com.dongnh.bubblepicker.physics.CirclePacker
import com.dongnh.bubblepicker.physics.Vec2

private const val MIN_WEIGHT = 0.5f
private const val MAX_WEIGHT = 2.0f

@Composable
fun BubblePicker(
    state: BubblePickerState,
    modifier: Modifier = Modifier,
    style: BubbleStyle = BubbleStyle(),
    onItemTap: (BubbleItem) -> Unit = { state.toggle(it.id) },
    onItemLongPress: (BubbleItem) -> Unit = {},
) {
    val textMeasurer: TextMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val itemsSnapshot = state.items.toList()
    val itemsById = remember(itemsSnapshot) { itemsSnapshot.associateBy { it.id } }
    val selectedIds = state.selectedIds

    LaunchedEffect(itemsSnapshot, selectedIds, style, density) {
        syncPhysicsWorld(state, style, density)
        state.wake()
    }

    PhysicsDriver(state)
    ImageCacheLoader(state)

    Canvas(
        modifier = modifier
            .pointerInput(state, style) {
                var pinnedId: Long? = null
                detectDragGestures(
                    onDragStart = { offset ->
                        val worldPoint = toWorld(offset, size)
                        val hit = hitTest(state, style, worldPoint) ?: return@detectDragGestures
                        pinnedId = hit.id
                        state.world.pin(hit.id, worldPoint)
                        state.wake()
                    },
                    onDrag = { change, _ ->
                        val id = pinnedId ?: return@detectDragGestures
                        val worldPoint = toWorld(change.position, size)
                        state.world.updatePinned(id, worldPoint)
                        state.wake()
                    },
                    onDragEnd = {
                        pinnedId?.let { state.world.unpin(it) }
                        pinnedId = null
                    },
                    onDragCancel = {
                        pinnedId?.let { state.world.unpin(it) }
                        pinnedId = null
                    },
                )
            }
            .pointerInput(state, style) {
                detectTapGestures(
                    onTap = { offset ->
                        val worldPoint = toWorld(offset, size)
                        hitTest(state, style, worldPoint)?.let(onItemTap)
                    },
                    onLongPress = { offset ->
                        val worldPoint = toWorld(offset, size)
                        hitTest(state, style, worldPoint)?.let(onItemLongPress)
                    },
                )
            },
    ) {
        @Suppress("UNUSED_EXPRESSION")
        state.frameTick.longValue

        translate(left = size.width / 2f, top = size.height / 2f) {
            for (bubble in state.world.bubbles) {
                val item = itemsById[bubble.id] ?: continue
                val isSelected = bubble.id in state.selectedIds
                val image = state.imageCache[bubble.id]
                drawBubble(bubble, item, style, isSelected, textMeasurer, image)
            }
        }
    }
}

private fun syncPhysicsWorld(
    state: BubblePickerState,
    style: BubbleStyle,
    density: Density,
) {
    val items = state.items
    if (items.isEmpty()) {
        state.world.clear()
        return
    }

    val effectiveRadiusById: Map<Long, Float> = items.associate {
        it.id to effectiveRadius(it, style, density, state.selectedIds)
    }

    if (state.world.bubbles.isEmpty()) {
        val packed = CirclePacker.pack(items.map { it.id to effectiveRadiusById.getValue(it.id) })
        packed.forEach { state.world.add(it) }
        return
    }

    val desiredIds: Set<Long> = items.map { it.id }.toSet()
    val presentIds: Set<Long> = state.world.bubbles.map { it.id }.toSet()

    val toRemove = presentIds - desiredIds
    toRemove.forEach { state.world.remove(it) }

    val toAdd = items.filter { it.id !in presentIds }
    toAdd.forEach { item ->
        state.world.addWithAutoPlacement(item.id, effectiveRadiusById.getValue(item.id))
    }

    // Sync radii for every bubble so that the selected scale participates in
    // collision resolution (selected bubbles push neighbors out of the way).
    items.forEach { item ->
        state.world.setRadius(item.id, effectiveRadiusById.getValue(item.id))
    }
}

private fun effectiveRadius(
    item: BubbleItem,
    style: BubbleStyle,
    density: Density,
    selectedIds: Set<Long>,
): Float {
    val base = weightToRadiusPx(item.weight, style, density)
    return if (item.id in selectedIds) base * style.selectedScale else base
}

private fun weightToRadiusPx(weight: Float, style: BubbleStyle, density: Density): Float {
    val clamped = weight.coerceIn(MIN_WEIGHT, MAX_WEIGHT)
    val normalized = (clamped - MIN_WEIGHT) / (MAX_WEIGHT - MIN_WEIGHT)
    val radiusDp = lerp(style.minRadius, style.maxRadius, normalized)
    return with(density) { radiusDp.toPx() }
}

private fun toWorld(canvasPoint: Offset, size: IntSize): Vec2 =
    Vec2(canvasPoint.x - size.width / 2f, canvasPoint.y - size.height / 2f)

private fun hitTest(
    state: BubblePickerState,
    style: BubbleStyle,
    worldPoint: Vec2,
): BubbleItem? {
    val bubbles = state.world.bubbles
    val items = state.items
    for (i in bubbles.indices.reversed()) {
        val bubble = bubbles[i]
        val radius = bubble.radius
        val dx = worldPoint.x - bubble.position.x
        val dy = worldPoint.y - bubble.position.y
        if (dx * dx + dy * dy <= radius * radius) {
            return items.firstOrNull { it.id == bubble.id }
        }
    }
    return null
}
