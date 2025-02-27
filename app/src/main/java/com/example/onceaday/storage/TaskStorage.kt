package com.example.onceaday.storage

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.onceaday.model.Task

private val Context.dataStore by preferencesDataStore("tasks")

object TaskStorage {
    private val TAG = "TaskStorage"
    private val TASKS_KEY = stringPreferencesKey("tasks")
    private val COMPLETED_TASKS_KEY = stringPreferencesKey("completed_tasks")
    private val gson = Gson()

    fun getTasks(context: Context): Flow<List<Task>> {
        Log.d(TAG, "getTasks called")
        return context.dataStore.data.map { preferences ->
            val tasksJson = preferences[TASKS_KEY] ?: "[]"
            val type = object : TypeToken<List<Task>>() {}.type
            gson.fromJson(tasksJson, type)
        }
    }

    suspend fun saveTasks(context: Context, tasks: List<Task>) {
        Log.d(TAG, "saveTasks called")
        val tasksJson = gson.toJson(tasks)
        context.dataStore.edit { preferences ->
            preferences[TASKS_KEY] = tasksJson
        }
    }

    fun getCompletedTasks(context: Context): Flow<List<Task>> {
        Log.d(TAG, "getCompletedTasks called")
        return context.dataStore.data.map { preferences ->
            val completedTasksJson = preferences[COMPLETED_TASKS_KEY] ?: "[]"
            val type = object : TypeToken<List<Task>>() {}.type
            gson.fromJson(completedTasksJson, type)
        }
    }

    suspend fun saveCompletedTasks(context: Context, completedTasks: List<Task>) {
        Log.d(TAG, "saveCompletedTasks called")
        val completedTasksJson = gson.toJson(completedTasks)
        context.dataStore.edit { preferences ->
            preferences[COMPLETED_TASKS_KEY] = completedTasksJson
        }
    }

    suspend fun resetTaskCompletionStatus(context: Context) {
        Log.d(TAG, "resetTaskCompletionStatus called")
        context.dataStore.edit { preferences ->
            preferences[COMPLETED_TASKS_KEY] = "[]"
        }
    }

    suspend fun clearDataStore(context: Context) {
        Log.d(TAG, "clearDataStore called")
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}