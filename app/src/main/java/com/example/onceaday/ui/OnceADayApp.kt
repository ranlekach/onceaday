package com.example.onceaday.ui

import android.content.Context
import android.util.Log
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
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.livedata.observeAsState
import kotlinx.coroutines.flow.first
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.onceaday.worker.ResetTasksWorker
import androidx.work.ExistingWorkPolicy
import com.example.onceaday.notifications.NotificationHelper
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.ui.res.vectorResource
import java.util.Calendar
import android.app.TimePickerDialog
import androidx.compose.ui.platform.LocalContext
import com.example.onceaday.model.Task
import  com.example.onceaday.notifications.scheduleTaskNotification

@Composable
fun OnceADayApp(context: Context) {
    val TAG = "OnceADayApp"
    var isListView by remember { mutableStateOf(true) }
    val tasks = remember { mutableStateListOf<Task>() }
    val completedTasks = remember { mutableStateListOf<Task>() }
    var showAddTaskBubble by remember { mutableStateOf(false) }
    var newTask by remember { mutableStateOf("") }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var showWarning by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    // Load tasks and completed tasks from DataStore when app starts
    LaunchedEffect(Unit) {
        Log.d(TAG, "LaunchedEffect started")
        coroutineScope.launch {
            val savedTasks = TaskStorage.getTasks(context).first()
            val savedCompletedTasks = TaskStorage.getCompletedTasks(context).first()
            Log.d(TAG, "Tasks loaded: $savedTasks")
            Log.d(TAG, "Completed tasks loaded: $savedCompletedTasks")
            tasks.clear()
            tasks.addAll(savedTasks)
            completedTasks.clear()
            completedTasks.addAll(savedCompletedTasks)

            // Sort tasks so completed ones move to the bottom
            tasks.sortWith(compareBy({ completedTasks.contains(it) }, { it.description }))
        }
    }

    // Observe WorkManager for worker completion
    val workManager = WorkManager.getInstance(context)
    val workInfo by workManager.getWorkInfosForUniqueWorkLiveData("resetTasksWorker").observeAsState()

    LaunchedEffect(workInfo) {
        workInfo?.let { infoList ->
            val workInfo = infoList.firstOrNull()
            if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                Log.d(TAG, "Worker completed successfully")
                coroutineScope.launch {
                    val savedCompletedTasks = TaskStorage.getCompletedTasks(context).first()
                    completedTasks.clear()
                    completedTasks.addAll(savedCompletedTasks)

                    // Sort tasks so completed ones move to the bottom
                    tasks.sortWith(compareBy({ completedTasks.contains(it) }, { it.description }))
                }
            }
        }
    }

    // Function to toggle task completion
    fun toggleTaskCompletion(task: Task) {
        coroutineScope.launch {
            Log.d(TAG, "toggleTaskCompletion called with task: ${task.description}")
            if (completedTasks.contains(task)) {
                completedTasks.remove(task)
                NotificationHelper.showNotification(
                    context,
                    "Task Incomplete",
                    "Task '${task.description}' marked as incomplete",
                    task.description.hashCode()
                )
            } else {
                completedTasks.add(task)
                NotificationHelper.showNotification(
                    context,
                    "Task Complete",
                    "Task '${task.description}' marked as complete",
                    task.description.hashCode()
                )
            }
            Log.d(TAG, "calling saveCompletedTasks with task: $completedTasks")
            TaskStorage.saveCompletedTasks(context, completedTasks.toList())

            // Sort tasks so completed ones move to the bottom
            tasks.sortWith(compareBy({ completedTasks.contains(it) }, { it.description }))
        }
    }
    var isInDeletionMode by remember { mutableStateOf(false) }

    // Function to handle long press
    fun handleLongPress(task: Task) {
        Log.d("OnceADayApp", "handleLongPress called with task: ${task.description}")
        isInDeletionMode = true
    }

    // Function to cancel delete mode
    fun cancelDeleteMode() {
        Log.d(TAG, "cancelDeleteMode called")
        isInDeletionMode = false
    }

    var selectedTime by remember { mutableStateOf<Calendar?>(null) }
    val timePickerDialog = TimePickerDialog(
        LocalContext.current,
        { _, hourOfDay, minute ->
            selectedTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
            }
        },
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        Calendar.getInstance().get(Calendar.MINUTE),
        true
    )

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = newTask,
                        onValueChange = {
                            newTask = it
                            showWarning = false
                        },
                        placeholder = { Text("Enter task") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { timePickerDialog.show() }) {
                        Icon(Icons.Filled.AccessTime, contentDescription = "Select Time")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newTask.isNotBlank()) {
                                if (tasks.any { it.description == newTask }) {
                                    showWarning = true
                                } else {
                                    val task = Task(
                                        description = newTask,
                                        hasTimer = selectedTime != null,
                                        timer = selectedTime?.timeInMillis
                                    )
                                    tasks.add(task)
                                    newTask = ""
                                    selectedTime = null
                                    coroutineScope.launch { bottomSheetState.hide() }
                                    coroutineScope.launch { TaskStorage.saveTasks(context, tasks.toList()) }
                                    NotificationHelper.showNotification(
                                        context,
                                        "New Task Added",
                                        "Task '${task.description}' added",
                                        task.description.hashCode()
                                    )
                                    // Schedule the notification if the task has a timer
                                    if (task.hasTimer && task.timer != null) {
                                        scheduleTaskNotification(context, task.description, task.timer)
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF03DAC6))
                    ) {
                        Text("Add")
                    }
                }
                if (showWarning) {
                    Text("Task already exists", color = Color.Red)
                }
                selectedTime?.let {
                    Text("Notification Time: ${it.get(Calendar.HOUR_OF_DAY)}:${it.get(Calendar.MINUTE)}")
                }
            }
        }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { cancelDeleteMode() })
            }
            .padding(16.dp)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Once-A-Day", style = MaterialTheme.typography.h5)
                    Switch(checked = isListView, onCheckedChange = { isListView = it })
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (isListView) {
                    TaskListView(tasks, completedTasks, isInDeletionMode, ::handleLongPress, { task: Task ->
                        tasks.remove(task)
                        coroutineScope.launch { TaskStorage.saveTasks(context, tasks) }
                    }, ::toggleTaskCompletion, ::cancelDeleteMode)
                } else {
                    TaskTileView(tasks, completedTasks, isInDeletionMode, ::handleLongPress, { task: Task ->
                        tasks.remove(task)
                        coroutineScope.launch  { TaskStorage.saveTasks(context, tasks) }
                    }, ::toggleTaskCompletion, ::cancelDeleteMode)
                }
                Button(onClick = { triggerResetTasksWorker(context) }) {
                    Text(text = "Trigger Worker")
                }
            }
            FloatingActionButton(
                onClick = { coroutineScope.launch { bottomSheetState.show() } },
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
            }
        }
    }
}
private fun triggerResetTasksWorker(context: Context) {
    val workRequest = OneTimeWorkRequestBuilder<ResetTasksWorker>().build()
    WorkManager.getInstance(context).enqueueUniqueWork(
        "resetTasksWorker",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}
/*@Composable
fun OnceADayApp(context: Context) {
    val TAG = "OnceADayApp"
    var isListView by remember { mutableStateOf(true) }
    val tasks = remember { mutableStateListOf<Task>() }
    val completedTasks = remember { mutableStateListOf<String>() }
    var showAddTaskBubble by remember { mutableStateOf(false) }
    var newTask by remember { mutableStateOf("") }
    var taskToDelete by remember { mutableStateOf<String?>(null) }
    var showWarning by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    // Load tasks and completed tasks from DataStore when app starts
    LaunchedEffect(Unit) {
        Log.d(TAG, "LaunchedEffect started")
        coroutineScope.launch {
            val savedTasks = TaskStorage.getTasks(context).first()
            val savedCompletedTasks = TaskStorage.getCompletedTasks(context).first()
            Log.d(TAG, "Tasks loaded: $savedTasks")
            Log.d(TAG, "Completed tasks loaded: $savedCompletedTasks")
            tasks.clear()
            tasks.addAll(savedTasks)
            completedTasks.clear()
            completedTasks.addAll(savedCompletedTasks)

            // Sort tasks so completed ones move to the bottom
            tasks.sortWith(compareBy({ completedTasks.contains(it.description) }, { it.description }))
        }
    }
    // Observe WorkManager for worker completion
    val workManager = WorkManager.getInstance(context)
    val workInfo by workManager.getWorkInfosForUniqueWorkLiveData("resetTasksWorker").observeAsState()

    LaunchedEffect(workInfo) {
        workInfo?.let { infoList ->
            val workInfo = infoList.firstOrNull()
            if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                Log.d(TAG, "Worker completed successfully")
                coroutineScope.launch {
                    val savedCompletedTasks = TaskStorage.getCompletedTasks(context).first()
                    completedTasks.clear()
                    completedTasks.addAll(savedCompletedTasks)

                    // Sort tasks so completed ones move to the bottom
                    tasks.sortWith(compareBy({ completedTasks.contains(it.description) }, { it.description }))
                }
            }
        }
    }

    // Function to toggle task completion
    fun toggleTaskCompletion(task: Task) {
        coroutineScope.launch {
            Log.d(TAG, "toggleTaskCompletion called with task: ${task.description}")
            if (completedTasks.contains(task.description)) {
                completedTasks.remove(task.description)
                NotificationHelper.showNotification(
                    context,
                    "Task Incomplete",
                    "Task '${task.description}' marked as incomplete",
                    task.description.hashCode()
                )
            } else {
                completedTasks.add(task.description)
                NotificationHelper.showNotification(
                    context,
                    "Task Complete",
                    "Task '${task.description}' marked as complete",
                    task.description.hashCode()
                )
            }
            Log.d(TAG, "calling saveCompletedTasks with task: $completedTasks")
            TaskStorage.saveCompletedTasks(context, completedTasks.toList())

            // Sort tasks so completed ones move to the bottom
            tasks.sortWith(compareBy({ completedTasks.contains(it.description) }, { it.description }))
        }
    }

    // Function to handle long press
    fun handleLongPress(task: Task) {
        Log.d("OnceADayApp", "handleLongPress called with task: ${task.description}")
        taskToDelete = task.description
    }

    // Function to cancel delete mode
    fun cancelDeleteMode() {
        Log.d(TAG, "cancelDeleteMode called")
        taskToDelete = null
    }

    var selectedTime by remember { mutableStateOf<Calendar?>(null) }
    val timePickerDialog = TimePickerDialog(
        LocalContext.current,
        { _, hourOfDay, minute ->
            selectedTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
            }
        },
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        Calendar.getInstance().get(Calendar.MINUTE),
        true
    )

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = newTask,
                        onValueChange = {
                            newTask = it
                            showWarning = false
                        },
                        placeholder = { Text("Enter task") },
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { timePickerDialog.show() }) {
                        Icon(Icons.Filled.AccessTime, contentDescription = "Select Time")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newTask.isNotBlank()) {
                                if (tasks.any { it.description == newTask }) {
                                    showWarning = true
                                } else {
                                    val task = Task(
                                        description = newTask,
                                        hasTimer = selectedTime != null,
                                        timer = selectedTime?.timeInMillis
                                    )
                                    tasks.add(task)
                                    newTask = ""
                                    selectedTime = null
                                    coroutineScope.launch { bottomSheetState.hide() }
                                    coroutineScope.launch { TaskStorage.saveTasks(context, tasks.map { it.description }) }
                                    NotificationHelper.showNotification(
                                        context,
                                        "New Task Added",
                                        "Task '${task.description}' added",
                                        task.description.hashCode()
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF03DAC6))
                    ) {
                        Text("Add")
                    }
                }
                if (showWarning) {
                    Text("Task already exists", color = Color.Red)
                }
                selectedTime?.let {
                    Text("Notification Time: ${it.get(Calendar.HOUR_OF_DAY)}:${it.get(Calendar.MINUTE)}")
                }
            }
        }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { cancelDeleteMode() })
            }
            .padding(16.dp)) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Once-A-Day", style = MaterialTheme.typography.h5)

                    Switch(checked = isListView, onCheckedChange = { isListView = it })
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (isListView) {
                    TaskListView(tasks, completedTasks, taskToDelete, ::handleLongPress, { task: Task ->
                        tasks.remove(task)
                        taskToDelete = null
                        coroutineScope.launch { TaskStorage.saveTasks(context, tasks.map { it.description }) }
                    }, ::toggleTaskCompletion, ::cancelDeleteMode)
                } else {
                    TaskTileView(tasks, completedTasks, taskToDelete, ::handleLongPress, { task: Task ->
                        tasks.remove(task)
                        taskToDelete = null
                        coroutineScope.launch { TaskStorage.saveTasks(context, tasks.map { it.description }) }
                    }, ::toggleTaskCompletion, ::cancelDeleteMode)
                }
                Button(onClick = { triggerResetTasksWorker(context) }) {
                    Text(text = "Trigger Worker")
                }
            }
            FloatingActionButton(
                onClick = { coroutineScope.launch { bottomSheetState.show() } },
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Task")
            }
        }
    }
}
private fun triggerResetTasksWorker(context: Context) {
    val workRequest = OneTimeWorkRequestBuilder<ResetTasksWorker>().build()
    WorkManager.getInstance(context).enqueueUniqueWork(
        "resetTasksWorker",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}*/