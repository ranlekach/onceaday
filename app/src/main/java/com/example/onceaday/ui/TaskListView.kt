// TaskListView.kt
package com.example.onceaday.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.onceaday.model.Task

@Composable
fun TaskListView(
    tasks: MutableList<Task>,
    completedTasks: MutableList<Task>,
    isInDeletionMode: Boolean,
    onLongPress: (Task) -> Unit,
    onRemoveTask: (Task) -> Unit,
    onToggleComplete: (Task) -> Unit,
    onCancelDeleteMode: () -> Unit
) {
    LazyColumn {
        items(tasks) { task ->
            TaskRow(
                task,
                completedTasks.contains(task),
                isInDeletionMode,
                onLongPress,
                onRemoveTask,
                onToggleComplete,
                onCancelDeleteMode
            )
        }
    }
}