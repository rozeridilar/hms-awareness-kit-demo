package com.huawei.awarenesskitsuperdemo.capture.headset

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.huawei.awarenesskitsuperdemo.R
import com.huawei.hms.kit.awareness.Awareness
import com.huawei.hms.kit.awareness.status.HeadsetStatus

class HeadsetAwarenessActivity : AppCompatActivity() {

    private lateinit var logViewTv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_headset_awareness)

        logViewTv = findViewById(R.id.logView)

        getHeadsetStatus()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, HeadsetAwarenessActivity::class.java))
            }
    }

    private fun getHeadsetStatus() {
        // Use the getHeadsetStatus API to get headset connection status.
        Awareness.getCaptureClient(this)
            .headsetStatus
            .addOnSuccessListener { headsetStatusResponse ->
                val headsetStatus = headsetStatusResponse.headsetStatus
                val status = headsetStatus.status
                val stateStr = "Headsets are " + if (status == HeadsetStatus.CONNECTED) "connected" else "disconnected"
                logViewTv.text = stateStr
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    applicationContext,
                    "get Headsets Capture failed", Toast.LENGTH_SHORT
                ).show()
                logViewTv.text = "get Headsets Capture failed" + e.message
            }
    }
}
