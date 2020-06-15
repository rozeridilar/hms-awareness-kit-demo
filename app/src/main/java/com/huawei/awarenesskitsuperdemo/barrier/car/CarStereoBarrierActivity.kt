package com.huawei.awarenesskitsuperdemo.barrier.car

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
import com.huawei.hms.kit.awareness.barrier.BarrierStatus
import com.huawei.hms.kit.awareness.barrier.BarrierUpdateRequest
import com.huawei.hms.kit.awareness.barrier.BluetoothBarrier


class CarStereoBarrierActivity : AppCompatActivity() {

    private val TAG = javaClass.simpleName

    private var mPendingIntent: PendingIntent? = null
    private var mBarrierReceiver: BluetoothBarrierReceiver ? = null
    private val mAddedLabels = hashSetOf<String>()

    private lateinit var logViewTv : TextView
    private lateinit var addBarrierBtn : Button

    val deviceType = 0 // Value 0 indicates a Bluetooth car stereo.
    var connectingBarrier = BluetoothBarrier.connecting(deviceType)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car_stereo_barrier)

        logViewTv = findViewById(R.id.logView)
        addBarrierBtn = findViewById(R.id.add_barrier)

        val BARRIER_RECEIVER_ACTION = application.packageName + "BLUETOOTH_BARRIER_RECEIVER_ACTION"

        val intent = Intent(BARRIER_RECEIVER_ACTION)
        mPendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBarrierReceiver = BluetoothBarrierReceiver()
        registerReceiver(mBarrierReceiver, IntentFilter(BARRIER_RECEIVER_ACTION))

        initView()
    }

    companion object {

        fun launch(activity: AppCompatActivity) =
            activity.apply {
                startActivity(Intent(this, CarStereoBarrierActivity::class.java))
            }
    }

    private fun initView(){

        addBarrierBtn.setOnClickListener{
            try {
                val bluetoothBarrierLabel = "bluetooth connecting barrier"

                addBarrier(bluetoothBarrierLabel)
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

        val builder = BarrierUpdateRequest.Builder()
        val request = builder.addBarrier(label, connectingBarrier, mPendingIntent).build()
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

    class BluetoothBarrierReceiver : BroadcastReceiver() {
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
