// TaskTileView.kt
package com.example.onceaday.ui

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import com.example.onceaday.ui.TaskTile

@Composable
fun TaskTileView(
    tasks: MutableList<String>,
    completedTasks: MutableList<String>,
    taskToDelete: String?,
    onLongPress: (String) -> Unit,
    onRemoveTask: (String) -> Unit
) {
    val sortedTasks = tasks.sortedBy { completedTasks.contains(it) }.toList()
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(sortedTasks) { task ->
            TaskTile(
                task,
                completedTasks.contains(task),
                taskToDelete == task,
                onLongPress,
                onRemoveTask,
                onToggleComplete = {
                    if (completedTasks.contains(task)) completedTasks.remove(task) else completedTasks.add(task)
                }
            )
        }
    }
}
