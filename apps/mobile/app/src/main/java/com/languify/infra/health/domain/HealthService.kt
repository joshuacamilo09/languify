package com.languify.infra.health.domain

import com.languify.infra.health.data.HealthApi

class HealthService(private val api: HealthApi) {
  suspend fun checkHealth(): Boolean {
    return try {
      api.health()
      true
    } catch (e: Exception) {
      false
    }
  }
}