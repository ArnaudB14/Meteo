package fr.creative.meteo.ui.main

import Api5daysWeather
import MeteoAdapter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import fr.creative.meteo.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [Onglet2Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Onglet2Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var listViewMeteo: ListView
    private lateinit var date: TextView
    private lateinit var temperature: TextView
    private lateinit var description: TextView
    private lateinit var textViewCity: TextView
    private var adapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_onglet2, container, false)

        listViewMeteo = view.findViewById(R.id.listViewMeteo)
        textViewCity = view.findViewById(R.id.textViewCity)

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val cityLon = sharedViewModel.cityLon
        val cityLat = sharedViewModel.cityLat
        val cityName = sharedViewModel.cityName


        val queue = Volley.newRequestQueue(requireContext())
        val url = String.format(URL, cityLat, cityLon)

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { json -> // json = String
                // Gson
                val api = Gson().fromJson(json, Api5daysWeather::class.java)

                if(api.cod == 200) {

                    textViewCity.text = cityName
                    val adapter = MeteoAdapter(requireContext(), ArrayList())

                    val items = api.list ?: emptyArray()

                    val filteredList = items.filter { item ->
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val dateTime = LocalDateTime.parse(item.dt_txt, formatter)
                        dateTime.hour == 12
                    }

                    // Obtenir la date courante et la date de demain
                    val currentDate = LocalDate.now()
                    val tomorrowDate = LocalDate.now().plusDays(1)

                    for (item in filteredList) {
                        // CONVERT DATE
                        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val outputDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

                        val inputDateStr = item.dt_txt
                        val inputDate = inputDateFormat.parse(inputDateStr)

                        // Créer une instance de Calendar avec la date de l'élément courant
                        val calendar = Calendar.getInstance()
                        calendar.time = inputDate

                        // Déterminer le libellé à afficher en fonction de la date de l'élément
                        val dayLabel = when {
                            LocalDate.from(inputDate.toInstant().atZone(ZoneId.systemDefault())) == currentDate -> "Aujourd'hui"
                            LocalDate.from(inputDate.toInstant().atZone(ZoneId.systemDefault())) == tomorrowDate -> "Demain"
                            else -> calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                        }

                        // Température
                        var roundedTemp = (String.format("%.1f", item.main.temp) + " °C")

                        // Description
                        var description = item.weather[0].description.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                        }

                        val meteo = MeteoModel(dayLabel, roundedTemp, description)
                        adapter.add(meteo)
                    }


                    adapter.notifyDataSetChanged()

                    listViewMeteo.adapter = adapter
                }
            },
            {
                val toast = Toast.makeText(requireContext(), "Cette ville n'existe pas", Toast.LENGTH_SHORT)
                toast.show()
            })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Onglet2Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(cityLon: Double, cityLat: Double) =
            Onglet2Fragment().apply {
                arguments = Bundle().apply {
                    putDouble("lon", cityLon)
                    putDouble("lat", cityLat)
                    Log.d("lonArg", cityLon.toString())
                    Log.d("latArg", cityLat.toString())
                }
            }

        const val URL/*: String*/ =
            "https://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&units=metric&lang=fr&appid=ae3b18f5fd3d61182c7bb64054b419c2"
    }
}