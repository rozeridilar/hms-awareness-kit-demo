package com.huawei.awarenesskitsuperdemo.capture.location

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.huawei.awarenesskitsuperdemo.R
import com.huawei.hms.kit.awareness.Awareness

class LocationAwarenessActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940

    private lateinit var locationTv : TextView
    private lateinit var accuracyTv : TextView
    private lateinit var altitudeTv : TextView
    private lateinit var logViewTv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_awareness)

        locationTv = findViewById(R.id.text_location)
        accuracyTv = findViewById(R.id.text_accuracy)
        altitudeTv = findViewById(R.id.text_altitude)
        logViewTv = findViewById(R.id.logView)

        getLocation()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, LocationAwarenessActivity::class.java))
            }
    }

    private fun getLocation(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
            return
        }

        Awareness.getCaptureClient(this).location
            .addOnSuccessListener { locationResponse ->
                val location: Location = locationResponse.location

                val locationText = getString(R.string.text_coordinates, location.latitude.toString() + ", " + location.longitude)

                locationTv.text = locationText
                accuracyTv.text = getString(R.string.text_accuracy, location.accuracy)
                altitudeTv.text = getString(R.string.text_altitude, location.altitude)

            }
            .addOnFailureListener {
                logViewTv.text = "get location failed:" + it.message
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
                    getLocation()
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
