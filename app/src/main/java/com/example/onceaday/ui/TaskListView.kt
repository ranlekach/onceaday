// TaskListView.kt
package com.example.onceaday.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable

@Composable
fun TaskListView(
    tasks: MutableList<String>,
    completedTasks: MutableList<String>,
    taskToDelete: String?,
    onLongPress: (String) -> Unit,
    onRemoveTask: (String) -> Unit,
    onToggleComplete: (String) -> Unit
) {
    LazyColumn {
        items(tasks) { task ->
            TaskRow(
                task,
                completedTasks.contains(task),
                taskToDelete == task,
                onLongPress,
                onRemoveTask,
                onToggleComplete
            )
        }
    }
}
