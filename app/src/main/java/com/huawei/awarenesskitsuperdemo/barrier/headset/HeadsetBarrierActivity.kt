package com.huawei.awarenesskitsuperdemo.barrier.headset

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.huawei.awarenesskitsuperdemo.R
import com.huawei.hms.kit.awareness.Awareness
import com.huawei.hms.kit.awareness.barrier.*
import com.huawei.hms.kit.awareness.status.HeadsetStatus

class HeadsetBarrierActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private var mPendingIntent: PendingIntent? = null
    private var mBarrierReceiver: BarrierReceiver? = null
    private val mAddedLabels = hashSetOf<String>()

    private lateinit var logViewTv : TextView
    private lateinit var addBarrierBtn : Button
    private lateinit var addCombinationBarrierBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_headset_barrier)

        logViewTv = findViewById(R.id.logView)
        addBarrierBtn = findViewById(R.id.add_barrier)
        addCombinationBarrierBtn = findViewById(R.id.add_combination_barrier)

        val BARRIER_RECEIVER_ACTION = application.packageName + "BARRIER_RECEIVER_ACTION"

        val intent = Intent(BARRIER_RECEIVER_ACTION)
        mPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Register a broadcast receiver to receive the broadcast sent by the system when the barrier status changes.

        // Register a broadcast receiver to receive the broadcast sent by the system when the barrier status changes.
        mBarrierReceiver = BarrierReceiver()
        registerReceiver(mBarrierReceiver, IntentFilter(BARRIER_RECEIVER_ACTION))

        initView()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, HeadsetBarrierActivity::class.java))
            }
    }

    private fun initView(){

        addBarrierBtn.setOnClickListener{
            try {
                // Create a headset barrier. When the headset are connected, the barrier status changes to true temporarily for about 5 seconds.
                // After 5 seconds, the status changes to false. If headset are disconnected within 5 seconds, the status also changes to false.
                val barrier = HeadsetBarrier.connecting()
                val headsetBarrierLabel = "headset_connecting"
                addBarrier(headsetBarrierLabel, barrier)
            } catch (e: Exception) {
                logViewTv.text = "add barrier failed.Exception: " + e.message
            }
        }

        addCombinationBarrierBtn.setOnClickListener{
            try {
                // Use the AND logic to combine two barriers. That is, when the status of both barriers is true, the status of the combined barrier changes to true. The rule is similar for other states.
                // In the following example, the status of the combined barrier changes to true only when the user is still and the headset are connected.
                val stillBarrier = BehaviorBarrier.keeping(BehaviorBarrier.BEHAVIOR_STILL)
                val headsetBarrier = HeadsetBarrier.keeping(HeadsetStatus.CONNECTED)
                val combinationBarrierLabel = "still_AND_headsetConnected"
                val combinationBarrier = AwarenessBarrier.and(stillBarrier, headsetBarrier)
                addBarrier(combinationBarrierLabel, combinationBarrier)
                // In addition to the AND logic, the OR and NOT logic can be used to combine barrier.
            } catch (e: Exception) {
                logViewTv.text = "add combination barrier failed.Exception:" + e.message
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

    private fun addBarrier(label: String, barrier: AwarenessBarrier) {
        val builder = BarrierUpdateRequest.Builder()
        // When the status of the registered barrier changes, pendingIntent is triggered. In this example, pendingIntent sends a broadcast.
        // label is used to uniquely identify the barrier. You can query a barrier by label and delete it.
        val request = builder.addBarrier(label, barrier, mPendingIntent).build()
        val task = Awareness.getBarrierClient(this).updateBarriers(request)
        task.addOnSuccessListener {
            Toast.makeText(applicationContext, "add barrier success", Toast.LENGTH_SHORT).show()
            mAddedLabels.add(label)
        }.addOnFailureListener { e ->
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

    class BarrierReceiver : BroadcastReceiver() {
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
