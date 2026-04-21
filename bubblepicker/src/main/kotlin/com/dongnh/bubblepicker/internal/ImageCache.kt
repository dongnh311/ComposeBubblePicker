package com.dongnh.bubblepicker.internal

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import coil3.SingletonImageLoader
import coil3.asDrawable
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import com.dongnh.bubblepicker.BubbleItem
import com.dongnh.bubblepicker.BubblePickerState
import kotlinx.coroutines.launch

@Composable
internal fun ImageCacheLoader(state: BubblePickerState) {
    val context = LocalContext.current
    LaunchedEffect(state, state.items.toList()) {
        val toLoad = state.items.filter { item ->
            item.backgroundImageUrl != null && !state.imageCache.containsKey(item.id)
        }
        for (item in toLoad) {
            launch { loadInto(context, state, item) }
        }
    }
}

private suspend fun loadInto(context: Context, state: BubblePickerState, item: BubbleItem) {
    val url = item.backgroundImageUrl ?: return
    val bitmap = runCatching { fetchBitmap(context, url) }.getOrNull() ?: return
    state.imageCache[item.id] = bitmap
    state.wake()
}

private suspend fun fetchBitmap(context: Context, url: String): ImageBitmap? {
    val request = ImageRequest.Builder(context)
        .data(url)
        .allowHardware(false)
        .build()
    val loader = SingletonImageLoader.get(context)
    val result = loader.execute(request)
    if (result !is SuccessResult) return null
    val drawable = result.image.asDrawable(context.resources)
    return (drawable as? BitmapDrawable)?.bitmap?.asImageBitmap()
}
