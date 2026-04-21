package com.dongnh.bubblepicker

import androidx.compose.ui.graphics.Color

data class BubbleItem(
    val id: Long,
    val text: String,
    val weight: Float = 1f,
    val backgroundColor: Color? = null,
    val gradient: BubbleGradient? = null,
    val backgroundImageUrl: String? = null,
    val textColor: Color? = null,
)
