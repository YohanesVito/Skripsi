package com.example.mokuramqtt.ui.test

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mokuramqtt.ViewModelFactory
import com.example.mokuramqtt.database.MokuraNew
import com.example.mokuramqtt.databinding.ActivitySendDummyBinding
import com.example.mokuramqtt.helper.DateHelper
import com.example.mokuramqtt.model.Result
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.services.DataRepository
import com.example.mokuramqtt.services.StreamingService
import com.example.mokuramqtt.utils.Location
import com.example.mokuramqtt.viewmodel.DummyViewModel
import com.example.mokuramqtt.viewmodel.MonitorViewModel

class SendDummyActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySendDummyBinding
    private lateinit var dummyViewModel: DummyViewModel
    private lateinit var monitorViewModel: MonitorViewModel
    private lateinit var mLocation: Location
    private lateinit var user: UserModel
    private var lat: String = ""
    private var lon: String = ""
    private var isStreaming = false
    private val CHANNEL_ID = "my_channel_id"
    private lateinit var dataRepository: DataRepository

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySendDummyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setUpLocation()
        setupAction()
        setupChannel()

        // Initialize DataRepository
        dataRepository = DataRepository(applicationContext)

        // Observe data location changes
        dataRepository.dataLocation.observe(this) { location ->
            // Handle data changes here
            val lat = location[0].toString()
            val lon = location[1].toString()
            val time = DateHelper.getCurrentDate()

            // Create a new MokuraNew object with the received lat and lon
            val newMokura = MokuraNew(
                id = "bf49543d-d4d9-4fd9-bb24-2179fca3f713",
                name = "Mokura 2###$time",
                speed = 50,
                battery = 60,
                throtle = 20,
                lat = lat,
                long = lon,
                status = 1,
            )

            // Start streaming
            dummyViewModel.sendDummy(newMokura).observe(this) {
                when (it) {
                    is Result.Loading -> Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                    is Result.Success -> Toast.makeText(this, "Data Terkirim", Toast.LENGTH_SHORT).show()
                    is Result.Error -> Toast.makeText(this, "Gagal Kirim Data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Streaming Data"
            val descriptionText = "Streaming in progress"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }

    private fun setupAction() {
        binding.btStart.setOnClickListener {
            if (isStreaming) {
                stopStreaming()
            } else {
                startStreaming()
            }
        }
    }

    private fun setupViewModel() {
        dummyViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[DummyViewModel::class.java]

        monitorViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[MonitorViewModel::class.java]

        dummyViewModel.getUser().observe(this) { user ->
            this.user = user
        }
    }

    private fun updateLocation(data: Array<Double>) {
        lat = data[0].toString()
        lon = data[1].toString()
        binding.tvLat.text = lat
        binding.tvLon.text = lon
    }

    private fun setUpLocation() {
        mLocation = Location(this,monitorViewModel)
        mLocation.setUpLocation()
    }

    private fun startStreaming2() {
        isStreaming = true
        binding.btStart.text = "Stop Streaming"
        monitorViewModel.dataLocation.observe(this) { location ->
            updateLocation(location)
            val newMokura = MokuraNew(
                id = "bf49543d-d4d9-4fd9-bb24-2179fca3f713",
                name = "Mokura 2",
                speed = 50,
                battery = 60,
                throtle = 20,
                lat = lat,
                long = lon,
                status = 1,
            )
            //start streaming
            dummyViewModel.sendDummy(newMokura).observe(this){
                when(it) {
                    is Result.Loading -> Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()

                    is Result.Success -> {
                        Toast.makeText(this, "Data Terkirim", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Error -> Toast.makeText(this, "Gagal Kirim Data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
//
//    private fun stopStreaming2() {
//        isStreaming = false
//        binding.btStart.text = "Start Streaming"
//        monitorViewModel.dataLocation.removeObservers(this)
//        Toast.makeText(this, "Stream Stopped", Toast.LENGTH_SHORT).show()
//    }

    private fun startStreaming() {
        isStreaming = true
        binding.btStart.text = "Stop Streaming"

        val serviceIntent = Intent(this, StreamingService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopStreaming() {
        isStreaming = false
        binding.btStart.text = "Start Streaming"

        val serviceIntent = Intent(this, StreamingService::class.java)
        stopService(serviceIntent)
        Toast.makeText(this, "Stream Stopped", Toast.LENGTH_SHORT).show()
    }



}