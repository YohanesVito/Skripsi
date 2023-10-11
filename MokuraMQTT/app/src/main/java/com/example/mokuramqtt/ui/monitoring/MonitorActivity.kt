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
import com.example.mokuramqtt.Constants.Companion.DURATION
import com.example.mokuramqtt.R
import com.example.mokuramqtt.ViewModelFactory
import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.database.MokuraMQTT
import com.example.mokuramqtt.databinding.ActivityMonitorBinding
import com.example.mokuramqtt.helper.DateHelper
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.ui.testing.DetailsHTTPActivity
import com.example.mokuramqtt.utils.Accelerometer
import com.example.mokuramqtt.utils.BluetoothService
import com.example.mokuramqtt.utils.Location
import com.example.mokuramqtt.viewmodel.MQTTViewModel
import com.example.mokuramqtt.viewmodel.MonitorViewModel


class MonitorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMonitorBinding
    private lateinit var monitorViewModel: MonitorViewModel
    private lateinit var mqttViewModel: MQTTViewModel
    private lateinit var mBluetoothService: BluetoothService
    private lateinit var mAccelerometer: Accelerometer
    private lateinit var mLocation: Location
    private lateinit var mAddress: String
    private lateinit var mName: String
    private lateinit var mHandler: Handler
    private lateinit var compass: String
    private lateinit var mUser: UserModel
    private var lat: String = ""
    private var lon: String = ""

    companion object{
        private var currentDegree= 0f
        const val EXTRA_ADDRESS: String = "Device Address"
        const val EXTRA_NAME: String = "Device Name"
        const val MAX_CAPACITY: Int = 10
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get bluetooth address and name
        mAddress = intent.getStringExtra(EXTRA_ADDRESS).toString()
        mName = intent.getStringExtra(EXTRA_NAME).toString()
        binding.tvHardwareIdVal?.text = mAddress

        setupView()
        setupViewModel()
        setupMQTT()
        setUpCompass()
        setUpLocation()
        setupAction()
        connectBluetooth2()

    }

    private fun setupMQTT() {
        mqttViewModel.connect(this)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            mqttViewModel.subscribe("mokura/user_response", mqttViewModel = mqttViewModel)
        }, 3000)
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
                    id_hardware = mUser.id_hardware,
                    id_user = mUser.id_user,
                    time_stamp = timeStamp,
                    speed = speed.toString(),
                    rpm = rpm.toString(),
                    battery = battery.toString(),
                    duty_cycle = dutyCycle,
                    compass = compass,
                    lat = lat,
                    lon = lon,
                    )

                val mqttData = MokuraMQTT(
                    id_hardware = newData.id_hardware,
                    id_user = newData.id_user,
                    time_stamp = newData.time_stamp,
                    speed = newData.speed,
                    rpm = newData.rpm,
                    battery = newData.battery,
                    duty_cycle = newData.duty_cycle,
                    compass = newData.compass,
                    lat = newData.lat,
                    lon = newData.lon,
                )
                //save data to HTTP DB -- TOGGLE THIS TO ENABLE/DISABLE
                monitorViewModel.saveDataHTTP(mUser,newData)

                //save data to MQTT DB -- TOGGLE THIS TO ENABLE/DISABLE
//                mqttViewModel.saveDataMQTT(mUser, mqttData)

                //update ui
                updateUI(newData)

                mArrayMokura.add(newData)
                if(mArrayMokura.size >= MAX_CAPACITY){

//                    //sent packet over HTTP -- TOGGLE THIS TO ENABLE/DISABLE
                    monitorViewModel.uploadData(mArrayMokura)
                    monitorViewModel.uploadDataNew(mArrayMokura)


//                    sent packet over MQTT -- TOGGLE THIS TO ENABLE/DISABLE
//                    mqttViewModel.publishArrayLogging(mArrayMokura)

                    //reset Array
                    mArrayMokura.clear()
                }

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

        binding.btDetails?.setOnClickListener {
            val intent = Intent(this, DetailsHTTPActivity::class.java)
            startActivity(intent)
        }

        binding.btDisconnect.setOnClickListener {
            disconnectBluetooth()
            mqttViewModel.unsubscribe("mokura/user_response")
            mqttViewModel.disconnect()
        }
    }


    private fun updateUI(newData: Mokura) {
        val rpm = newData.rpm.toDouble().toInt()
        val speed = newData.speed.toDouble().toInt()
        val battery = newData.battery.toDouble().toInt()
        val dutyCycle = newData.duty_cycle.toFloat()*10

        updateSpeed(speed)
        updateThrottle(dutyCycle.toInt())
        updateBattery(battery)
        updateRPM(rpm)
    }

    private fun updateThrottle(value: Int) {
        val throttle = binding.throttle
        throttle?.setSpeed(value, DURATION)
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
        rpm.setSpeed(i/100, DURATION)
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

        mqttViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[MQTTViewModel::class.java]

        monitorViewModel.getUser().observe(this){
            mUser = it
        }
    }
}