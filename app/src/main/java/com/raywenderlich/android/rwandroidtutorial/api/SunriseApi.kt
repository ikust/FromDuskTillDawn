package com.raywenderlich.android.rwandroidtutorial.api

import com.raywenderlich.android.rwandroidtutorial.data.SunriseResponse
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SunriseApi {

  @GET("json?date=today")
  fun getSunriseAndSunset(
      @Query("lat") latitude: Double,
      @Query("lng") longitude: Double
  ): Call<SunriseResponse>

  companion object {
    private const val BASE_URL = "https://api.sunrise-sunset.org/"

    fun create(): SunriseApi = create(HttpUrl.parse(BASE_URL)!!)

    fun create(httpUrl: HttpUrl): SunriseApi {
      val client = OkHttpClient.Builder()
          .build()
      return Retrofit.Builder()
          .baseUrl(httpUrl)
          .client(client)
          .addConverterFactory(GsonConverterFactory.create())
          .build()
          .create(SunriseApi::class.java);
    }
  }
}