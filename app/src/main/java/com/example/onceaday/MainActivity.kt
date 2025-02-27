package com.example.onceaday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.onceaday.notifications.NotificationHelper
import com.example.onceaday.ui.OnceADayApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createNotificationChannel(this)
        setContent {
            OnceADayApp(context = this)
        }
    }
}