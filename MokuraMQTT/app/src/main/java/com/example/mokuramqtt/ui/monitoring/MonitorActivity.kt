package com.example.mokuramqtt.ui.monitoring

import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mokuramqtt.Constants
import com.example.mokuramqtt.ViewModelFactory
import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.databinding.ActivityMonitorBinding
import com.example.mokuramqtt.helper.DateHelper
import com.example.mokuramqtt.utils.Accelerometer
import com.example.mokuramqtt.utils.BluetoothService
import com.example.mokuramqtt.utils.Location
import com.example.mokuramqtt.viewmodel.MonitorViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MonitorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMonitorBinding
    private lateinit var monitorViewModel: MonitorViewModel
    private lateinit var mBluetoothService: BluetoothService
    private lateinit var mAddress: String
    private lateinit var mHandler: Handler
    private lateinit var compass: String
    private var lat: String = ""
    private var lon: String = ""

    companion object{
        private var currentDegree= 0f
        val EXTRA_ADDRESS: String = "Device"
        val MAX_CAPACITY: Int = 20
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get bluetooth address
        mAddress = intent.getStringExtra(EXTRA_ADDRESS).toString()

        setupView()
        setupViewModel()
        setUpCompass()
        setUpLocation()
        setupAction()
        connectBluetooth()

        binding.btDisconnect.setOnClickListener {
            disconnectBluetooth()
        }
    }

    private fun setUpCompass() {
        Accelerometer(this, monitorViewModel).setUpCompass()
    }

    private fun setUpLocation() {
        Location(this, monitorViewModel).setUpLocation()
    }

    private fun setupAction() {
        //insert hardware
        monitorViewModel.saveHardware(mAddress)

        //observe bluetooth message
        monitorViewModel.dataBluetooth.observe(this) { bluetoothMessage ->

            if (bluetoothMessage.endsWith("#") && bluetoothMessage.startsWith("$")) {

                val arrayData: List<String> = bluetoothMessage.split(";")
                val rpm = arrayData[0].drop(1).toFloat()
                val speed = arrayData[1].toFloat()
                val battery = arrayData[2].toFloat()
                val dutyCycle = arrayData[3].filterNot { it == '#' }

//                val timeStamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now()).toString()
                val timeStamp = DateHelper.getCurrentDate()

                val newData = Mokura(
                    timeStamp = timeStamp,
                    speed = speed.toString(),
                    rpm = rpm.toString(),
                    battery = battery.toString(),
                    dutyCycle = dutyCycle,
                    compass = compass,
                    lat = lat,
                    lon = lon,
                    )
                monitorViewModel.saveData(newData)
                monitorViewModel.valArrayLogging.add(newData)
                monitorViewModel.arrayLogging.value = monitorViewModel.valArrayLogging
            }
        }

        monitorViewModel.dataCompass.observe(this){
            updateCompass(it)
        }

        monitorViewModel.dataLocation.observe(this) {
            updateLocation(it)
        }

        //observe loading
        monitorViewModel.isLoading.observe(this){
            if (it){
                binding.progressBar.visibility = View.VISIBLE
            }
            else binding.progressBar.visibility = View.GONE
        }

        monitorViewModel.arrayLogging.observe(this){
            if(it.size>=MAX_CAPACITY){
                monitorViewModel.uploadData()
            }
            monitorViewModel.valArrayLogging.clear()
        }

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

    private fun updateLocation(data: Array<Double>) {
        lat = "%.${3}f".format(data[0])
        lon = "%.${3}f".format(data[1])
        binding.tvLatVal.text = lat
        binding.tvLonVal.text = lon
    }

    override fun onBackPressed() {
        Toast.makeText(this,"Tekan tombol Disconnect untuk kembali",Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun connectBluetooth() {
        mHandler = object: Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    Constants.MESSAGE_READ -> {
                        // construct a string from the valid bytes in the buffer
                        val readBuf = msg.obj as ByteArray
                        // construct a string from the valid bytes in the buffer
                        val readMessage = String(readBuf, 0, msg.arg1)
                        Log.d("msghandler",readMessage)
                        monitorViewModel.dataBluetooth.value = readMessage
                    }
                    Constants.BLUETOOTH_LOADING -> {
                        monitorViewModel.isLoading.value = true
                    }
                    Constants.BLUETOOTH_NOT_LOADING -> {
                        monitorViewModel.isLoading.value = false
                    }
                }
            }
        }
        mBluetoothService = BluetoothService(this,mHandler,mAddress)
        mBluetoothService.run()
    }

    private fun disconnectBluetooth(){
        mBluetoothService.disconnect()
        val executorService: ExecutorService = Executors.newSingleThreadExecutor()
        val bService = executorService.submit(mBluetoothService)
        bService.cancel(true)
        startActivity(Intent(this,PairActivity::class.java))
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        monitorViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[MonitorViewModel::class.java]
    }
}