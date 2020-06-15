package com.huawei.awarenesskitsuperdemo.capture.ambientlight

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.huawei.awarenesskitsuperdemo.R
import com.huawei.hms.kit.awareness.Awareness


class AmbientLightAwarenessActivity : AppCompatActivity() {

    private lateinit var logViewTv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ambient_light)

        logViewTv = findViewById(R.id.logView)

        getAmbientLight()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, AmbientLightAwarenessActivity::class.java))
            }
    }

    private fun getAmbientLight() {
        Awareness.getCaptureClient(this).lightIntensity
            .addOnSuccessListener { ambientLightResponse ->
                val ambientLightStatus = ambientLightResponse.ambientLightStatus

                logViewTv.text = "Light intensity is " + ambientLightStatus.lightIntensity + " lux"
            }
            .addOnFailureListener { e ->
                logViewTv.text = "get light intensity failed" + e.message
            }
    }
}
