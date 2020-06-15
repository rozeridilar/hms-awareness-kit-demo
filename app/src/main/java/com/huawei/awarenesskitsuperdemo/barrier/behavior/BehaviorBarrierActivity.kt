package com.huawei.awarenesskitsuperdemo.barrier.behavior

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.huawei.awarenesskitsuperdemo.R
import com.huawei.hms.kit.awareness.Awareness
import com.huawei.hms.kit.awareness.barrier.BarrierStatus
import com.huawei.hms.kit.awareness.barrier.BarrierUpdateRequest
import com.huawei.hms.kit.awareness.barrier.BehaviorBarrier


class BehaviorBarrierActivity : AppCompatActivity() {

    private val PERMISSION_ACTIVITY_RECOGNITION = 940

    private val TAG = javaClass.simpleName

    private var mPendingIntent: PendingIntent? = null
    private var mBarrierReceiver: BehaviorBarrierReceiver ? = null
    private val mAddedLabels = hashSetOf<String>()

    private lateinit var logViewTv : TextView
    private lateinit var addBarrierBtn : Button

    var keepStillBarrier = BehaviorBarrier.keeping(BehaviorBarrier.BEHAVIOR_STILL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_behavior_barrier)

        logViewTv = findViewById(R.id.logView)
        addBarrierBtn = findViewById(R.id.add_barrier)

        val BARRIER_RECEIVER_ACTION = application.packageName + "BEHAVIOR_BARRIER_RECEIVER_ACTION"

        val intent = Intent(BARRIER_RECEIVER_ACTION)
        mPendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBarrierReceiver = BehaviorBarrierReceiver()
        registerReceiver(mBarrierReceiver, IntentFilter(BARRIER_RECEIVER_ACTION))

        initView()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, BehaviorBarrierActivity::class.java))
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

                    val behaviorBarrierLabel = "behavior keeping barrier"

                    addBarrier(behaviorBarrierLabel)
                } else {
                    Toast.makeText(
                        applicationContext,  getString(R.string.error_general),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun initView(){

        addBarrierBtn.setOnClickListener{
            try {
                val behaviorBarrierLabel = "behavior keeping barrier"

                addBarrier(behaviorBarrierLabel)
            } catch (e: Exception) {
                logViewTv.text = "add barrier failed.Exception: " + e.message
            }
        }
    }

    override fun onDestroy() {
        if (mBarrierReceiver != null) {
            unregisterReceiver(mBarrierReceiver)
            mBarrierReceiver = null
        }
        deleteBarrier(mAddedLabels)
        super.onDestroy()
    }

    private fun addBarrier(label: String) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), PERMISSION_ACTIVITY_RECOGNITION)
            }
            return
        }

        val builder = BarrierUpdateRequest.Builder()
        val request = builder.addBarrier(label, keepStillBarrier, mPendingIntent).build()
        Awareness.getBarrierClient(applicationContext).updateBarriers(request)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "add barrier success", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "add barrier failed", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "add barrier failed", e)
            }
    }

    private fun deleteBarrier(labels: Set<String>) {
        val builder = BarrierUpdateRequest.Builder()
        for (label in labels) {
            builder.deleteBarrier(label)
        }
        Awareness.getBarrierClient(this)
            .updateBarriers(builder.build())
            .addOnSuccessListener {
                Log.i(TAG, "remove Barrier success")
            }
            .addOnFailureListener {
                    e -> Log.e(TAG, "remove Barrier failed", e)
            }
    }

    class BehaviorBarrierReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val barrierStatus = BarrierStatus.extract(intent)
            val label = barrierStatus.barrierLabel
            when (barrierStatus.presentStatus) {
                BarrierStatus.TRUE -> {
                    Toast.makeText(context, label + "status:true", Toast.LENGTH_SHORT).show()
                }
                BarrierStatus.FALSE -> {
                    Toast.makeText(context, label + "status:false", Toast.LENGTH_SHORT).show()
                }
                BarrierStatus.UNKNOWN -> {
                    Toast.makeText(context, label + "status:unknown", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
