package com.avdeev.weather.network.data

data class Fact(
    val temp: Int?,
    val feels_like: Int?,
    val condition: String?
)