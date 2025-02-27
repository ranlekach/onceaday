package com.example.onceaday.model

data class Task(
    val description: String,
    val hasTimer: Boolean = false,
    val timer: Long? = null // Timer in milliseconds
)