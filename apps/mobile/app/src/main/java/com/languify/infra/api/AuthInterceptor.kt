package com.languify.infra.api

import com.languify.infra.storage.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenStorage: TokenStorage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        val token = tokenStorage.getToken() ?: return chain.proceed(req)

        val authenticated = req.newBuilder().header("Authorization", "Bearer $token").build()
        return chain.proceed(authenticated)
    }
}
