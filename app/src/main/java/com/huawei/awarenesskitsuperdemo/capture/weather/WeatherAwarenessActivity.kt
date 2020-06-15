package com.huawei.awarenesskitsuperdemo.capture.weather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.huawei.awarenesskitsuperdemo.R
import com.huawei.hms.kit.awareness.Awareness


class WeatherAwarenessActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940

    private lateinit var logViewTv : TextView
    private lateinit var cityNameTv : TextView
    private lateinit var weatherIdTv : TextView
    private lateinit var cnWeatherIdTv : TextView
    private lateinit var temperatureCTv : TextView
    private lateinit var temperatureFTv : TextView
    private lateinit var windSpeedTv : TextView
    private lateinit var windDirectionTv : TextView
    private lateinit var humidityTv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_awareness)

        logViewTv = findViewById(R.id.logView)
        cityNameTv = findViewById(R.id.cityNameTv)
        weatherIdTv = findViewById(R.id.weatherIdTv)
        cnWeatherIdTv = findViewById(R.id.cnWeatherIdTv)
        temperatureCTv = findViewById(R.id.temperatureCTv)
        temperatureFTv = findViewById(R.id.temperatureFTv)
        windSpeedTv = findViewById(R.id.windSpeedTv)
        windDirectionTv = findViewById(R.id.windDirectionTv)
        humidityTv = findViewById(R.id.humidityTv)

        getWeather()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, WeatherAwarenessActivity::class.java))
            }
    }


    private fun getWeather(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
            return
        }

        Awareness.getCaptureClient(this).weatherByDevice
            .addOnSuccessListener { weatherStatusResponse ->
                val weatherStatus = weatherStatusResponse.weatherStatus
                val weatherSituation = weatherStatus.weatherSituation
                val situation = weatherSituation.situation

                cityNameTv.text = "City: " + weatherSituation.city.name
                weatherIdTv.text = "Weather id is: " + situation.weatherId
                cnWeatherIdTv.text = "CN Weather id is: " + situation.cnWeatherId
                temperatureCTv.text = "Temperature is: " + situation.temperatureC + "℃"
                temperatureFTv.text = "Temperature is: " + situation.temperatureF + "℉"
                windSpeedTv.text = "Wind speed is: " + situation.windSpeed
                windDirectionTv.text = "Wind direction is: " + situation.windDir
                humidityTv.text = "Humidity is : " + situation.humidity

            }
            .addOnFailureListener {
                logViewTv.text = "get weather failed: " + it.message
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getWeather()
                } else {
                    Toast.makeText(
                        applicationContext,  getString(R.string.error_general),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
