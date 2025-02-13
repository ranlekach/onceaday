package com.example.onceaday

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OnceADayApp()
        }
    }
}

@Composable
fun OnceADayApp() {
    var isListView by remember { mutableStateOf(true) }
    val tasks = remember { mutableStateListOf("Task 1", "Task 2", "Task 3") }
    val completedTasks = remember { mutableStateListOf<String>() }
    var showAddTaskBubble by remember { mutableStateOf(false) }
    var newTask by remember { mutableStateOf(TextFieldValue("")) }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Once-A-Day", style = MaterialTheme.typography.h5)
                Switch(checked = isListView, onCheckedChange = { isListView = it })
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (isListView) {
                TaskListView(tasks, completedTasks, onRemoveTask = { tasks.remove(it) })
            } else {
                TaskTileView(tasks, completedTasks, onRemoveTask = { tasks.remove(it) })
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
                        if (newTask.text.isNotBlank()) {
                            tasks.add(newTask.text)
                            newTask = TextFieldValue("")
                            showAddTaskBubble = false
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

@Composable
fun TaskListView(tasks: MutableList<String>, completedTasks: MutableList<String>, onRemoveTask: (String) -> Unit) {
    LazyColumn {
        items(tasks) { task ->
            TaskRow(task, completedTasks.contains(task), onCheckedChange = {
                if (completedTasks.contains(task)) completedTasks.remove(task)
                else completedTasks.add(task)
            }, onRemoveTask = { onRemoveTask(task) })
        }
    }
}

@Composable
fun TaskTileView(tasks: MutableList<String>, completedTasks: MutableList<String>, onRemoveTask: (String) -> Unit) {
    val sortedTasks = tasks.sortedBy { completedTasks.contains(it) }.toList()
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(sortedTasks.size) { index ->
            val task = sortedTasks[index]
            TaskTile(task, completedTasks.contains(task), onCheckedChange = {
                if (completedTasks.contains(task)) completedTasks.remove(task)
                else completedTasks.add(task)
            }, onRemoveTask = { onRemoveTask(task) })
        }
    }
}

@Composable
fun TaskRow(task: String, isCompleted: Boolean, onCheckedChange: () -> Unit, onRemoveTask: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(task, textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None)
        Row {
            Checkbox(checked = isCompleted, onCheckedChange = { onCheckedChange() })
            IconButton(onClick = { onRemoveTask() }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun TaskTile(task: String, isCompleted: Boolean, onCheckedChange: () -> Unit, onRemoveTask: () -> Unit) {
    Card(
        modifier = Modifier.size(150.dp).padding(8.dp),
        backgroundColor = if (isCompleted) Color.Gray else MaterialTheme.colors.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
            Text(task, textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None)
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Checkbox(checked = isCompleted, onCheckedChange = { onCheckedChange() })
                IconButton(onClick = { onRemoveTask() }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
