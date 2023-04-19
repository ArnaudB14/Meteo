package fr.creative.meteo

import android.R
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import fr.creative.meteo.ui.main.MainActivity

open class AppActivity : AppCompatActivity() {
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null && this@AppActivity.javaClass != MainActivity::class.java) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }
}