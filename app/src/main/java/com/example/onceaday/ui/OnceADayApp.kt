// OnceADayApp.kt
package com.example.onceaday.ui

import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

import com.example.onceaday.storage.TaskStorage
import com.example.onceaday.ui.TaskListView
import com.example.onceaday.ui.TaskTileView
import kotlinx.coroutines.launch


@Composable
fun OnceADayApp(context: Context) {
    var isListView by remember { mutableStateOf(true) }
    val tasks = remember { mutableStateListOf<String>() }
    val completedTasks = remember { mutableStateListOf<String>() }
    var showAddTaskBubble by remember { mutableStateOf(false) }
    var newTask by remember { mutableStateOf("") }
    var taskToDelete by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Load tasks and completed tasks from DataStore when app starts
    LaunchedEffect(Unit) {
        TaskStorage.getTasks(context).collect { savedTasks ->
            tasks.clear()
            tasks.addAll(savedTasks)
        }
        TaskStorage.getCompletedTasks(context).collect { savedCompletedTasks ->
            completedTasks.clear()
            completedTasks.addAll(savedCompletedTasks)
        }
    }

    // Function to toggle task completion
    fun toggleTaskCompletion(task: String) {
        coroutineScope.launch {
            if (completedTasks.contains(task)) {
                completedTasks.remove(task)
            } else {
                completedTasks.add(task)
            }
            TaskStorage.saveCompletedTasks(context, completedTasks.toList())

            // Sort tasks so completed ones move to the bottom
            tasks.sortWith(compareBy({ completedTasks.contains(it) }, { it }))
        }
    }
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
                TaskListView(tasks, completedTasks, taskToDelete, { taskToDelete = it }, {
                    tasks.remove(it)
                    taskToDelete = null
                    coroutineScope.launch { TaskStorage.saveTasks(context, tasks) }
                }, ::toggleTaskCompletion)
            } else {
                TaskTileView(tasks, completedTasks, taskToDelete, { taskToDelete = it }, {
                    tasks.remove(it)
                    taskToDelete = null
                    coroutineScope.launch { TaskStorage.saveTasks(context, tasks) }
                }, ::toggleTaskCompletion)
            }
        }
        if (showAddTaskBubble) {
            Card(
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = 8.dp
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = newTask,
                        onValueChange = { newTask = it },
                        placeholder = { Text("Enter task") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (newTask.isNotBlank()) {
                            tasks.add(newTask)
                            newTask = ""
                            showAddTaskBubble = false
                            coroutineScope.launch { TaskStorage.saveTasks(context, tasks) }
                        }
                    }) {
                        Text("Add")
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = { showAddTaskBubble = !showAddTaskBubble },
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Task")
        }
    }
}
