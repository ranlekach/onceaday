// TaskTileView.kt
package com.example.onceaday.ui

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import com.example.onceaday.model.Task

@Composable
fun TaskTileView(
    tasks: MutableList<Task>,
    completedTasks: MutableList<String>,
    taskToDelete: String?,
    onLongPress: (Task) -> Unit,
    onRemoveTask: (Task) -> Unit,
    onToggleComplete: (Task) -> Unit,
    onCancelDeleteMode: () -> Unit
) {
    val sortedTasks = tasks.sortedBy { completedTasks.contains(it.description) }.toList()
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(sortedTasks, key = { it.description }) { task ->
            TaskTile(
                task,
                completedTasks.contains(task.description),
                taskToDelete == task.description,
                onLongPress,
                onRemoveTask,
                onToggleComplete,
                onCancelDeleteMode
            )
        }
    }
}