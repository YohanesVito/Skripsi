package com.example.mokuramqtt.ui.monitoring

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mokuramqtt.Constants
import com.example.mokuramqtt.Constants.Companion.DURATION
import com.example.mokuramqtt.Constants.Companion.REQUEST_CODE_PERMISSIONS
import com.example.mokuramqtt.Constants.Companion.REQUIRED_PERMISSIONS
import com.example.mokuramqtt.R
import com.example.mokuramqtt.ViewModelFactory
import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.databinding.ActivityMonitorBinding
import com.example.mokuramqtt.helper.DateHelper
import com.example.mokuramqtt.utils.Accelerometer
import com.example.mokuramqtt.utils.BluetoothService
import com.example.mokuramqtt.utils.Location
import com.example.mokuramqtt.viewmodel.MonitorViewModel

class MonitorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMonitorBinding
    private lateinit var monitorViewModel: MonitorViewModel
    private lateinit var mBluetoothService: BluetoothService
    private lateinit var mAccelerometer: Accelerometer
    private lateinit var mLocation: Location
    private lateinit var mAddress: String
    private lateinit var mName: String
    private lateinit var mHandler: Handler
    private lateinit var compass: String
    private var lat: String = ""
    private var lon: String = ""

    companion object{
        private var currentDegree= 0f
        const val EXTRA_ADDRESS: String = "Device Address"
        const val EXTRA_NAME: String = "Device Name"
        const val MAX_CAPACITY: Int = 20
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get bluetooth address and name
        mAddress = "B4:9D:02:8D:76:9C"
        mName = intent.getStringExtra(EXTRA_NAME).toString()

        setupView()
        setupViewModel()
        setUpCompass()
        setUpLocation()
        setupAction()
        connectBluetooth2()

        binding.btDisconnect.setOnClickListener {
            disconnectBluetooth()
        }
    }

    private fun setUpCompass() {
        mAccelerometer = Accelerometer(this, monitorViewModel)
        mAccelerometer.setUpCompass()
    }

    private fun setUpLocation() {
        mLocation = Location(this,monitorViewModel)
        mLocation.setUpLocation()
    }

    private fun setupAction() {
        val mArrayMokura = ArrayList<Mokura>()
        //insert hardware to SP and post to cloud
        monitorViewModel.saveHardware(mAddress,mName)

        //observe bluetooth message
        monitorViewModel.dataBluetooth.observe(this) { bluetoothMessage ->

            if (bluetoothMessage.endsWith("#") && bluetoothMessage.startsWith("$")) {

                val arrayData: List<String> = bluetoothMessage.split(";")
                val rpm: Float
                val speed: Float
                val battery: Float
                val dutyCycle: String
                try {
                    rpm = arrayData[0].drop(1).toFloat()
                    speed = arrayData[1].toFloat()
                    battery = arrayData[2].toFloat()
                    dutyCycle = arrayData[3].filterNot { it == '#' }
                } catch (e: NumberFormatException) {
                    // Handle invalid data
                    return@observe
                }

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
                //save data to db
//                monitorViewModel.saveData(newData)

                //logging data to csv
                updateUI(newData)
                mArrayMokura.add(newData)
                Log.d("maray size",mArrayMokura.size.toString())
                if(mArrayMokura.size >= MAX_CAPACITY){
                    monitorViewModel.arrayLogging.value = mArrayMokura
                    Log.d("LOGGING",monitorViewModel.arrayLogging.value.toString())
                    mArrayMokura.clear()
                }

            }
        }

        //logging data to cloud using http
        monitorViewModel.arrayLogging.observe(this){
            Log.d("size",it.size.toString())
            monitorViewModel.uploadData()

            //csv writer
            Log.d("arraylogging",it.toString())
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // If the app doesn't have permission, request it from the user
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSIONS)
                return@observe
            }
            if (ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            } else {
                // Permission already granted, proceed with writing to external storage
//                monitorViewModel.writeToCSV()
            }

        }

        //update UI
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

    }

    private fun updateUI(newData: Mokura) {
        val rpm = newData.rpm.toDouble().toInt()
        val speed = newData.speed.toDouble().toInt()
        val battery = newData.battery.toDouble().toInt()
        val dutyCycle = newData.dutyCycle.toDouble().toInt()

        updateSpeed(speed)
        updateRPM(dutyCycle)
        updateBattery(battery)
        updateRPM(rpm)
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

    private fun updateSpeed(s: Int) {
        val speedometer = binding.speedometer
        speedometer.setSpeed(s, DURATION)
    }

    private fun updateRPM(i: Int) {
        val rpm = binding.rpm
        rpm.setSpeed(i, DURATION)
    }

    private fun updateBattery(percentage: Int) {
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

    override fun onBackPressed() {
        Toast.makeText(this,"Tekan tombol Disconnect untuk kembali",Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun connectBluetooth2() {
        // Create a new thread to start the BluetoothService
        Thread {
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
        }.start()
    }

    private fun disconnectBluetooth(){
        mBluetoothService.disconnect(object : BluetoothService.DisconnectListener {
            override fun onDisconnected() {
                startActivity(Intent(this@MonitorActivity, PairActivity::class.java))
                finish() // optional, depending on your use case
            }
        })
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