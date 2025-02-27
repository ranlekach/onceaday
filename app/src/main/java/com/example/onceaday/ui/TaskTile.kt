// TaskTile.kt
package com.example.onceaday.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.onceaday.model.Task

@Composable
fun TaskTile(
    task: Task,
    isCompleted: Boolean,
    showDelete: Boolean,
    onLongPress: (Task) -> Unit,
    onRemoveTask: (Task) -> Unit,
    onToggleComplete: (Task) -> Unit,
    onCancelDeleteMode: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(150.dp) // Explicitly specify the type as Dp
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { if (showDelete) onCancelDeleteMode() else onToggleComplete(task) },
                    onLongPress = { onLongPress(task) }
                )
            },
        backgroundColor = if (isCompleted) Color.Gray else MaterialTheme.colors.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.align(Alignment.Center).padding(16.dp)) {
                Text(task.description, textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None)
            }
            if (showDelete) {
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                    IconButton(
                        onClick = { onRemoveTask(task) },
                        modifier = Modifier.size(24.dp).background(Color.White, shape = CircleShape) // Explicitly specify the type as Dp
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Delete", tint = Color.Black)
                    }
                }
            }
        }
    }
}