package com.example.monitoringwheelchair

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.monitoringwheelchair.bluetooth.ConnectToDevice
import com.example.monitoringwheelchair.databinding.ActivityMonitorBinding
import com.example.monitoringwheelchair.gyroscope.Gyroscope
import com.example.monitoringwheelchair.gyroscope.GyroscopeData

class MonitorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMonitorBinding
    lateinit var viewModel: MainViewModel
    lateinit var m_address: String

    companion object{
        val duration = 200L
        private lateinit var mHandler: Handler
        val EXTRA_ADDRESS: String = "Device"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitorBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                        viewModel.currentRandom.value = readMessage.toInt()
                    }
                }
            }
        }
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        ConnectToDevice(this,m_address, mHandler).execute()
        Gyroscope(this,viewModel).setUpGyroSensor()

        viewModel.currentRandom.observe(this) {
            getSpeed(it, duration)
        }

        viewModel.dataGyroscope.observe(this){
            updateGyroscope(it)
        }
    }

    private fun getSpeed(s: Int, duration: Long) {
        binding.tvSpeed.text = s.toString()
        val speedometer = binding.speedometer
        speedometer.setSpeed(s,duration)
    }

    override fun onBackPressed() {
        ConnectToDevice(this,m_address, mHandler).disconnect()
    }

    private fun updateGyroscope(data: GyroscopeData){
        with(binding.tvGyrovalue){
            rotation = data.rotation
            rotationX = data.rotationX
            rotationY = data.rotationY
            translationX = data.translationX
            translationY = data.translationY
            setBackgroundColor(data.color)
            text = data.text
        }

    }
}