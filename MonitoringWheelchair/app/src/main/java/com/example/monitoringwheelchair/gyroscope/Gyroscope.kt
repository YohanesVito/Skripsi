package com.example.monitoringwheelchair.gyroscope

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.monitoringwheelchair.MainViewModel

class Gyroscope(private val context: Context, private val viewModel: MainViewModel): SensorEventListener {

    fun setUpGyroSensor(){
//        private lateinit var sensorManager: SensorManager
        val sensorManager: SensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this,it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST)
        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            val sides = event.values[0]
            val upDown = event.values[1]

//            binding.tvGyrovalue.apply {
//            viewModel.rotationX.value = upDown * 3f
//            viewModel.rotationY.value = sides * 3f
//            viewModel.rotation.value = -sides
//            viewModel.translationX.value = sides * -10
//            viewModel.translationY.value = upDown * 10
//
////            }

            val color = if (upDown.toInt()== 0
                && sides.toInt() ==0) Color.GREEN else Color.RED

            val text = "up/down ${upDown.toInt()}\nleft/right ${sides.toInt()}"
            viewModel.dataGyroscope.value = GyroscopeData(-sides,upDown * 3f,sides * 3f,sides * -10,upDown * 10,color,text)
//            binding.tvGyrovalue.setBackgroundColor(color)

 //           binding.tvGyrovalue.text = "up/down ${upDown.toInt()}\nleft/right ${sides.toInt()}"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

}