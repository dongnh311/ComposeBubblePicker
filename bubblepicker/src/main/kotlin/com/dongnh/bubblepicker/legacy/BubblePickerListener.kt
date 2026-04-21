package com.dongnh.bubblepicker.legacy

@Deprecated(
    message = "Handle selection with onItemTap / state.toggle on the Composable API.",
    replaceWith = ReplaceWith(
        "BubblePicker",
        "com.dongnh.bubblepicker.BubblePicker",
    ),
    level = DeprecationLevel.WARNING,
)
interface BubblePickerListener {
    fun onBubbleSelected(item: PickerItem)
    fun onBubbleDeselected(item: PickerItem)
}
