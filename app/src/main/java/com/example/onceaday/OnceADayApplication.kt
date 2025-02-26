package com.example.onceaday

import android.app.Application
import androidx.work.*
import com.example.onceaday.worker.ResetTasksWorker
import java.util.concurrent.TimeUnit
import java.util.Calendar

class OnceADayApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        scheduleResetTasksWorker()
    }

    private fun scheduleResetTasksWorker() {
        val currentTime = Calendar.getInstance()
        val midnightTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.DAY_OF_MONTH, 1)
        }

        val initialDelay = midnightTime.timeInMillis - currentTime.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<ResetTasksWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ResetTasksWorker",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
}