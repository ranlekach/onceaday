// TaskTileView.kt
package com.example.onceaday.ui

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable

@Composable
fun TaskTileView(
    tasks: MutableList<String>,
    completedTasks: MutableList<String>,
    taskToDelete: String?,
    onLongPress: (String) -> Unit,
    onRemoveTask: (String) -> Unit,
    onToggleComplete: (String) -> Unit
) {
    val sortedTasks = tasks.sortedBy { completedTasks.contains(it) }.toList()
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(sortedTasks, key = { it }) { task ->
            TaskTile(
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