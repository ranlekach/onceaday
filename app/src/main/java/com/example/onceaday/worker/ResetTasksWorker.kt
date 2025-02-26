package com.example.onceaday.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.onceaday.storage.TaskStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

class ResetTasksWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("ResetTasksWorker", "Worker is running")
                TaskStorage.resetTaskCompletionStatus(applicationContext)
                Log.d("ResetTasksWorker", "Task completion status reset")
                Result.success()
            } catch (e: Exception) {
                Log.e("ResetTasksWorker", "Worker failed", e)
                Result.failure()
            }
        }
    }
}