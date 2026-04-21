package com.dongnh.bubblepicker.legacy

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import com.dongnh.bubblepicker.BubbleGradient

/**
 * Legacy data holder for the XML-based BubblePickerView.
 *
 * In v1.0.0 many fields are accepted but NOT rendered because the new
 * Composable API does not yet support them: [icon], [iconOnTop],
 * [overlayAlpha], [typeface], [textSize], [showImageOnUnSelected],
 * [isViewBorderSelected], [colorBorderSelected], [strokeWidthBorder],
 * and [customData]. They are kept only so existing source code keeps
 * compiling while users migrate.
 */
@Deprecated(
    message = "Use com.dongnh.bubblepicker.BubbleItem with the Composable BubblePicker.",
    replaceWith = ReplaceWith(
        "BubbleItem",
        "com.dongnh.bubblepicker.BubbleItem",
    ),
    level = DeprecationLevel.WARNING,
)
data class PickerItem @JvmOverloads constructor(
    var title: String? = null,
    var icon: Drawable? = null,
    var iconOnTop: Boolean = true,
    @ColorInt var color: Int? = null,
    var gradient: BubbleGradient? = null,
    var overlayAlpha: Float = 0.4f,
    var typeface: Typeface = Typeface.DEFAULT,
    @ColorInt var textColor: Int? = null,
    var textSize: Float = 40f,
    var imgDrawable: Drawable? = null,
    var showImageOnUnSelected: Boolean = false,
    var isSelected: Boolean = false,
    var isViewBorderSelected: Boolean = false,
    @ColorInt var colorBorderSelected: Int? = null,
    var strokeWidthBorder: Float = 10f,
    var customData: Any? = null,
)
