package fr.creative.meteo.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.gson.Gson
import fr.creative.meteo.R
import fr.creative.meteo.models.ApiCurrentWeather
import fr.creative.meteo.ui.maps.MapsActivity
import fr.creative.meteo.utils.FastDialog
import fr.creative.meteo.utils.Network
import fr.creative.meteo.utils.Preference
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Onglet1Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Onglet1Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var editTextCity: EditText? = null
    private lateinit var buttonSubmit: Button // Initialisation tardive
    private lateinit var textViewCity: TextView
    private lateinit var textViewTemperature: TextView
    private lateinit var imageViewIcon: ImageView
    private lateinit var textViewWeather: TextView
    private lateinit var textViewRessenti: TextView
    private lateinit var buttonMap: Button
    private lateinit var buttonOnglet1: Button
    private lateinit var buttonOnglet2: Button

    private var cityLon: Double? = null
    private var cityLat: Double? = null
    private var cityName: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_onglet1, container, false)

        editTextCity = view.findViewById(R.id.editTextCity)
        buttonSubmit = view.findViewById(R.id.buttonSubmit)
        textViewCity = view.findViewById(R.id.textViewCity)
        textViewTemperature = view.findViewById(R.id.textViewTemperature)
        imageViewIcon = view.findViewById(R.id.imageViewIcon)
        textViewWeather = view.findViewById(R.id.textViewWeather)
        textViewRessenti = view.findViewById(R.id.textViewRessenti)
        buttonMap = view.findViewById(R.id.buttonMap)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonSubmit.setOnClickListener { myButton ->
            // 1) vérification de la saisie d'une ville / affichage d'une erreur si nécessaire
            if (editTextCity?.text.toString().isEmpty()) {
                // affichage dialog
                FastDialog.showDialog(
                    requireContext(),
                    FastDialog.SIMPLE_DIALOG,
                    getString(R.string.main_error_city_empty)
                )
                return@setOnClickListener
            }

            // 2) vérification de la connexion / affichage d'une erreur si nécessaire (FastDialog)
            if (!Network.isNetworkAvailable(requireContext())) {
                // affichage dialog
                FastDialog.showDialog(
                    requireContext(),
                    FastDialog.SIMPLE_DIALOG,
                    getString(R.string.main_error_network)
                )
                return@setOnClickListener
            }
            // 3) requête HTTP avec VOLLEY

            // Instantiate the RequestQueue.
            val queue = Volley.newRequestQueue(requireContext())
            val url = String.format(URL, editTextCity?.text.toString())

            // Request a string response from the provided URL.
            val stringRequest = StringRequest(
                Request.Method.GET, url,
                { json -> // json = String
                    // Gson
                    val api = Gson().fromJson(json, ApiCurrentWeather::class.java)

                    if(api?.cod == 200) {
                        buttonMap.visibility = View.VISIBLE
                        Preference(requireContext()).setCity(api.name)
                        //linearLayoutTransparent.setBackgroundResource(R.drawable.background_main_item)
                        // afficher les données
                        textViewCity.text = api.name + ", " + api.sys?.country;

                        var roundedTemp = api.temperature?.temp?.let { String.format("%.1f", it) }
                        textViewTemperature.text = roundedTemp.toString() + " °C";

                        textViewWeather.text =
                            api.weathers?.get(0)?.description?.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.ROOT
                                ) else it.toString()
                            };

                        var urlImage = String.format(URL_IMAGE, api.weathers?.firstOrNull()?.icon)
                        Glide.with(requireContext()).load(urlImage).into(imageViewIcon)

                        var roundedTempRessenti = api.temperature?.feels_like?.let { String.format("%.1f", it) }
                        textViewRessenti.text = "Ressentie : " + roundedTempRessenti.toString() + " °C";

                        api.coord?.lon?.let { cityLon = it }
                        api.coord?.lat?.let { cityLat = it }
                        api.name.let { cityName = it }

                        // Initialize the shared view model
                        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

                        // Set the cityLon and cityLat values
                        sharedViewModel.cityLon = cityLon
                        sharedViewModel.cityLat = cityLat
                        sharedViewModel.cityName = cityName
                    }
                },
                {
                    buttonMap.visibility = View.GONE
                    Preference(requireContext()).setCity(null)
                    val toast = Toast.makeText(requireContext(), "Cette ville n'existe pas", Toast.LENGTH_SHORT)
                    toast.show()
                })

            // Add the request to the RequestQueue.
            queue.add(stringRequest)

            // 4) lecture du JSON et récupération d'un objet avec GSON
            // 5) affichage des informations sur le layout
        }
        buttonMap.setOnClickListener {
            val intentMap = Intent(requireContext(), MapsActivity::class.java)
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
        editTextCity?.setText(Preference(requireContext()).getCity())

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(requireContext())
        val url = String.format(URL, editTextCity?.text.toString())

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { json -> // json = String
                // Gson
                val api = Gson().fromJson(json, ApiCurrentWeather::class.java)

                if(api.cod == 200) {
                    buttonMap.visibility = View.VISIBLE
                    Preference(requireContext()).setCity(api?.name)
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
                    Glide.with(requireContext()).load(urlImage).into(imageViewIcon)

                    var roundedTempRessenti = api.temperature?.let { String.format("%.1f", it.feels_like) }
                    textViewRessenti.text = "Ressentie : " + roundedTempRessenti.toString() + " °C";

                    api.coord?.lon?.let { cityLon = it }
                    api.coord?.lat?.let { cityLat = it }
                    api.name.let { cityName = it }

                    // Initialize the shared view model
                    val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

                    // Set the cityLon and cityLat values
                    sharedViewModel.cityLon = cityLon
                    sharedViewModel.cityLat = cityLat
                    sharedViewModel.cityName = cityName
                }
            },
            {
                buttonMap.visibility = View.GONE
                Preference(requireContext()).setCity(null)
                val toast = Toast.makeText(requireContext(), "Cette ville n'existe pas", Toast.LENGTH_SHORT)
                toast.show()
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)

        // 4) lecture du JSON et récupération d'un objet avec GSON
        // 5) affichage des informations sur le layout
        }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Onglet1Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Onglet1Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        const val URL/*: String*/ =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&units=metric&lang=fr&appid=ae3b18f5fd3d61182c7bb64054b419c2"
        const val URL_IMAGE = "https://openweathermap.org/img/wn/%s@2x.png"
    }
}