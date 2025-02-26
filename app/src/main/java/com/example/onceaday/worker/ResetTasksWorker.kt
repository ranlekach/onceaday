package com.example.onceaday.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.onceaday.storage.TaskStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResetTasksWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                TaskStorage.clearDataStore(applicationContext)
                Result.success()
            } catch (e: Exception) {
                Result.failure()
            }
        }
    }
}