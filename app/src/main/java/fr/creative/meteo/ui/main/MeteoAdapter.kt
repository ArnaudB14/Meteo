import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import fr.creative.meteo.R
import fr.creative.meteo.ui.main.MeteoModel

class MeteoAdapter(context: Context, meteoList: List<MeteoModel>) :
    ArrayAdapter<MeteoModel>(context, 0, meteoList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val meteo = getItem(position)

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }

        val dateTextView = view!!.findViewById<TextView>(R.id.date)
        val temperatureTextView = view.findViewById<TextView>(R.id.temperature)
        val descriptionTextView = view.findViewById<TextView>(R.id.description)

        dateTextView.text = meteo?.date
        temperatureTextView.text = meteo?.temperature
        descriptionTextView.text = meteo?.description

        return view
    }
}
