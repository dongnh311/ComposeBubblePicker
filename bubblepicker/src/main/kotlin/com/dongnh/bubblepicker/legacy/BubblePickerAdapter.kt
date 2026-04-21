package com.dongnh.bubblepicker.legacy

@Deprecated(
    message = "Pass a List<BubbleItem> directly to rememberBubblePickerState.",
    replaceWith = ReplaceWith(
        "rememberBubblePickerState",
        "com.dongnh.bubblepicker.rememberBubblePickerState",
    ),
    level = DeprecationLevel.WARNING,
)
interface BubblePickerAdapter {
    val totalCount: Int
    fun getItem(position: Int): PickerItem
}
