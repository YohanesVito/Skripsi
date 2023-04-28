package com.example.mokuramqtt.ui.monitoring

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mokuramqtt.Constants
import com.example.mokuramqtt.databinding.ActivityPairBinding

class PairActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPairBinding
    private lateinit var pairedDevices: Set<BluetoothDevice>
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    companion object{
        const val EXTRA_ADDRESS: String = "Device Address"
        const val EXTRA_NAME: String = "Device Name"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPairBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupBluetooth()

        binding.rvBt.setHasFixedSize(true)
        pairedDeviceList()

        binding.btRefresh.setOnClickListener {
            pairedDeviceList()
        }
    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupBluetooth() {
        bluetoothManager= getSystemService(BluetoothManager::class.java)
        bluetoothAdapter= bluetoothManager.adapter

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    Constants.REQUEST_ENABLE_BLUETOOTH
                )
            }
            startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BLUETOOTH)
        }
    }


    private fun pairedDeviceList(){

        val list: ArrayList<BluetoothDevice> = ArrayList()

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                Constants.REQUEST_ENABLE_BLUETOOTH
            )
        }
        pairedDevices = bluetoothAdapter.bondedDevices
        pairedDevices.forEach { device ->
            list.add(device)
        }
        showRecylerList(list)

    }

    private fun showMonitoring(data: BluetoothDevice) {

        val intent = Intent(this,MonitorActivity::class.java)

        //send bluetooth address and bluetooth name
        intent.putExtra(EXTRA_ADDRESS, data.address)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        intent.putExtra(EXTRA_NAME, data.name)

        startActivity(intent)
    }

    private fun showRecylerList(list: ArrayList<BluetoothDevice>) {
        binding.rvBt.layoutManager = LinearLayoutManager(this)

        val listPairedDevices = PairAdapter(this,list)
        binding.rvBt.adapter = listPairedDevices

        listPairedDevices.setOnItemClickCallback(object : PairAdapter.OnItemClickCallback {
            override fun onItemClicked(data: BluetoothDevice) {
                loadingUI()
                showMonitoring(data)
            }
        })
    }


    private fun loadingUI(){
        binding.progressBar.visibility = View.VISIBLE
        Toast.makeText(this,"Connecting...",Toast.LENGTH_SHORT).show()
        binding.progressBar.visibility = View.GONE
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
}