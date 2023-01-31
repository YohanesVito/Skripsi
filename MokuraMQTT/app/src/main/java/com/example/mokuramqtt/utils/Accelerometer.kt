package com.example.mokuramqtt.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.mokuramqtt.viewmodel.MonitorViewModel

import kotlin.math.roundToInt

class Accelerometer(private val context: Context, private val monitorViewModel: MonitorViewModel): SensorEventListener {

    override fun onSensorChanged(event: SensorEvent?) {
        val degree = (event?.values?.get(0)!!).roundToInt()
        monitorViewModel.dataCompass.value = degree
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

     fun setUpCompass(){
        val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)?.also {
            sensorManager.registerListener(this,it,
                SensorManager.SENSOR_DELAY_GAME,
                SensorManager.SENSOR_DELAY_GAME)
        }
    }

}