package fr.creative.meteo.ui.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import fr.creative.meteo.AppActivity
import fr.creative.meteo.R
import fr.creative.meteo.databinding.ActivityMapsBinding
import java.lang.Integer.max
import java.net.URL

class MapsActivity : AppActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var precipitations: Button
    private lateinit var temperatures: Button
    private lateinit var vents: Button
    private var layerUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        precipitations = findViewById(R.id.precipitations)
        temperatures = findViewById(R.id.temperatures)
        vents = findViewById(R.id.vents)

        precipitations.setOnClickListener {
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val cityName = intent.getStringExtra("cityName")
            val city = LatLng(latitude, longitude)

            val tileProvider = object : UrlTileProvider(256, 256) {
                override fun getTileUrl(x: Int, y: Int, zoom: Int): URL {
                    return URL("https://tile.openweathermap.org/map/precipitation_new/$zoom/$x/$y.png?appid=ae3b18f5fd3d61182c7bb64054b419c2")
                }
            }
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(city).title(cityName))
            mMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))
        }

        temperatures.setOnClickListener {
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val cityName = intent.getStringExtra("cityName")
            val city = LatLng(latitude, longitude)

            val tileProvider = object : UrlTileProvider(256, 256) {
                override fun getTileUrl(x: Int, y: Int, zoom: Int): URL {
                    return URL("https://tile.openweathermap.org/map/temp_new/$zoom/$x/$y.png?appid=ae3b18f5fd3d61182c7bb64054b419c2")
                }
            }
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(city).title(cityName))
            mMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))
        }

        vents.setOnClickListener {
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val cityName = intent.getStringExtra("cityName")
            val city = LatLng(latitude, longitude)

            val tileProvider = object : UrlTileProvider(256, 256) {
                override fun getTileUrl(x: Int, y: Int, zoom: Int): URL {
                    return URL("https://tile.openweathermap.org/map/wind_new/$zoom/$x/$y.png?appid=ae3b18f5fd3d61182c7bb64054b419c2")
                }
            }
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(city).title(cityName))
            mMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val cityName = intent.getStringExtra("cityName")
        val city = LatLng(latitude, longitude)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.view?.viewTreeObserver?.addOnGlobalLayoutListener {
            val width = mapFragment.view?.width ?: 0
            val height = mapFragment.view?.height ?: 0
            val tileSize = max(width, height) / 2
            val tileProvider = object : UrlTileProvider(tileSize, tileSize) {
                override fun getTileUrl(x: Int, y: Int, zoom: Int): URL {
                    val url = "https://tile.openweathermap.org/map/precipitation_new/$zoom/$x/$y.png?appid=ae3b18f5fd3d61182c7bb64054b419c2"
                    return URL(url)
                }
            }
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(city).title(cityName))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(city, 5f))
            mMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))
        }

        val style = MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
        mMap.setMapStyle(style)
    }
}