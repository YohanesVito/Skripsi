package com.example.monitoringwheelchair.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

import com.example.monitoringwheelchair.MainViewModel
import kotlin.math.roundToInt

class Compass(private val context: Context, private val viewModel: MainViewModel): SensorEventListener {

    override fun onSensorChanged(event: SensorEvent?) {
        val degree = (event?.values?.get(0)!!).roundToInt()
        viewModel.dataCompass.value = degree
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