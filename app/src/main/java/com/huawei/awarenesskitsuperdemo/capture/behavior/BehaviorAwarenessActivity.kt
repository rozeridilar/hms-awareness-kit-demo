package com.huawei.awarenesskitsuperdemo.capture.behavior

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.huawei.awarenesskitsuperdemo.R
import com.huawei.hms.kit.awareness.Awareness
import com.huawei.hms.location.ActivityIdentificationData
import java.util.*


class BehaviorAwarenessActivity : AppCompatActivity() {

    private val PERMISSION_ACTIVITY_RECOGNITION = 940

    private lateinit var logViewTv : TextView
    private lateinit var timeTv : TextView
    private lateinit var elapsedTimeTv : TextView
    private lateinit var mostProbableActivityTv : TextView
    private lateinit var probableActivitiesTv : TextView
    private lateinit var activityConfidenceTv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_behavior)

        logViewTv = findViewById(R.id.logView)
        timeTv = findViewById(R.id.text_time)
        elapsedTimeTv = findViewById(R.id.text_elapsed_time)
        mostProbableActivityTv = findViewById(R.id.text_most_probable_activity)
        probableActivitiesTv = findViewById(R.id.text_probable_activities)
        activityConfidenceTv = findViewById(R.id.text_activity_confidence)

        getUserActivity()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, BehaviorAwarenessActivity::class.java))
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_ACTIVITY_RECOGNITION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserActivity()
                } else {
                    Toast.makeText(
                        applicationContext,  getString(R.string.error_general),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getUserActivity(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), PERMISSION_ACTIVITY_RECOGNITION)
            }
            return
        }

        Awareness.getCaptureClient(this).behavior
            .addOnSuccessListener { behaviorResponse ->

                val behaviorStatus = behaviorResponse.behaviorStatus
                val mostLikelyBehavior = behaviorStatus.mostLikelyBehavior
                val mostLikelyBehaviors = behaviorStatus.probableBehavior
                val mostProbableActivity = "Most likely behavior type is " + mostLikelyBehavior.type

                val detectedBehavior: Long = behaviorStatus.time

                val dateString = DateFormat.format("dd/MM/yyyy hh:mm:ss", Date(detectedBehavior)).toString()
                timeTv.text = getString(R.string.text_activity_time, dateString)

                val elapsedTime: Long = behaviorStatus.elapsedRealtimeMillis

                val elapsed = DateFormat.format("hh:mm:ss", Date(elapsedTime)).toString()
                elapsedTimeTv.text = getString(R.string.text_elapsed_time, elapsed)

                mostProbableActivityTv.text = mostProbableActivity

                if (mostLikelyBehaviors.size > 0) {
                    val stringBuilder = StringBuilder()
                    for (i in mostLikelyBehaviors.indices) {
                        if (i > 0) {
                            stringBuilder.append(", ")
                        }
                        stringBuilder.append(
                            getActivityString(mostLikelyBehaviors[i].type)
                        )
                    }
                    probableActivitiesTv.text = getString(R.string.text_probable_activities, stringBuilder.toString())
                }

                val activityConfidence = behaviorResponse.behaviorStatus.getBehaviorConfidence(ActivityIdentificationData.RUNNING)

                activityConfidenceTv.text = getString(R.string.text_running_confidence, activityConfidence)
            }
            .addOnFailureListener {
                logViewTv.text = "get behavior failed: " + it.message
            }

        ActivityIdentificationData.RUNNING
    }

    private fun getActivityString(activity: Int): String? {
        return when (activity) {
            ActivityIdentificationData.VEHICLE -> getString(R.string.activity_in_vehicle)
            ActivityIdentificationData.BIKE -> getString(R.string.activity_on_bicycle)
            ActivityIdentificationData.FOOT -> getString(R.string.activity_on_foot)
            ActivityIdentificationData.RUNNING -> getString(R.string.activity_running)
            ActivityIdentificationData.STILL -> getString(R.string.activity_still)
            ActivityIdentificationData.TILTING -> getString(R.string.activity_tilting)
            ActivityIdentificationData.WALKING -> getString(R.string.activity_walking)
            else -> getString(R.string.activity_unknown)
        }
    }
}
