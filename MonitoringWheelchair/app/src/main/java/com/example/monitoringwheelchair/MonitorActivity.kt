package com.example.monitoringwheelchair

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.lifecycle.ViewModelProvider
import com.example.monitoringwheelchair.Constants.Companion.TIMER_UPDATED
import com.example.monitoringwheelchair.Constants.Companion.TIME_EXTRA
import com.example.monitoringwheelchair.bluetooth.ConnectToDevice
import com.example.monitoringwheelchair.compass.Compass
import com.example.monitoringwheelchair.databinding.ActivityMonitorBinding
import com.example.monitoringwheelchair.gyroscope.Gyroscope
import com.example.monitoringwheelchair.gyroscope.GyroscopeData
import com.example.monitoringwheelchair.location.Location
import com.example.monitoringwheelchair.time.TimerService
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class MonitorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMonitorBinding
    private lateinit var serviceIntent: Intent
    lateinit var viewModel: MainViewModel
    lateinit var m_address: String
    private var time = 0.0
    private var timerStarted = false

    companion object{
        const val duration = 200L
        private var currentDegree= 0f
        private lateinit var mHandler: Handler
        val EXTRA_ADDRESS: String = "Device"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        m_address = intent.getStringExtra(EXTRA_ADDRESS).toString()
        mHandler = object:Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    Constants.MESSAGE_READ -> {
                        // construct a string from the valid bytes in the buffer
                        val readBuf = msg.obj as ByteArray
                        // construct a string from the valid bytes in the buffer
                        val readMessage = String(readBuf, 0, msg.arg1)
                        Log.d("msghandler",readMessage)
                        viewModel.dataSpeed.value = readMessage.toInt()
                    }
                }
            }
        }
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        ConnectToDevice(this,m_address, mHandler).execute()
        Compass(this,viewModel).setUpCompass()
        Location(this,viewModel).setUpLocation()

//        Gyroscope(this,viewModel).setUpGyroSensor()
//        viewModel.dataGyroscope.observe(this){
//            updateGyroscope(it)
//        }

        viewModel.dataSpeed.observe(this) {
            updateSpeed(it, duration)
        }

        viewModel.dataCompass.observe(this){
            updateCompass(it)
        }

        viewModel.dataLocation.observe(this) {
            updateLocation(it)
        }

//        viewModel.dataDummy.observe(this) {
//            updateSpeed(it["speed"]!!, duration)
//            updateRPM(it["rpm"]!!, duration)
//            Log.d("viw",it["speed"].toString())
//        }

        serviceIntent = Intent(applicationContext,TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TIMER_UPDATED))

        binding.btDisconnect.setOnClickListener {
            ConnectToDevice(this,m_address, mHandler).disconnect()
            stopTimer()
            super.onBackPressed()
        }

        startTimer()

        binding.speedometer.setOnClickListener{
            val randomSpeed = (0..70).random()
            updateSpeed(randomSpeed, duration)
        }

        binding.rpm.setOnClickListener{
            val randomRPM = (0..10).random()
            updateRPM(randomRPM, duration)
        }

        binding.ivBattery.setOnClickListener{
            val randomBatt = (0..100).random()
            updateBatt(randomBatt)
            binding.tvBatteryVal.text = "$randomBatt%"
        }

    }

    private fun updateBatt(percentage: Int) {

        if (percentage<=25){
            binding.ivBattery.setImageResource(R.drawable.batt1)
        }
        else if (percentage<=50){
            binding.ivBattery.setImageResource(R.drawable.batt2)
        }
        else if(percentage<=75){
            binding.ivBattery.setImageResource(R.drawable.batt3)
        }
        else if(percentage<=100){
            binding.ivBattery.setImageResource(R.drawable.batt4)
        }
    }

    private fun updateRPM(i: Int, duration: Long) {
        val rpm = binding.rpm
        rpm.setSpeed(i,duration)
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                time = intent.getDoubleExtra(TIME_EXTRA,0.0)
            }
            binding.tvTimer.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours,minutes,seconds)
    }

    private fun makeTimeString(hour: Int, min: Int, sec: Int): String {
        return String.format("%02d:%02d:%02d", hour, min, sec)
    }

    private fun updateLocation(data: Array<Double>) {
        binding.tvLatVal.text = "%.${3}f".format(data[0])
        binding.tvLonVal.text = "%.${3}f".format(data[1])
        Log.i("tttt",data[0].toString())
        Log.i("lon",data[1].toString())
    }
    private fun updateCompass(degree: Int) {
        val rotateAnimation = RotateAnimation(
            currentDegree,
            (-degree).toFloat(),
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotateAnimation.duration = 210
        rotateAnimation.fillAfter = true

        binding.ivCompass.startAnimation(rotateAnimation)
        currentDegree = (-degree).toFloat()

        binding.tvCompassVal.text = "$currentDegree°"
    }

    private fun updateSpeed(s: Int, duration: Long) {
        val speedometer = binding.speedometer
        speedometer.setSpeed(s,duration)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ConnectToDevice(this,m_address, mHandler).disconnect()
        stopTimer()
    }

    private fun startTimer(){
        serviceIntent.putExtra(TIME_EXTRA,time)
        startService(serviceIntent)
        timerStarted = true
    }

    private fun stopTimer(){
        stopService(serviceIntent)
        time = 0.0
        binding.tvTimer.text = getTimeStringFromDouble(time)
        timerStarted = false
    }

//    private fun updateGyroscope(data: GyroscopeData){
//        with(binding.tvGyrovalue){
//            rotation = data.rotation
//            rotationX = data.rotationX
//            rotationY = data.rotationY
//            translationX = data.translationX
//            translationY = data.translationY
//            setBackgroundColor(data.color)
//            text = data.text
//        }
//
//    }
}