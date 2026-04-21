package com.dongnh.bubblepickerdemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dongnh.bubblepicker.BubblePicker
import com.dongnh.bubblepicker.rememberBubblePickerState

private const val REQUIRED_SELECTIONS = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeDemoScreen(onOpenLegacy: () -> Unit) {
    val state = rememberBubblePickerState(items = remember { SampleData.initialItems() })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose three or more favorites") },
                navigationIcon = {
                    IconButton(onClick = { /* top-level no-op */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    TextButton(onClick = onOpenLegacy) { Text("Legacy") }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                text = "Tap on the brands you like, hold on the ones you don't",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
            BubblePicker(
                state = state,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                onItemLongPress = { state.removeItem(it.id) },
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedButton(onClick = { state.addItems(SampleData.nextRandomBatch(10)) }) {
                    Text("Load More")
                }
                Spacer(Modifier.weight(1f))
                AssistChip(
                    onClick = {},
                    label = { Text("${state.selectedIds.size} / $REQUIRED_SELECTIONS") },
                    colors = AssistChipDefaults.assistChipColors(),
                )
                Button(
                    enabled = state.selectedIds.size >= REQUIRED_SELECTIONS,
                    onClick = {},
                ) {
                    Text("Next")
                }
            }
        }
    }
}
