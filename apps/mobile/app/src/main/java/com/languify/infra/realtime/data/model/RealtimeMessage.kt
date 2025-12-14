package com.languify.infra.realtime.data.model

import com.google.gson.JsonElement

data class RealtimeMessage(
    val type: String,
    val data: JsonElement? = null,
)
