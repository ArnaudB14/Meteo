package fr.creative.meteo.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class Preference(context: Context) {

    companion object {
        const val PREF_CITY = "city"
    }

    private var pref: SharedPreferences

    init {
        pref = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun setCity(city: String?) = pref.edit().putString(PREF_CITY, city).apply()

    fun getCity(): String? = pref.getString(PREF_CITY, null)

}