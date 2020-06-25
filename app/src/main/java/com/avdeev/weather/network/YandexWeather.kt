package com.avdeev.weather.network

import com.avdeev.weather.network.data.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface YandexWeather {
    @GET("v1/informers")
    fun informers(
        @Header("X-Yandex-API-Key") token: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Call<WeatherResponse>
}