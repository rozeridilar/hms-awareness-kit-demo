package com.huawei.awarenesskitsuperdemo.capture.beacon

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.huawei.awarenesskitsuperdemo.R
import com.huawei.hms.kit.awareness.Awareness
import com.huawei.hms.kit.awareness.status.BeaconStatus
import java.util.*


class BeaconAwarenessActivity : AppCompatActivity() {

    private lateinit var logViewTv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beacon_awareness)

        logViewTv = findViewById(R.id.logView)

        getBeacons()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, BeaconAwarenessActivity::class.java))
            }
    }

    private fun getBeacons(){
        val namespace = "sample namespace"
        val type = "sample type"
        val content = byteArrayOf(
            's'.toByte(),
            'a'.toByte(),
            'm'.toByte(),
            'p'.toByte(),
            'l'.toByte(),
            'e'.toByte()
        )
        val filter = BeaconStatus.Filter.match(namespace, type, content)
        Awareness.getCaptureClient(this).getBeaconStatus(filter)
            .addOnSuccessListener { beaconStatusResponse ->
                val beaconDataList =
                    beaconStatusResponse.beaconStatus.beaconData
                if (beaconDataList != null && beaconDataList.size != 0) {
                    var i = 1
                    val builder = StringBuilder()
                    for (beaconData in beaconDataList) {
                        builder.append("Beacon Data ").append(i)
                        builder.append(" namespace:").append(beaconData.namespace)
                        builder.append(",type:").append(beaconData.type)
                        builder.append(",content:")
                            .append(Arrays.toString(beaconData.content))
                        builder.append("; ")
                        i++
                    }

                    logViewTv.text = builder.toString()

                } else {
                    logViewTv.text = "no beacons match filter nearby"
                }
            }
            .addOnFailureListener { e ->
                logViewTv.text = "get beacon status failed: " + e.message
            }
    }
}
