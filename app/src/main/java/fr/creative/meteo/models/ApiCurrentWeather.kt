package fr.creative.meteo.models

import com.google.gson.annotations.SerializedName

data class ApiCurrentWeather(
    val cod: Int,
    val name: String?,
    val message: String?,

    @SerializedName("main")
    val temperature: Main?,

    @SerializedName("weather")
    val weathers: ArrayList<Weather>?,

    val sys: Sys?,

    val coord: Coord?
) {
    data class Main(
        val temp: Float,
        val feels_like: Float
    )
    data class Weather(
        val icon: String,
        val description: String
    )
    data class Coord(
        val lon: Double,
        val lat: Double
    )
    data class Sys(
        val country: String
    )
}
