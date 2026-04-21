package com.dongnh.bubblepicker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.graphics.ImageBitmap
import com.dongnh.bubblepicker.physics.PhysicsConfig
import com.dongnh.bubblepicker.physics.PhysicsWorld
import kotlinx.coroutines.channels.Channel

class BubblePickerState internal constructor(
    initialItems: List<BubbleItem>,
    val config: PhysicsConfig,
) {
    internal val world: PhysicsWorld = PhysicsWorld(config)

    private val _items: SnapshotStateList<BubbleItem> = mutableStateListOf<BubbleItem>().also {
        it.addAll(initialItems)
    }
    val items: List<BubbleItem> get() = _items

    private val _selectedIds = mutableStateOf<Set<Long>>(emptySet())
    val selectedIds: Set<Long> get() = _selectedIds.value

    internal val frameTick: MutableLongState = mutableLongStateOf(0L)

    internal val imageCache: SnapshotStateMap<Long, ImageBitmap> = mutableStateMapOf()

    internal val wakeChannel: Channel<Unit> = Channel(Channel.CONFLATED)

    fun toggle(id: Long) {
        _selectedIds.value = if (id in _selectedIds.value) {
            _selectedIds.value - id
        } else {
            _selectedIds.value + id
        }
    }

    fun select(id: Long) {
        if (id !in _selectedIds.value) _selectedIds.value = _selectedIds.value + id
    }

    fun deselect(id: Long) {
        if (id in _selectedIds.value) _selectedIds.value = _selectedIds.value - id
    }

    fun deselectAll() {
        if (_selectedIds.value.isNotEmpty()) _selectedIds.value = emptySet()
    }

    fun addItem(item: BubbleItem) {
        _items.add(item)
        wake()
    }

    fun addItems(items: List<BubbleItem>) {
        if (items.isEmpty()) return
        _items.addAll(items)
        wake()
    }

    fun removeItem(id: Long) {
        val removed = _items.removeAll { it.id == id }
        if (id in _selectedIds.value) _selectedIds.value = _selectedIds.value - id
        imageCache.remove(id)
        if (removed) wake()
    }

    fun clear() {
        _items.clear()
        _selectedIds.value = emptySet()
        imageCache.clear()
        world.clear()
        wake()
    }

    fun wake() {
        wakeChannel.trySend(Unit)
    }

    internal fun bumpFrameTick() {
        frameTick.longValue += 1
    }
}

@Composable
fun rememberBubblePickerState(
    items: List<BubbleItem>,
    config: PhysicsConfig = PhysicsConfig(),
): BubblePickerState = remember(config) { BubblePickerState(items, config) }
