package com.example.monitoringwheelchair

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.monitoringwheelchair.Constants.Companion.DURATION
import com.example.monitoringwheelchair.Constants.Companion.TIMER_UPDATED
import com.example.monitoringwheelchair.Constants.Companion.TIME_EXTRA
import com.example.monitoringwheelchair.bluetooth.ConnectToDevice
import com.example.monitoringwheelchair.compass.Compass
import com.example.monitoringwheelchair.databinding.ActivityMonitorBinding
import com.example.monitoringwheelchair.gyroscope.Gyroscope
import com.example.monitoringwheelchair.gyroscope.GyroscopeData
import com.example.monitoringwheelchair.location.Location
import com.example.monitoringwheelchair.logging.Data
import com.example.monitoringwheelchair.logging.LoggingData
import com.example.monitoringwheelchair.time.TimerService
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class MonitorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMonitorBinding
    private lateinit var serviceIntent: Intent
    lateinit var viewModel: MainViewModel
    lateinit var m_address: String
    private var time = 0.0
    private var timerStarted = false
    private var compass: String? = null
    private var lat: String? = null
    private var lon: String? = null

    companion object{
        private var currentDegree= 0f
        private lateinit var mHandler: Handler
        val EXTRA_ADDRESS: String = "Device"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        //create file
        initiateLoggingFile()

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
                        viewModel.dataBluetooth.value = readMessage
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

        viewModel.dataBluetooth.observe(this) {
            val arrayData: List<String> = it.split(";")
            val penanda = arrayData[0][0]
            val rpm = arrayData[0].drop(1).toFloat()
            val speed = arrayData[1].toFloat()
            val battery = arrayData[2].toFloat()
            val dutyCycle = arrayData[3].dropLast(1)
            val timeStamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString()

            val newData = Data(timeStamp,speed.toString(), rpm.toString(), battery.toString(), dutyCycle, compass, lat, lon)
            LoggingData(newData).logData()
            updateData(it)
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
            updateSpeed(randomSpeed)
        }

        binding.rpm.setOnClickListener{
            val randomRPM = (0..10).random()
            updateRPM(randomRPM)
        }

        binding.ivBattery.setOnClickListener{
            val randomBatt = (0..100).random()
            updateBatt(randomBatt)
            binding.tvBatteryVal.text = "$randomBatt%"
        }

        binding.btTest.setOnClickListener {
            val timeStamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString()
            val randomSpeed = (0..70).random()

            val randomRPM = (0..10).random()

            val randomBatt = (0..100).random()

            updateSpeed(randomSpeed)
            updateRPM(randomRPM)
            updateBatt(randomBatt)
            binding.tvBatteryVal.text = "$randomBatt%"

            val data = Data(timeStamp,randomSpeed.toString(),randomRPM.toString(),randomBatt.toString())

            LoggingData(data).logData()
        }

    }

    private fun initiateLoggingFile() {
        //create directory in folder ABA
        val folder = Environment.getExternalStorageDirectory().toString()
        val f = File(folder,"MokuraLoggingData")
        f.mkdir()

        ///storage/emulated/0/MokuraLoggingData/Logging1.csv
        val fileName = Environment.getExternalStorageDirectory().toString() + "/MokuraLoggingData/Logging1.csv"
        Log.d("path","$fileName")

        //create header csv
        val header = listOf("timeStamp","speed","rpm","battery","compass","dutyCycle","lat","lon")
        csvWriter().open(File(fileName)) {
            writeRow(header)
        }

    }

    private fun updateData(stringData: String?) {
        val arrayData: List<String>? = stringData?.split(";")
        val penanda = arrayData?.get(0)?.get(0)
        val rpm = arrayData?.get(0)?.drop(1)?.toFloat()?.div(100)
        val speed = arrayData?.get(1)?.toFloat()
        val battery = arrayData?.get(2)?.toFloat()
        val dutyCycle = arrayData?.get(3)?.dropLast(1)

        if (penanda != null) {
            if (penanda.hashCode() == 0x02 ){
                Log.d("Data: ","valid")
            }else{
                Log.d("Data: ","invalid")
            }
        }

        if (speed != null) {
            updateSpeed(speed.toInt())
        }
        if (rpm != null) {
            updateRPM(rpm.toInt())
        }
        if (battery != null) {
            updateBatt(battery.toInt())
        }
    }

    private fun updateBatt(percentage: Int) {
        binding.tvBatteryVal.text = "$percentage%"
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

    private fun updateRPM(i: Int) {
        val rpm = binding.rpm
        rpm.setSpeed(i, DURATION)
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
        lat = "%.${3}f".format(data[0])
        lon = "%.${3}f".format(data[1])
        binding.tvLatVal.text = lat
        binding.tvLonVal.text = lon
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

        compass = currentDegree.toString()
        binding.tvCompassVal.text = compass
    }

    private fun updateSpeed(s: Int) {
        val speedometer = binding.speedometer
        speedometer.setSpeed(s, DURATION)
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