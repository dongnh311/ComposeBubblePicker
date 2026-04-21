package com.dongnh.bubblepicker

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BubbleStyle(
    val minRadius: Dp = 40.dp,
    val maxRadius: Dp = 80.dp,
    val defaultFill: Color = Color.White,
    val defaultStroke: Color = Color.Black,
    val defaultTextColor: Color = Color.Black,
    val selectedFill: Color = Color.Black,
    val selectedTextColor: Color = Color.White,
    val selectedScale: Float = 1.15f,
    val strokeWidth: Dp = 1.dp,
    val fontSize: TextUnit = 14.sp,
    val fontWeight: FontWeight = FontWeight.Medium,
    val imageOpacity: Float = 0.85f,
)
