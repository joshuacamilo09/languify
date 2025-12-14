package com.languify.infra.realtime.data.model

sealed class RealtimeState {
    data object Disconnected : RealtimeState()

    data object Connecting : RealtimeState()

    data object Connected : RealtimeState()

    data class Failed(val error: Throwable) : RealtimeState()
}
