package com.example.weatherapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Callable
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

//1ab5b9e6061e041f76f7f6f4eba2985d
class MainActivity : AppCompatActivity() {
    private  val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchWeatherdata("jaipur")
        searchCity()


    }

    private fun searchCity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherdata(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

    }

    private fun fetchWeatherdata(cityName: String) {
        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response=retrofit.getWeatherData(cityName,"1ab5b9e6061e041f76f7f6f4eba2985d","metric")
        response.enqueue(object : Callback<weatherApp> {
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody!=null){
                    //Log.d("TAG", "onResponse: $temperature")

                    val temperature=responseBody.main.temp.toString()
                    val humidity=responseBody.main.humidity
                    val windspeed=responseBody.wind.speed
                    val sunrise=responseBody.sys.sunrise.toLong()
                    val sunset=responseBody.sys.sunset.toLong()
                    val sealevel=responseBody.main.pressure
                    val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxtemp=responseBody.main.temp_max
                    val mintemp=responseBody.main.temp_min

                    binding.temp.text= "$temperature °C"
                    binding.weather.text=condition
                    binding.maxTemp.text="Max Temp: $maxtemp °C"
                    binding.minTemp.text="Min Temp: $mintemp °C"
                    binding.humdity.text="$humidity %"
                    binding.windspeed.text="$windspeed m/s"
                    binding.sunrise.text="${time(sunrise)}"
                    binding.sunset.text="${time(sunset)}"
                    binding.sea.text="$sealevel hPa"
                    binding.conditions.text=condition
                    binding.day.text=dayName(System.currentTimeMillis())
                    binding.date.text=date()
                    binding.cityName.text="$cityName"

                    changeImagesAccToWeatherCondition(condition)

                }

            }

            override fun onFailure(p0: Call<weatherApp>, p1: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun changeImagesAccToWeatherCondition(conditions:String) {
        when(conditions){
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
    fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
    fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }


}