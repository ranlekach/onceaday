// TaskRow.kt
package com.example.onceaday.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.onceaday.model.Task

@Composable
fun TaskRow(
    task: Task,
    isCompleted: Boolean,
    showDelete: Boolean,
    onLongPress: (Task) -> Unit,
    onRemoveTask: (Task) -> Unit,
    onToggleComplete: (Task) -> Unit,
    onCancelDeleteMode: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onCancelDeleteMode() },
                    onLongPress = { onLongPress(task) }
                )
            },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.Start) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggleComplete(task) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(task.description, textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None)
        }
        if (showDelete) {
            IconButton(onClick = { onRemoveTask(task) }) {
                Icon(Icons.Filled.Close, contentDescription = "Delete")
            }
        }
        if (task.hasTimer) {
            Icon(
                imageVector = Icons.Filled.AccessTime,
                contentDescription = "Timer Set",
                modifier = Modifier.size(24.dp).padding(start = 8.dp)
            )
        }
    }
}