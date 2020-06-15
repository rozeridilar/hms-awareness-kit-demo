package com.huawei.awarenesskitsuperdemo.capture.time

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.huawei.awarenesskitsuperdemo.R
import com.huawei.hms.kit.awareness.Awareness
import com.huawei.hms.kit.awareness.barrier.TimeBarrier

class TimeAwarenessActivity : AppCompatActivity() {

    private val TAG = TimeAwarenessActivity::class.java.simpleName

    private val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 940

    private val mTimeInfoMap = hashMapOf<Int, Any?>()

    private lateinit var logViewTv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_awareness)

        logViewTv = findViewById(R.id.logView)

        initTimeInfoMap()
        getTimeCategories()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, TimeAwarenessActivity::class.java))
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
                    getTimeCategories()
                } else {
                    Toast.makeText(
                        applicationContext,  getString(R.string.error_general),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getTimeCategories() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
            return
        }

        try {
            // Use getTimeCategories() to get the information about the current time of the user location.
            // Time information includes whether the current day is a workday or a holiday, and whether the current day is in the morning, afternoon, or evening, or at the night.
            val task = Awareness.getCaptureClient(this).timeCategories
            task.addOnSuccessListener { timeCategoriesResponse ->
                val timeCategories = timeCategoriesResponse.timeCategories
                val stringBuilder = StringBuilder()
                for (timeCategoriesCode in mTimeInfoMap.keys) {
                    if (timeCategories.isTimeCategory(timeCategoriesCode)) {
                        stringBuilder.append(mTimeInfoMap[timeCategoriesCode])
                    }
                }
                logViewTv.text = stringBuilder.toString()

            }.addOnFailureListener { e ->
                Toast.makeText(
                    applicationContext, "get Time Categories failed",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e(TAG, "get Time Categories failed", e)
            }
        } catch (e: Exception) {
            logViewTv.text = "get Time Categories failed.Exception:" + e.message
        }
    }

    private fun initTimeInfoMap() {
        val weekday = "Today is weekday."
        val weekend = "Today is weekend."
        val holiday = "Today is holiday."
        val morning = "Good morning."
        val afternoon = "Good afternoon."
        val evening = "Good evening."
        val night = "Good night."

        mTimeInfoMap[TimeBarrier.TIME_CATEGORY_WEEKDAY] = weekday
        mTimeInfoMap[TimeBarrier.TIME_CATEGORY_WEEKEND] = weekend
        mTimeInfoMap[TimeBarrier.TIME_CATEGORY_HOLIDAY] = holiday
        mTimeInfoMap[TimeBarrier.TIME_CATEGORY_MORNING] = morning
        mTimeInfoMap[TimeBarrier.TIME_CATEGORY_AFTERNOON] = afternoon
        mTimeInfoMap[TimeBarrier.TIME_CATEGORY_EVENING] = evening
        mTimeInfoMap[TimeBarrier.TIME_CATEGORY_NIGHT] = night
    }
}
