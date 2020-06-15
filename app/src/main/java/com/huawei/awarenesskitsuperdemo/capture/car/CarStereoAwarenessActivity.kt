package com.huawei.awarenesskitsuperdemo.capture.car

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.huawei.awarenesskitsuperdemo.R
import com.huawei.hms.kit.awareness.Awareness
import com.huawei.hms.kit.awareness.status.BluetoothStatus


class CarStereoAwarenessActivity : AppCompatActivity() {

    private lateinit var logViewTv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_stereo_awareness)

        logViewTv = findViewById(R.id.logView)

        getCarStereoStatus()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, CarStereoAwarenessActivity::class.java))
            }
    }

    private fun getCarStereoStatus() {
        val deviceType = 0 // Value 0 indicates a Bluetooth car stereo.

        Awareness.getCaptureClient(this).getBluetoothStatus(deviceType)
            .addOnSuccessListener { bluetoothStatusResponse ->
                val bluetoothStatus =
                    bluetoothStatusResponse.bluetoothStatus
                val status = bluetoothStatus.status
                val stateStr = "The Bluetooth car stereo is " + if (status == BluetoothStatus.CONNECTED) "connected" else "disconnected"

                logViewTv.text = stateStr
            }
            .addOnFailureListener { e ->
                logViewTv.text = "get bluetooth status failed: " + e.message
            }
    }

}
