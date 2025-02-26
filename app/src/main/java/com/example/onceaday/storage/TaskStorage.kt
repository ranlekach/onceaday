package com.example.onceaday.storage

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("tasks")

object TaskStorage {
    private val TAG = "TaskStorage"
    private val TASKS_KEY = stringPreferencesKey("tasks")
    private val COMPLETED_TASKS_KEY = stringPreferencesKey("completed_tasks")

    fun getTasks(context: Context): Flow<List<String>> {
        Log.d(TAG, "getTasks called")
        return context.dataStore.data.map { preferences ->
            preferences[TASKS_KEY]?.split(",") ?: emptyList()
        }
    }

    suspend fun saveTasks(context: Context, tasks: List<String>) {
        Log.d(TAG, "saveTasks called")
        context.dataStore.edit { preferences ->
            preferences[TASKS_KEY] = tasks.joinToString(",")
        }
    }

    fun getCompletedTasks(context: Context): Flow<List<String>> {
        Log.d(TAG, "getCompletedTasks called")
        return context.dataStore.data.map { preferences ->
            preferences[COMPLETED_TASKS_KEY]?.split(",") ?: emptyList()
        }
    }

    suspend fun saveCompletedTasks(context: Context, completedTasks: List<String>) {
        Log.d(TAG, "saveCompletedTasks called")
        context.dataStore.edit { preferences ->
            preferences[COMPLETED_TASKS_KEY] = completedTasks.joinToString(",")
        }
    }

    suspend fun resetTaskCompletionStatus(context: Context) {
        Log.d(TAG, "resetTaskCompletionStatus called")
        context.dataStore.edit { preferences ->
            preferences[COMPLETED_TASKS_KEY] = ""
        }
    }
    suspend fun clearDataStore(context: Context) {
        Log.d(TAG, "clearDataStore called")
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}