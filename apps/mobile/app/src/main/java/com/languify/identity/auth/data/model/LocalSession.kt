package com.languify.identity.auth.data.model

import androidx.compose.runtime.compositionLocalOf

val LocalSession = compositionLocalOf<Session> { error("No LocalSession Provided") }
