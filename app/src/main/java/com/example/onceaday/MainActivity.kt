package com.example.onceaday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Once-A-Day", style = MaterialTheme.typography.h5)
            Switch(checked = isListView, onCheckedChange = { isListView = it })
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isListView) {
            TaskListView(tasks, completedTasks)
        } else {
            TaskTileView(tasks, completedTasks)
        }
    }
}

@Composable
fun TaskListView(tasks: MutableList<String>, completedTasks: MutableList<String>) {
    LazyColumn {
        items(tasks) { task ->
            TaskRow(task, completedTasks.contains(task)) {
                if (completedTasks.contains(task)) completedTasks.remove(task)
                else completedTasks.add(task)
            }
        }
    }
}

@Composable
fun TaskTileView(tasks: MutableList<String>, completedTasks: MutableList<String>) {
    Column {
        tasks.sortedBy { completedTasks.contains(it) }.forEach { task ->
            TaskTile(task, completedTasks.contains(task)) {
                if (completedTasks.contains(task)) completedTasks.remove(task)
                else completedTasks.add(task)
            }
        }
    }
}

@Composable
fun TaskRow(task: String, isCompleted: Boolean, onCheckedChange: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(task, textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None)
        Checkbox(checked = isCompleted, onCheckedChange = { onCheckedChange() })
    }
}

@Composable
fun TaskTile(task: String, isCompleted: Boolean, onCheckedChange: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        backgroundColor = if (isCompleted) Color.Gray else MaterialTheme.colors.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(task, textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None)
            Checkbox(checked = isCompleted, onCheckedChange = { onCheckedChange() })
        }
    }
}
