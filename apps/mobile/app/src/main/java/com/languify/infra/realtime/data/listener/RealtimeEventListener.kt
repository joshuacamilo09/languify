package com.languify.infra.realtime.data.listener

import com.languify.infra.realtime.data.model.RealtimeEvent
import com.languify.infra.realtime.data.model.RealtimeState

interface RealtimeEventListener {
    fun onStateChanged(state: RealtimeState) {}

    fun onEvent(event: RealtimeEvent) {}

    fun onRawMessage(message: String) {}
}
