package fr.creative.meteo.ui.main

import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel(){
    var cityLon: Double? = null
    var cityLat: Double? = null
    var cityName: String? = null
}