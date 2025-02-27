package com.example.onceaday.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

fun scheduleTaskNotification(context: Context, taskName: String, triggerTime: Long) {
    val intent = Intent(context, TaskNotificationReceiver::class.java).apply {
        putExtra("task_name", taskName)
    }
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
}