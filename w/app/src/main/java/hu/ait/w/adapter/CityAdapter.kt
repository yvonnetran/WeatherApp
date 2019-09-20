package hu.ait.w.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.ait.w.DetailsActivity
import hu.ait.w.R
import hu.ait.w.ScrollingActivity
import hu.ait.w.data.AppDatabase
import hu.ait.w.data.City
import hu.ait.w.touch.CityTouchHelperCallback
import kotlinx.android.synthetic.main.city_row.view.*
import java.util.*

class CityAdapter : RecyclerView.Adapter<CityAdapter.ViewHolder>, CityTouchHelperCallback {

    companion object {
        val CITY_NAME = "CITY_NAME"
    }

    var cityItems = mutableListOf<City>()
    private val context: Context

    constructor(context: Context, listCitys: List<City>) : super() {
        this.context = context
        cityItems.addAll(listCitys)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val cityRowView = LayoutInflater.from(context).inflate(
            R.layout.city_row, viewGroup, false
        )
        return ViewHolder(cityRowView)
    }

    override fun getItemCount(): Int {
        return cityItems.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val city = cityItems.get(viewHolder.adapterPosition)

        viewHolder.tvItemName.text = city.cityName.capitalize()

        viewHolder.itemView.setOnClickListener{
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(CITY_NAME, cityItems.get(viewHolder.adapterPosition).cityName)
            context.startActivity(intent)
        }

        viewHolder.btnDelete.setOnClickListener {
            deleteCity(viewHolder.adapterPosition) }
    }

    fun updateCity(city: City, editIndex: Int) {
        cityItems.set(editIndex, city)
        notifyItemChanged(editIndex)
    }


    fun addCity(city: City) {
        cityItems.add(0, city)
        notifyItemInserted(0)
    }

    fun deleteCity(deletePosition: Int) {
        Thread {
            AppDatabase.getInstance(context).cityDao().deleteCity(cityItems.get(deletePosition))
            (context as ScrollingActivity).runOnUiThread {
                cityItems.removeAt(deletePosition)
                notifyItemRemoved(deletePosition)
            }
        }.start()
    }

    override fun onDismissed(position: Int) {
        deleteCity(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(cityItems, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var btnDelete = itemView.btnDelete
        var tvItemName = itemView.tvName
    }
}