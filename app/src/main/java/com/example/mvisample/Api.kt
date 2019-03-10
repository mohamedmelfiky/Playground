package com.example.mvisample

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "8f45f21ebe8ea824b764f080afe29a6a"

interface Api {

    @GET("movie/now_playing?api_key=$API_KEY")
    suspend fun getNowPlaying(
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): ApiResult

}

fun getApiService(): Api {

    val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    return retrofit.create(Api::class.java)
}