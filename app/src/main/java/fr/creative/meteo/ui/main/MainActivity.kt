package fr.creative.meteo.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.gson.Gson
import fr.creative.meteo.AppActivity
import fr.creative.meteo.R
import fr.creative.meteo.models.ApiCurrentWeather
import fr.creative.meteo.ui.maps.MapsActivity
import fr.creative.meteo.utils.FastDialog
import fr.creative.meteo.utils.Network
import fr.creative.meteo.utils.Preference
import java.util.*

class MainActivity : AppActivity() {

    companion object {
        const val URL/*: String*/ =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=fr&appid=ae3b18f5fd3d61182c7bb64054b419c2"
        const val URL_IMAGE = "https://openweathermap.org/img/wn/%s@2x.png"
    }

    private var editTextCity: EditText? = null
    private lateinit var buttonSubmit: Button // Initialisation tardive
    private val textViewCity: TextView by lazy { findViewById(R.id.textViewCity) }
    private val textViewTemperature: TextView by lazy { findViewById(R.id.textViewTemperature) }
    private val imageViewIcon: ImageView by lazy { findViewById(R.id.imageViewIcon) }
    private val textViewWeather: TextView by lazy { findViewById(R.id.textViewWeather) }
    private val textViewRessenti: TextView by lazy { findViewById(R.id.textViewRessenti) }
    private val buttonMap: Button by lazy { findViewById(R.id.buttonMap) }

    private var cityLon: Double? = null
    private var cityLat: Double? = null
    private var cityName: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextCity = findViewById(R.id.editTextCity)
        buttonSubmit = findViewById(R.id.buttonSubmit)

        buttonSubmit.setOnClickListener { myButton ->
            // 1) vérification de la saisie d'une ville / affichage d'une erreur si nécessaire
            if (editTextCity?.text.toString().isEmpty()) {
                // affichage dialog
                FastDialog.showDialog(
                    this@MainActivity,
                    FastDialog.SIMPLE_DIALOG,
                    getString(R.string.main_error_city_empty)
                )
                return@setOnClickListener
            }

            // 2) vérification de la connexion / affichage d'une erreur si nécessaire (FastDialog)
            if (!Network.isNetworkAvailable(this@MainActivity)) {
                // affichage dialog
                FastDialog.showDialog(
                    this@MainActivity,
                    FastDialog.SIMPLE_DIALOG,
                    getString(R.string.main_error_network)
                )
                return@setOnClickListener
            }
            // 3) requête HTTP avec VOLLEY

            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(this)
            val url = String.format(URL, editTextCity?.text.toString())

            // Request a string response from the provided URL.
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { json -> // json = String
                    // Gson
                    val api = Gson().fromJson(json, ApiCurrentWeather::class.java)

                    if(api.cod == 200) {
                        buttonMap.visibility = View.VISIBLE
                        Preference(this@MainActivity).setCity(api?.name)
                        //linearLayoutTransparent.setBackgroundResource(R.drawable.background_main_item)
                        // afficher les données
                        textViewCity.text = api.name + ", " + api.sys?.country;

                        var roundedTemp = api.temperature?.let { String.format("%.1f", it.temp) }
                        textViewTemperature.text = roundedTemp.toString() + " °C";

                        textViewWeather.text =
                            api.weathers?.get(0)?.description?.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.ROOT
                                ) else it.toString()
                            };

                        var urlImage = String.format(URL_IMAGE, api.weathers?.firstOrNull()?.icon)
                        Glide.with(this@MainActivity).load(urlImage).into(imageViewIcon)

                        var roundedTempRessenti = api.temperature?.let { String.format("%.1f", it.feels_like) }
                        textViewRessenti.text = "Ressentie : " + roundedTempRessenti.toString() + " °C";

                        api.coord?.lon?.let { cityLon = it }
                        api.coord?.lat?.let { cityLat = it }
                        api.name.let { cityName = it }
                    }
                },
                {
                    buttonMap.visibility = View.GONE
                    Preference(this@MainActivity).setCity(null)
                    val toast = Toast.makeText(this@MainActivity, "Cette ville n'existe pas", Toast.LENGTH_SHORT)
                    toast.show()
                })

            // Add the request to the RequestQueue.
            queue.add(stringRequest)

            // 4) lecture du JSON et récupération d'un objet avec GSON
            // 5) affichage des informations sur le layout
        }
        buttonMap.setOnClickListener {
            val intentMap = Intent(this@MainActivity, MapsActivity::class.java)
            intentMap.putExtra("longitude", cityLon)
            intentMap.putExtra("latitude", cityLat)
            intentMap.putExtra("cityName", cityName)
            Log.d("MapsActivity", "cityLat = " + cityLat)
            startActivity(intentMap)
        }
    }

    override fun onStop() {
        super.onStop()
        // Preference(this@MainActivity).setCity(editTextCity?.text.toString())
    }

    override fun onResume() {
        super.onResume()
        editTextCity?.setText(Preference(this@MainActivity).getCity())


        // REFAIT LA REQUETE EN RELANCANT L'APPLI


        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = String.format(URL, editTextCity?.text.toString())

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { json -> // json = String
                Log.e("JSON", json)

                // Gson
                val api = Gson().fromJson(json, ApiCurrentWeather::class.java)

                if(api.cod == 200) {
                    buttonMap.visibility = View.VISIBLE
                    Preference(this@MainActivity).setCity(api?.name)
                    //linearLayoutTransparent.setBackgroundResource(R.drawable.background_main_item)
                    // afficher les données
                    textViewCity.text = api.name + ", " + api.sys?.country;

                    var roundedTemp = api.temperature?.let { String.format("%.1f", it.temp) }
                    textViewTemperature.text = roundedTemp.toString() + " °C";

                    textViewWeather.text =
                        api.weathers?.get(0)?.description?.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.ROOT
                            ) else it.toString()
                        };

                    var urlImage = String.format(URL_IMAGE, api.weathers?.firstOrNull()?.icon)
                    Glide.with(this@MainActivity).load(urlImage).into(imageViewIcon)

                    var roundedTempRessenti = api.temperature?.let { String.format("%.1f", it.feels_like) }
                    textViewRessenti.text = "Ressentie : " + roundedTempRessenti.toString() + " °C";


                    api.coord?.lon?.let { cityLon = it }
                    api.coord?.lat?.let { cityLat = it }
                    api.name.let { cityName = it }
                }
            },
            {
                buttonMap.visibility = View.GONE
                Preference(this@MainActivity).setCity(null)
                val toast = Toast.makeText(this@MainActivity, "Cette ville n'existe pas", Toast.LENGTH_SHORT)
                toast.show()
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

}