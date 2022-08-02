package com.example.speedometer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.speedometer.databinding.ActivityMainBinding
import java.net.InetAddress
import java.net.ServerSocket
import java.net.UnknownHostException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*


class MainActivity : AppCompatActivity(), LocationListener{
    private lateinit var binding: ActivityMainBinding

    private lateinit var locationManager: LocationManager
    private lateinit var serverSocket: ServerSocket
    private lateinit var thread: Thread


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



        getLocation()

//        binding.btSend.setOnClickListener {
//            startActivity(Intent(this,SendDataActivity::class.java))
//        }

        binding.btRefresh.setOnClickListener {
            finish()
            startActivity(intent)
        }

        binding.btUpdate.setOnClickListener {
            val speed = binding.etSpeed.text.toString()
            getSpeed(speed.toInt(), duration)
        }
//        try {
//            SERVER_IP = getLocalIpAddress();
//        } catch (e: UnknownHostException) {
//            e.printStackTrace();
//        }
//        thread = Thread()
//        thread.start()
    }
//    private fun getLocalIpAddress():String  {
//        var wifiManager = WifiManager
//        wifiManager = getApplicationContext().getSystemService(WIFI_SERVICE)
//
//        val wifiInfo = wifiManager.getConnectionInfo();
//        val ipInt = wifiInfo.getIpAddress();
//        return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
//    }
    private fun getSpeed(s:Int, d:Long){
        val speedometer = binding.speedometer
        speedometer.setSpeed(s,d)
    }

    private fun receiveData(){

    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }
    override fun onLocationChanged(location: Location) {
        val tvGpsLocation = binding.tvGPS
        tvGpsLocation.text = "Latitude: " + location.latitude + " , Longitude: " + location.longitude
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
}