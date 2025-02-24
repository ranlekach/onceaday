package com.example.onceaday.storage

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("tasks")

object TaskStorage {
    private val TASKS_KEY = stringSetPreferencesKey("tasks")
    private val COMPLETED_TASKS_KEY = stringSetPreferencesKey("completed_tasks")

    suspend fun saveTasks(context: Context, tasks: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[TASKS_KEY] = tasks.toSet()
        }
    }

    fun getTasks(context: Context): Flow<List<String>> {
        return context.dataStore.data.map { preferences ->
            preferences[TASKS_KEY]?.toList() ?: emptyList()
        }
    }

    suspend fun saveCompletedTasks(context: Context, completedTasks: List<String>) {
        context.dataStore.edit { preferences ->
            preferences[COMPLETED_TASKS_KEY] = completedTasks.toSet()
        }
    }

    fun getCompletedTasks(context: Context): Flow<List<String>> {
        return context.dataStore.data.map { preferences ->
            preferences[COMPLETED_TASKS_KEY]?.toList() ?: emptyList()
        }
    }
}
