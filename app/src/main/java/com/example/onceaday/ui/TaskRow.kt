// TaskRow.kt
package com.example.onceaday.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun TaskRow(
    task: String,
    isCompleted: Boolean,
    showDelete: Boolean,
    onLongPress: (String) -> Unit,
    onRemoveTask: (String) -> Unit,
    onToggleComplete: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggleComplete(task) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(task, textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None)
        }
        if (showDelete) {
            IconButton(onClick = { onRemoveTask(task) }) {
                Icon(Icons.Filled.Close, contentDescription = "Delete")
            }
        }
    }
}
