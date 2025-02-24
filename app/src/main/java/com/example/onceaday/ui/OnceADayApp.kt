// OnceADayApp.kt
package com.example.onceaday.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.onceaday.ui.TaskListView
import com.example.onceaday.ui.TaskTileView

@Composable
fun OnceADayApp() {
    var isListView by remember { mutableStateOf(true) }
    val tasks = remember { mutableStateListOf("Task 1", "Task 2", "Task 3") }
    val completedTasks = remember { mutableStateListOf<String>() }
    var showAddTaskBubble by remember { mutableStateOf(false) }
    var newTask by remember { mutableStateOf("") }
    var taskToDelete by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
        detectTapGestures(onTap = { taskToDelete = null })
    }.padding(16.dp)) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Once-A-Day", style = MaterialTheme.typography.h5)
                Switch(checked = isListView, onCheckedChange = { isListView = it })
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (isListView) {
                TaskListView(tasks, completedTasks, taskToDelete, { taskToDelete = it }, { tasks.remove(it); taskToDelete = null })
            } else {
                TaskTileView(tasks, completedTasks, taskToDelete, { taskToDelete = it }, { tasks.remove(it); taskToDelete = null })
            }
        }
    }
}
