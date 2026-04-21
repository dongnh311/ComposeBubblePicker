@file:Suppress("DEPRECATION")

package com.dongnh.bubblepickerdemo

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.dongnh.bubblepicker.BubbleGradient
import com.dongnh.bubblepicker.BubbleGradientOrientation
import com.dongnh.bubblepicker.legacy.BubblePickerAdapter
import com.dongnh.bubblepicker.legacy.BubblePickerListener
import com.dongnh.bubblepicker.legacy.BubblePickerView
import com.dongnh.bubblepicker.legacy.PickerItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegacyDemoScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val adapter = remember { buildCountriesAdapter(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Legacy XML API") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        AndroidView(
            modifier = Modifier.padding(padding).fillMaxSize(),
            factory = { ctx ->
                BubblePickerView(ctx).apply {
                    this.adapter = adapter
                    listener = object : BubblePickerListener {
                        override fun onBubbleSelected(item: PickerItem) {
                            Toast.makeText(ctx, "${item.title} selected", Toast.LENGTH_SHORT).show()
                        }

                        override fun onBubbleDeselected(item: PickerItem) {
                            Toast.makeText(ctx, "${item.title} deselected", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
        )
    }
}

private fun buildCountriesAdapter(context: Context): BubblePickerAdapter {
    val titles = context.resources.getStringArray(R.array.countries)
    val colors = context.resources.obtainTypedArray(R.array.colors)
    val images = context.resources.obtainTypedArray(R.array.images)

    val items = List(titles.size) { position ->
        val startColor = colors.getColor((position * 2) % colors.length(), 0)
        val endColor = colors.getColor((position * 2) % colors.length() + 1, 0)
        PickerItem().apply {
            title = titles[position]
            gradient = BubbleGradient(
                startColor = Color(startColor),
                endColor = Color(endColor),
                orientation = BubbleGradientOrientation.VERTICAL,
            )
            textColor = android.graphics.Color.WHITE
            imgDrawable = ContextCompat.getDrawable(context, images.getResourceId(position, 0))
        }
    }

    colors.recycle()
    images.recycle()

    return object : BubblePickerAdapter {
        override val totalCount: Int = items.size
        override fun getItem(position: Int): PickerItem = items[position]
    }
}
