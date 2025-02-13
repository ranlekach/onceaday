package com.example.onceaday

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clip

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
fun TaskListView(tasks: MutableList<String>, completedTasks: MutableList<String>, taskToDelete: String?, onLongPress: (String) -> Unit, onRemoveTask: (String) -> Unit) {
    LazyColumn {
        items(tasks) { task ->
            TaskRow(task, completedTasks.contains(task), taskToDelete == task, onLongPress, onRemoveTask, onToggleComplete = {
                if (completedTasks.contains(task)) completedTasks.remove(task) else completedTasks.add(task)
            })
        }
    }
}

@Composable
fun TaskTileView(tasks: MutableList<String>, completedTasks: MutableList<String>, taskToDelete: String?, onLongPress: (String) -> Unit, onRemoveTask: (String) -> Unit) {
    val sortedTasks = tasks.sortedBy { completedTasks.contains(it) }.toList()
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(sortedTasks.size) { index ->
            val task = sortedTasks[index]
            TaskTile(task, completedTasks.contains(task), taskToDelete == task, onLongPress, onRemoveTask)
        }
    }
}

@Composable
fun TaskRow(task: String, isCompleted: Boolean, showDelete: Boolean, onLongPress: (String) -> Unit, onRemoveTask: (String) -> Unit, onToggleComplete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp).pointerInput(Unit) {
            detectTapGestures(onLongPress = { onLongPress(task) })
        },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { onToggleComplete() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(task, textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None)
        }
        if (showDelete) {
            IconButton(onClick = { onRemoveTask(task) }) {
                Icon(Icons.Filled.Close, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun TaskTile(task: String, isCompleted: Boolean, showDelete: Boolean, onLongPress: (String) -> Unit, onRemoveTask: (String) -> Unit) {
    Card(
        modifier = Modifier.size(150.dp).padding(8.dp).pointerInput(Unit) {
            detectTapGestures(onLongPress = { onLongPress(task) })
        },
        backgroundColor = if (isCompleted) Color.Gray else MaterialTheme.colors.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.align(Alignment.Center).padding(16.dp)) {
                Text(task, textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None)
            }
            if (showDelete) {
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                    IconButton(
                        onClick = { onRemoveTask(task) },
                        modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.White)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Delete", tint = Color.Black)
                    }
                }
            }
        }
    }
}
