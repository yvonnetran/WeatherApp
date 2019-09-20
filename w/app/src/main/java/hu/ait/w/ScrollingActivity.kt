package hu.ait.w

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import hu.ait.w.adapter.CityAdapter
import hu.ait.w.data.AppDatabase
import hu.ait.w.data.City
import hu.ait.w.touch.CityRecyclerTouchCallback
import kotlinx.android.synthetic.main.activity_scrolling.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt

class ScrollingActivity : AppCompatActivity(), CityDialog.CityHandler {

    lateinit var cityAdapter : CityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)

        setSupportActionBar(toolbar)
        fab.setOnClickListener { view -> showAddCityDialog() }

        if (!wasOpenedEarlier()) {
            MaterialTapTargetPrompt.Builder(this)
                .setTarget(R.id.fab).setPrimaryText(getString(R.string.fab_new_city_text))
                .setSecondaryText("Click here to add a city").show()
        }

        saveFirstOpenInfo()
        initRecyclerViewFromDB()
    }

    fun saveFirstOpenInfo() {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()
        editor.putBoolean("KEY_WAS_OPEN", true)
        editor.apply()
    }

    fun wasOpenedEarlier() : Boolean {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPref.getBoolean("KEY_WAS_OPEN", false)
    }



    private fun initRecyclerViewFromDB() {
        Thread {
            var cityList = AppDatabase.getInstance(this@ScrollingActivity).cityDao().getAllCities()

            runOnUiThread {
                cityAdapter = CityAdapter(this, cityList)

                recyclerCity.layoutManager = LinearLayoutManager(this)
                recyclerCity.adapter = cityAdapter

                val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
                recyclerCity.addItemDecoration(itemDecoration)

                val callback = CityRecyclerTouchCallback(cityAdapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerCity)
            }

        }.start()
    }

    private fun showAddCityDialog() {
        CityDialog().show(supportFragmentManager, "TAG_TODO_DIALOG")
    }

    var editIndex: Int = -1


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> { showAddCityDialog() }
        }
        return true
    }


    override fun cityCreated(item: City) {
        Thread {
            var newId = AppDatabase.getInstance(this).cityDao().insertCity(item)
            item.cityId = newId

            runOnUiThread {
                cityAdapter.addCity(item)
            }
        }.start()
    }

    override fun cityUpdated(item: City) {
        Thread {
            AppDatabase.getInstance(this).cityDao().updateCity(item)
            runOnUiThread {
                cityAdapter.updateCity(item, editIndex)
            }
        }.start()
    }
}