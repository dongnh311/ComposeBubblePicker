package com.dongnh.bubblepickerdemo

import androidx.compose.ui.graphics.Color
import com.dongnh.bubblepicker.BubbleGradient
import com.dongnh.bubblepicker.BubbleGradientOrientation
import com.dongnh.bubblepicker.BubbleItem
import kotlin.random.Random

object SampleData {

    private val BRANDS = listOf(
        "Burberry", "Stussy", "Hilfiger", "Prada", "Gucci",
        "Louis Vuitton", "Raf Simons", "Off-White", "Supreme", "Balenciaga",
        "Versace", "Chanel", "Hermès", "Dior", "Fendi",
        "Armani", "Valentino", "Bottega Veneta", "Celine", "Givenchy",
    )

    private val GRADIENTS = listOf(
        BubbleGradient(Color(0xFFE57373), Color(0xFFBA68C8), BubbleGradientOrientation.VERTICAL),
        BubbleGradient(Color(0xFF4DB6AC), Color(0xFF81C784), BubbleGradientOrientation.DIAGONAL),
        BubbleGradient(Color(0xFFFFB74D), Color(0xFFFF8A65), BubbleGradientOrientation.HORIZONTAL),
        BubbleGradient(Color(0xFF64B5F6), Color(0xFF4DD0E1), BubbleGradientOrientation.VERTICAL),
        BubbleGradient(Color(0xFFAED581), Color(0xFFDCE775), BubbleGradientOrientation.DIAGONAL),
        BubbleGradient(Color(0xFFF06292), Color(0xFFFFB74D), BubbleGradientOrientation.VERTICAL),
    )

    private val SOLIDS = listOf(
        Color(0xFF1E88E5), Color(0xFF43A047), Color(0xFF8E24AA),
        Color(0xFFEF6C00), Color(0xFF00ACC1), Color(0xFF5E35B1),
    )

    private var nextId: Long = BRANDS.size.toLong()
    private val random = Random(2026)

    fun initialItems(): List<BubbleItem> = BRANDS.mapIndexed { index, name ->
        val id = index.toLong()
        val weight = 0.7f + (index % 5) * 0.25f
        val mode = index % 3
        BubbleItem(
            id = id,
            text = name,
            weight = weight,
            gradient = if (mode == 0) GRADIENTS[index % GRADIENTS.size] else null,
            backgroundColor = if (mode == 1) SOLIDS[index % SOLIDS.size] else null,
            backgroundImageUrl = if (index % 5 == 0) "https://picsum.photos/200?random=$id" else null,
            textColor = Color.White,
        )
    }

    fun nextRandomBatch(count: Int): List<BubbleItem> {
        return List(count) {
            val id = nextId++
            BubbleItem(
                id = id,
                text = BRANDS[random.nextInt(BRANDS.size)],
                weight = 0.6f + random.nextFloat() * 1.2f,
                gradient = GRADIENTS[random.nextInt(GRADIENTS.size)],
                textColor = Color.White,
            )
        }
    }
}
