package com.example.data.remote.api

import com.example.data.entity.ApiResult
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "8f45f21ebe8ea824b764f080afe29a6a"

interface Api {

    @GET("movie/popular?api_key=$API_KEY")
    suspend fun getPopular(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int
    ): ApiResult

    @GET("movie/top_rated?api_key=$API_KEY")
    suspend fun getTopRated(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int
    ): ApiResult

    @GET("movie/now_playing?api_key=$API_KEY")
    suspend fun getNowPlaying(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int
    ): ApiResult

    @GET("movie/upcoming?api_key=$API_KEY")
    suspend fun getUpcoming(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int
    ): ApiResult

}

fun createNetworkClient(baseUrl: String, debug: Boolean = false) =
    retrofitClient(baseUrl, httpClient(debug))

private fun httpClient(debug: Boolean): OkHttpClient {
    val httpLoggingInterceptor = HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
    val clientBuilder = OkHttpClient.Builder()
    if (debug) {
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(httpLoggingInterceptor)
    }
    return clientBuilder.build()
}

private fun retrofitClient(baseUrl: String, httpClient: OkHttpClient): Retrofit =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()