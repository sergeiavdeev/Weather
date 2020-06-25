package com.avdeev.weather.network.data


import java.util.*

data class WeatherResponse (
    val now_dt: Date,
    val fact: Fact
)