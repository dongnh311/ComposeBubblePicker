package com.dongnh.bubblepicker.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.dongnh.bubblepicker.BubbleGradient
import com.dongnh.bubblepicker.BubbleGradientOrientation
import com.dongnh.bubblepicker.BubbleItem
import com.dongnh.bubblepicker.BubbleStyle
import com.dongnh.bubblepicker.physics.Bubble
import kotlin.math.roundToInt

internal fun DrawScope.drawBubble(
    bubble: Bubble,
    item: BubbleItem,
    style: BubbleStyle,
    isSelected: Boolean,
    textMeasurer: TextMeasurer,
    imageBitmap: ImageBitmap?,
) {
    // Bubble.radius is pre-scaled by the selection sync in BubblePicker, so the
    // selected visual size matches the physics collision radius.
    val radius = bubble.radius
    val center = Offset(bubble.position.x, bubble.position.y)

    drawFill(item, style, isSelected, center, radius)

    if (!isSelected && imageBitmap != null) {
        drawImageLayer(imageBitmap, center, radius, style.imageOpacity)
    }

    if (!isSelected) {
        drawCircle(
            color = style.defaultStroke,
            radius = radius,
            center = center,
            style = Stroke(width = style.strokeWidth.toPx()),
        )
    }

    drawLabel(item, style, isSelected, center, radius, textMeasurer)
}

private fun DrawScope.drawFill(
    item: BubbleItem,
    style: BubbleStyle,
    isSelected: Boolean,
    center: Offset,
    radius: Float,
) {
    when {
        isSelected -> drawCircle(style.selectedFill, radius, center)
        item.gradient != null -> drawCircle(gradientBrush(item.gradient, center, radius), radius, center)
        item.backgroundColor != null -> drawCircle(item.backgroundColor, radius, center)
        else -> drawCircle(style.defaultFill, radius, center)
    }
}

private fun DrawScope.drawImageLayer(
    imageBitmap: ImageBitmap,
    center: Offset,
    radius: Float,
    alpha: Float,
) {
    val circle = Path().apply { addOval(Rect(center, radius)) }
    clipPath(circle) {
        val diameter = (radius * 2f).roundToInt().coerceAtLeast(1)
        drawImage(
            image = imageBitmap,
            srcOffset = IntOffset.Zero,
            srcSize = IntSize(imageBitmap.width, imageBitmap.height),
            dstOffset = IntOffset(
                (center.x - radius).roundToInt(),
                (center.y - radius).roundToInt(),
            ),
            dstSize = IntSize(diameter, diameter),
            alpha = alpha,
        )
    }
}

private fun DrawScope.drawLabel(
    item: BubbleItem,
    style: BubbleStyle,
    isSelected: Boolean,
    center: Offset,
    radius: Float,
    textMeasurer: TextMeasurer,
) {
    val textColor = when {
        isSelected -> style.selectedTextColor
        item.textColor != null -> item.textColor
        else -> style.defaultTextColor
    }
    val textStyle = TextStyle(
        color = textColor,
        fontSize = style.fontSize,
        fontWeight = style.fontWeight,
        textAlign = TextAlign.Center,
    )
    val maxWidthPx = (radius * 1.6f).roundToInt().coerceAtLeast(1)
    val layout = textMeasurer.measure(
        text = item.text,
        style = textStyle,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        constraints = Constraints(maxWidth = maxWidthPx),
    )
    val topLeft = Offset(
        center.x - layout.size.width / 2f,
        center.y - layout.size.height / 2f,
    )
    drawText(textLayoutResult = layout, topLeft = topLeft)
}

private fun gradientBrush(gradient: BubbleGradient, center: Offset, radius: Float): Brush {
    val start: Offset
    val end: Offset
    when (gradient.orientation) {
        BubbleGradientOrientation.VERTICAL -> {
            start = Offset(center.x, center.y - radius)
            end = Offset(center.x, center.y + radius)
        }
        BubbleGradientOrientation.HORIZONTAL -> {
            start = Offset(center.x - radius, center.y)
            end = Offset(center.x + radius, center.y)
        }
        BubbleGradientOrientation.DIAGONAL -> {
            start = Offset(center.x - radius, center.y - radius)
            end = Offset(center.x + radius, center.y + radius)
        }
    }
    return Brush.linearGradient(
        colors = listOf(gradient.startColor, gradient.endColor),
        start = start,
        end = end,
    )
}
