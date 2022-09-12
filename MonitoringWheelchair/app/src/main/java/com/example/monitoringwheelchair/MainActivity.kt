package com.example.monitoringwheelchair

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.monitoringwheelchair.bluetooth.PairedBluetoothAdapter
import com.example.monitoringwheelchair.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pairedDevices: Set<BluetoothDevice>
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    companion object{
        val EXTRA_ADDRESS: String = "Device"
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bluetoothManager= getSystemService(BluetoothManager::class.java)
        bluetoothAdapter= bluetoothManager.adapter
//        Log.d("bt",bluetoothAdapter.toString())

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
        binding.rvBt.setHasFixedSize(true)
        pairedDeviceList()

        binding.btRefresh.setOnClickListener {
//            Log.d("clik","tenan")
            Toast.makeText(this,"tol", Toast.LENGTH_SHORT).show()
            pairedDeviceList()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun pairedDeviceList(){
//        Log.d("masuk","tenan")

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
    private fun showDetailGithubUser(data: BluetoothDevice) {
        val intent = Intent(this,MonitorActivity::class.java)
        intent.putExtra(EXTRA_ADDRESS, data.address)
        startActivity(intent)
    }
    private fun showRecylerList(list: ArrayList<BluetoothDevice>) {
        binding.rvBt.layoutManager = GridLayoutManager(this, 2)

        val listPairedDevices = PairedBluetoothAdapter(this,list)
        binding.rvBt.adapter = listPairedDevices

        listPairedDevices.setOnItemClickCallback(object : PairedBluetoothAdapter.OnItemClickCallback {
            override fun onItemClicked(data: BluetoothDevice) {
                showDetailGithubUser(data)
            }
        })
    }
}