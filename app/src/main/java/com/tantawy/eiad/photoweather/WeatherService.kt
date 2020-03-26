package com.tantawy.eiad.photoweather

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather")
    fun getWeather(
        @Query("APPID") key: String?,
        @Query("units") units: String?,
        @Query("lat") lat: Double,
        @Query("lon") long: Double
    ): Call<WResponse>
}