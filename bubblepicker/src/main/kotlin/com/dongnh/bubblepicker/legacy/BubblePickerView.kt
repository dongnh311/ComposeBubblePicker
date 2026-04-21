@file:Suppress("DEPRECATION")

package com.dongnh.bubblepicker.legacy

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.unit.dp
import com.dongnh.bubblepicker.BubbleItem
import com.dongnh.bubblepicker.BubblePicker
import com.dongnh.bubblepicker.BubblePickerState
import com.dongnh.bubblepicker.BubbleStyle
import com.dongnh.bubblepicker.R
import com.dongnh.bubblepicker.physics.PhysicsConfig
import android.graphics.Canvas as AndroidCanvas

/**
 * AbstractComposeView wrapping the Composable BubblePicker for legacy XML users.
 *
 * Legacy [Drawable] backgrounds are converted to [ImageBitmap] in-process and
 * injected directly into the state's image cache — no Coil round-trip is needed.
 * The drawables are cleared on [onDetachedFromWindow] to avoid leaking bitmaps
 * when the View is reused.
 */
@Deprecated(
    message = "Use the BubblePicker Composable with rememberBubblePickerState instead.",
    replaceWith = ReplaceWith(
        "BubblePicker",
        "com.dongnh.bubblepicker.BubblePicker",
    ),
    level = DeprecationLevel.WARNING,
)
class BubblePickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AbstractComposeView(context, attrs, defStyleAttr) {

    private var style by mutableStateOf(BubbleStyle())
    private var state by mutableStateOf<BubblePickerState?>(null)
    private var legacyById: Map<Long, PickerItem> = emptyMap()

    var adapter: BubblePickerAdapter? = null
        set(value) {
            field = value
            rebuild(value)
        }

    var listener: BubblePickerListener? = null

    /**
     * Not honored in v1.0.0. Accepted for source compatibility.
     */
    var isAlwaysSelected: Boolean = false

    init {
        if (attrs != null) applyStyledAttributes(attrs)
    }

    private fun applyStyledAttributes(attrs: AttributeSet) {
        val typed = context.obtainStyledAttributes(attrs, R.styleable.BubblePickerView)
        try {
            val density = context.resources.displayMetrics.density
            val bgArgb = typed.getColor(R.styleable.BubblePickerView_backgroundColor, NO_COLOR)
            val strokeArgb = typed.getColor(R.styleable.BubblePickerView_strokeColor, NO_COLOR)
            val strokePx = typed.getDimension(R.styleable.BubblePickerView_strokeWidth, -1f)
            val minPx = typed.getDimension(R.styleable.BubblePickerView_minRadius, -1f)
            val maxPx = typed.getDimension(R.styleable.BubblePickerView_maxRadius, -1f)
            style = style.copy(
                defaultFill = if (bgArgb != NO_COLOR) Color(bgArgb) else style.defaultFill,
                defaultStroke = if (strokeArgb != NO_COLOR) Color(strokeArgb) else style.defaultStroke,
                strokeWidth = if (strokePx > 0f) (strokePx / density).dp else style.strokeWidth,
                minRadius = if (minPx > 0f) (minPx / density).dp else style.minRadius,
                maxRadius = if (maxPx > 0f) (maxPx / density).dp else style.maxRadius,
            )
        } finally {
            typed.recycle()
        }
    }

    private fun rebuild(source: BubblePickerAdapter?) {
        if (source == null || source.totalCount == 0) {
            state?.imageCache?.clear()
            state = null
            legacyById = emptyMap()
            return
        }
        val items = ArrayList<BubbleItem>(source.totalCount)
        val legacies = HashMap<Long, PickerItem>(source.totalCount)
        val drawables = HashMap<Long, ImageBitmap>()
        val preselected = ArrayList<Long>()
        for (index in 0 until source.totalCount) {
            val item = source.getItem(index)
            val id = index.toLong()
            items += item.toBubbleItem(id)
            legacies[id] = item
            item.imgDrawable?.toImageBitmap()?.let { drawables[id] = it }
            if (item.isSelected) preselected += id
        }
        val next = BubblePickerState(initialItems = items, config = PhysicsConfig())
        drawables.forEach { (id, bitmap) -> next.imageCache[id] = bitmap }
        preselected.forEach(next::select)
        state = next
        legacyById = legacies
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        state?.imageCache?.clear()
    }

    @Composable
    override fun Content() {
        val current = state ?: return
        BubblePicker(
            state = current,
            style = style,
            onItemTap = { bubbleItem ->
                val wasSelected = bubbleItem.id in current.selectedIds
                current.toggle(bubbleItem.id)
                val legacyItem = legacyById[bubbleItem.id] ?: return@BubblePicker
                if (wasSelected) {
                    listener?.onBubbleDeselected(legacyItem)
                } else {
                    listener?.onBubbleSelected(legacyItem)
                }
            },
        )
    }

    companion object {
        private const val NO_COLOR = Int.MIN_VALUE
    }
}

private fun PickerItem.toBubbleItem(id: Long): BubbleItem = BubbleItem(
    id = id,
    text = title.orEmpty(),
    backgroundColor = color?.let { Color(it) },
    gradient = gradient,
    textColor = textColor?.let { Color(it) },
)

private fun Drawable.toImageBitmap(): ImageBitmap {
    val width = intrinsicWidth.takeIf { it > 0 } ?: DEFAULT_DRAWABLE_SIZE
    val height = intrinsicHeight.takeIf { it > 0 } ?: DEFAULT_DRAWABLE_SIZE
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = AndroidCanvas(bitmap)
    setBounds(0, 0, width, height)
    draw(canvas)
    return bitmap.asImageBitmap()
}

private const val DEFAULT_DRAWABLE_SIZE = 256
