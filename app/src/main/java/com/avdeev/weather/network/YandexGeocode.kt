package com.avdeev.weather.network

import com.avdeev.weather.network.data.YandexGeocoderResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YandexGeocode {
    @GET("1.x")
    fun geocode(
        @Query("apikey") token: String,
        @Query("geocode") geocode: String,
        @Query("format") format: String = "json"
    ): Call<YandexGeocoderResponse>
}