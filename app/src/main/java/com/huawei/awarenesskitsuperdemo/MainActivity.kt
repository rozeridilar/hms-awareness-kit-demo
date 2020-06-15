package com.huawei.awarenesskitsuperdemo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.huawei.awarenesskitsuperdemo.barrier.ambientlight.AmbientLightBarrierActivity
import com.huawei.awarenesskitsuperdemo.barrier.beacon.BeaconBarrierActivity
import com.huawei.awarenesskitsuperdemo.barrier.behavior.BehaviorBarrierActivity
import com.huawei.awarenesskitsuperdemo.barrier.car.CarStereoBarrierActivity
import com.huawei.awarenesskitsuperdemo.barrier.headset.HeadsetBarrierActivity
import com.huawei.awarenesskitsuperdemo.barrier.location.LocationBarrierActivity
import com.huawei.awarenesskitsuperdemo.barrier.time.TimeBarrierActivity
import com.huawei.awarenesskitsuperdemo.capture.ambientlight.AmbientLightAwarenessActivity
import com.huawei.awarenesskitsuperdemo.capture.beacon.BeaconAwarenessActivity
import com.huawei.awarenesskitsuperdemo.capture.behavior.BehaviorAwarenessActivity
import com.huawei.awarenesskitsuperdemo.capture.car.CarStereoAwarenessActivity
import com.huawei.awarenesskitsuperdemo.capture.headset.HeadsetAwarenessActivity
import com.huawei.awarenesskitsuperdemo.capture.location.LocationAwarenessActivity
import com.huawei.awarenesskitsuperdemo.capture.time.TimeAwarenessActivity
import com.huawei.awarenesskitsuperdemo.capture.weather.WeatherAwarenessActivity

class MainActivity : AppCompatActivity(){

    private lateinit var timeTv : TextView
    private lateinit var locationTv : TextView
    private lateinit var behaviorTv : TextView
    private lateinit var beaconTv : TextView
    private lateinit var bluetoothCarStereoTv : TextView
    private lateinit var headsetTv : TextView
    private lateinit var ambientLightTv : TextView
    private lateinit var weatherTv : TextView

    private lateinit var timeBarrierTv : TextView
    private lateinit var locationBarrierTv : TextView
    private lateinit var behaviorBarrierTv : TextView
    private lateinit var beaconBarrierTv : TextView
    private lateinit var bluetoothCarStereoBarrierTv : TextView
    private lateinit var headsetBarrierTv : TextView
    private lateinit var ambientLightBarrierTv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timeTv = findViewById(R.id.time_lyt)
        locationTv = findViewById(R.id.location_lyt)
        behaviorTv = findViewById(R.id.behavior_lyt)
        beaconTv = findViewById(R.id.beacon_lyt)
        bluetoothCarStereoTv = findViewById(R.id.bluetooth_car_stereo_lyt)
        headsetTv = findViewById(R.id.headset_lyt)
        ambientLightTv = findViewById(R.id.ambient_light_lyt)
        weatherTv = findViewById(R.id.weather_lyt)

        timeBarrierTv = findViewById(R.id.time_barrier_lyt)
        locationBarrierTv = findViewById(R.id.location_barrier_lyt)
        behaviorBarrierTv = findViewById(R.id.behavior_barrier_lyt)
        beaconBarrierTv = findViewById(R.id.beacon_barrier_lyt)
        bluetoothCarStereoBarrierTv = findViewById(R.id.bluetooth_car_stereo_barrier_lyt)
        headsetBarrierTv = findViewById(R.id.headset_barrier_lyt)
        ambientLightBarrierTv = findViewById(R.id.ambient_light_barrier_lyt)

        initView()
    }

    private fun initView(){
        timeTv.setOnClickListener{
            TimeAwarenessActivity.launch(this)
        }

        locationTv.setOnClickListener{
            LocationAwarenessActivity.launch(this)
        }

        behaviorTv.setOnClickListener{
            BehaviorAwarenessActivity.launch(this)
        }

        beaconTv.setOnClickListener{
            BeaconAwarenessActivity.launch(this)
        }

        bluetoothCarStereoTv.setOnClickListener{
            CarStereoAwarenessActivity.launch(this)
        }

        headsetTv.setOnClickListener{
            HeadsetAwarenessActivity.launch(this)
        }

        ambientLightTv.setOnClickListener{
            AmbientLightAwarenessActivity.launch(this)
        }

        weatherTv.setOnClickListener{
            WeatherAwarenessActivity.launch(this)
        }

        timeBarrierTv.setOnClickListener{
            TimeBarrierActivity.launch(this)
        }

        locationBarrierTv.setOnClickListener{
            LocationBarrierActivity.launch(this)
        }

        behaviorBarrierTv.setOnClickListener{
            BehaviorBarrierActivity.launch(this)
        }

        beaconBarrierTv.setOnClickListener{
            BeaconBarrierActivity.launch(this)
        }

        bluetoothCarStereoBarrierTv.setOnClickListener{
            CarStereoBarrierActivity.launch(this)
        }

        headsetBarrierTv.setOnClickListener{
            HeadsetBarrierActivity.launch(this)
        }

        ambientLightBarrierTv.setOnClickListener{
            AmbientLightBarrierActivity.launch(this)
        }
    }
}
