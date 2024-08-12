package com.example.ourmenu.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObject {
    private const val BASE_URL = "https://bluesparrow.shop/"
    const val TOKEN =
        "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MjM0NzQ2NjYsImV4cCI6MTcyMzU2MTA2NiwidXNlcklkIjoyM30.xKSDbLhZMeR6BN3y0lz41QBlxURqV3pEGuaPwxDpupk" // 하드코딩된 토큰 나중에 변경해야함

    private val loggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    private val authInterceptor =
        okhttp3.Interceptor { chain ->
            val newRequest =
                chain
                    .request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $TOKEN")
                    .build()
            chain.proceed(newRequest)
        }

    fun getAuthInterceptor(): Interceptor = authInterceptor

    private val client: OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()

    val retrofit: Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .client(NetworkModule.provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}
