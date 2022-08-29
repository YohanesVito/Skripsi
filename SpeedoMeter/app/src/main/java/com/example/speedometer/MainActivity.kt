package com.example.speedometer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.speedometer.databinding.ActivityMainBinding
import java.net.ServerSocket


class MainActivity : AppCompatActivity(), LocationListener, SensorEventListener{
    private lateinit var binding: ActivityMainBinding

    private lateinit var locationManager: LocationManager
    private lateinit var sensorManager: SensorManager
    private lateinit var serverSocket: ServerSocket
    private lateinit var thread: Thread

    lateinit var viewModel: MainViewModel

    companion object{
        var duration = 1000L
        var speed = 50
        var SERVER_IP = ""
        val SERVER_PORT = 8080
        private const val locationPermissionCode = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.currentRandom.observe(this, androidx.lifecycle.Observer{
            getSpeed(it, duration)
        })

        viewModel.latitude.observe(this, androidx.lifecycle.Observer {
            binding.tvLat.text = it.toString()
        })

        viewModel.longitude.observe(this, androidx.lifecycle.Observer {
            binding.tvLon.text = it.toString()
        })

        binding.btSend.setOnClickListener {
            startActivity(Intent(this,SecondActivity::class.java))
        }

        randomSpeed()
        getLocation()
        setUpGyroSensor()
    }

    private fun randomSpeed(){
        binding.btRandom.setOnClickListener {
            viewModel.currentRandom.value = (0..100).random()
        }
    }
    private fun getSpeed(s:Int, d:Long){
        binding.tvSpeed.text = s.toString()
        val speedometer = binding.speedometer
        speedometer.setSpeed(s,d)
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }
    override fun onLocationChanged(location: Location) {
        viewModel.latitude.value = location.latitude
        viewModel.longitude.value = location.longitude
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //gyroscope
    private fun setUpGyroSensor(){
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(this,it,SensorManager.SENSOR_DELAY_FASTEST,SensorManager.SENSOR_DELAY_FASTEST)
        }
    }
    override fun onSensorChanged(event: SensorEvent?) {
        if(event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            val sides = event.values[0]
            val upDown = event.values[1]

            binding.tvGyrovalue.apply {
                rotationX = upDown * 3f
                rotationY = sides * 3f
                rotation = -sides
                translationX = sides * -10
                translationY = upDown * 10

            }

            val color = if (upDown.toInt()== 0
                && sides.toInt() ==0) Color.GREEN else Color.RED
            binding.tvGyrovalue.setBackgroundColor(color)

            binding.tvGyrovalue.text = "up/down ${upDown.toInt()}\nleft/right ${sides.toInt()}"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
}