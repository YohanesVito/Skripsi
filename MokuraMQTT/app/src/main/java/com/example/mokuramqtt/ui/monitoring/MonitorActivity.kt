package com.example.mokuramqtt.ui.monitoring

import android.content.Intent
import android.opengl.Visibility
import android.os.*
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mokuramqtt.Constants
import com.example.mokuramqtt.ViewModelFactory
import com.example.mokuramqtt.databinding.ActivityMonitorBinding
import com.example.mokuramqtt.ui.monitoring.PairActivity.Companion.EXTRA_ADDRESS
import com.example.mokuramqtt.utils.BluetoothService
import com.example.mokuramqtt.viewmodel.MonitorViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MonitorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMonitorBinding
    private lateinit var monitorViewModel: MonitorViewModel
    private lateinit var mBluetoothService: BluetoothService
    private lateinit var mAddress: String
    private lateinit var mHandler: Handler

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitorBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //get bluetooth address
        mAddress = intent.getStringExtra(EXTRA_ADDRESS).toString()

        setupView()
        setupViewModel()
        connectBluetooth()

        monitorViewModel.dataBluetooth.observe(this){
            binding.tvLat.text = it.toString()
        }

        monitorViewModel.isLoading.observe(this){
            if (it){
                binding.progressBar.visibility = View.VISIBLE
            }
            else binding.progressBar.visibility = View.GONE
        }

        binding.btDisconnect.setOnClickListener {
            disconnectBluetooth()
        }
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