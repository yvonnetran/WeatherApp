package hu.ait.w

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import hu.ait.w.adapter.CityAdapter.Companion.CITY_NAME
import hu.ait.w.data.WeatherResult
import hu.ait.w.network.WeatherAPI
import kotlinx.android.synthetic.main.activity_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailsActivity : AppCompatActivity() {

    private val HOST_URL = "https://api.openweathermap.org/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        weatherDetails(intent.getStringExtra(CITY_NAME))
    }

    private fun weatherDetails(city: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(HOST_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherAPI = retrofit.create(WeatherAPI::class.java)
        val weatherCall = weatherAPI.getWeather(city,
            getString(R.string.weather_unit),
            getString(R.string.api_key))

        weatherCall.enqueue(object : Callback<WeatherResult> {
            override fun onFailure(call: Call<WeatherResult>, t: Throwable) {
                tvCityName.text = getString(R.string.error_cannot_connect)
            }

            override fun onResponse(call: Call<WeatherResult>, response: Response<WeatherResult>) {
                val result = response.body()

                if(result?.name != null){
                    tvCityName.text = result.name.capitalize()

                    tvTemperature.text = result.main?.temp.toString() + getString(R.string.degrees_string)

                    tvCountry.text = result.sys?.country.toString()

                    tvHumidity.text = getString(R.string.humidity_string) + " " + result.main?.humidity.toString() + getString(R.string.percent_string)

                    tvWind.text = getString(R.string.wind_string) + " " + result.wind?.speed.toString() + getString(R.string.speed_string)

                    tvDescription.text = result.weather?.get(0)?.description?.capitalize()

                    Glide.with(this@DetailsActivity)
                        .load(
                            ("https://openweathermap.org/img/w/"
                                    + response.body()?.weather?.get(0)?.icon + getString(R.string.file_type)))
                        .into(imIcon)
                } else {
                    tvCityName.text = getString(R.string.city_not_found_error)
                    tvTemperature.text = getString(R.string.na_error)
                    tvCountry.text = getString(R.string.error_hint)
                    tvHumidity.text = getString(R.string.na_error)
                    tvWind.text = getString(R.string.na_error)
                    tvDescription.text = getString(R.string.na_error)
                    imIcon.setImageResource(0)
                }
            }
        })
    }
}