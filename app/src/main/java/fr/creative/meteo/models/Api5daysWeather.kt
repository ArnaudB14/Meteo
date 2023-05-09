data class Api5daysWeather(
    val cod: Int,
    val message: String?,
    val city: City?,
    val list: Array<List>?
) {
    data class City(
        val name: String
    )
    data class List(
        val dt: Long,
        val main: Main,
        val weather: Array<Weather>,
        val wind: Wind,
        val dt_txt: String
    ) {
        data class Main(
            val temp: Double,
            val temp_min: Double,
            val temp_max: Double,
            val pressure: Double,
            val sea_level: Double,
            val grnd_level: Double,
            val humidity: Int,
            val temp_kf: Double
        )

        data class Weather(
            val id: Int,
            val main: String,
            val description: String,
            val icon: String
        )

        data class Wind(
            val speed: Double,
            val deg: Int
        )
    }
}
