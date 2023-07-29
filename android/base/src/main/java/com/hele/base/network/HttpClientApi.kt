package com.hele.base.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object HttpClientApi {

    private const val CONNECT_TIMEOUT = 10_000L
    private const val READ_TIMEOUT = 10_000L
    private const val WRITE_TIMEOUT = 10_000L

    private val okHttpBuilder by lazy {
        OkHttpClient.Builder().connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
    }

    /**
     * change network api to java/kotlin interface api.
     */
    fun <T> buildHttpClient(baseUrl: String, clazz: Class<T>): T {
        return Retrofit.Builder().client(okHttpBuilder.build()).baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create()).build().create(clazz)
    }

}