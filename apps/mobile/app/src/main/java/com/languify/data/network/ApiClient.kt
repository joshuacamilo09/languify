package com.languify.data.network

import com.google.firebase.appdistribution.gradle.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient
{
    private const val BASE_URL = "http://172.20.10.3:8080"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val ApiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}