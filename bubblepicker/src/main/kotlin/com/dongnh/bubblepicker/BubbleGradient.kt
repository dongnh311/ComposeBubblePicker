package com.dongnh.bubblepicker

import androidx.compose.ui.graphics.Color

enum class BubbleGradientOrientation { VERTICAL, HORIZONTAL, DIAGONAL }

data class BubbleGradient(
    val startColor: Color,
    val endColor: Color,
    val orientation: BubbleGradientOrientation = BubbleGradientOrientation.VERTICAL,
)
