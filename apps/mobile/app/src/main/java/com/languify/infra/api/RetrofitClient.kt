package com.languify.infra.api

import com.languify.identity.auth.data.AuthApi
import com.languify.infra.health.data.HealthApi
import com.languify.infra.security.TokenStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthInterceptor(private val tokenStorage: TokenStorage) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
    val token = runBlocking { tokenStorage.token.first() }
    val req = chain.request().newBuilder()

    if (token != null) {
      req.addHeader("Authorization", "Bearer $token")
    }

    return chain.proceed(req.build())
  }
}

object RetrofitClient {
  private const val BASE_URL = "http://10.0.2.2:8080"

  private fun buildClient(tokenStorage: TokenStorage? = null): OkHttpClient {
    val loggingInterceptor =
      HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    val builder =
      OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)

    if (tokenStorage != null) {
      builder.addInterceptor(AuthInterceptor(tokenStorage))
    }

    return builder.build()
  }

  fun createAuthApi(tokenStorage: TokenStorage): AuthApi {
    return Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(buildClient(tokenStorage))
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(AuthApi::class.java)
  }

  fun createHealthApi(): HealthApi {
    return Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(buildClient())
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(HealthApi::class.java)
  }
}
