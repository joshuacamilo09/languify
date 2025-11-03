package com.languify.ui.viewmodel

data class Translation(
    val original: String,
    val translated: String,
    val timestamp: Long = System.currentTimeMillis()
)
