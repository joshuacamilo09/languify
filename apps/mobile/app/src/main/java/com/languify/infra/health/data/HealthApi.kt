package com.languify.infra.health.data

import retrofit2.http.GET

data class HealthResponse(val status: String)

interface HealthApi {
  @GET("/health") suspend fun health(): HealthResponse
}
