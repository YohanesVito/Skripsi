package com.example.speedometer.bluetooth

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.core.app.ActivityCompat
import com.example.speedometer.databinding.ActivityBluetoothBinding

class BluetoothActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBluetoothBinding
    private lateinit var bA: BluetoothAdapter
    private lateinit var pairedDevices: Set<BluetoothDevice>
    companion object{
        val REQUEST_ENABLE_BLUETOOTH = 1
        val EXTRA_ADDRESS: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBluetoothBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bA = BluetoothAdapter.getDefaultAdapter()

        if (bA==null){
            Toast.makeText(this,"Bluetooth not supported",Toast.LENGTH_SHORT).show()
            finish()
        }

        if (!bA.isEnabled) {
            val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        binding.btRefresh.setOnClickListener {
            pairedDeviceList()
        }
    }
    private fun pairedDeviceList(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        pairedDevices - bA.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()

        if (pairedDevices.isEmpty()){
            for (device: BluetoothDevice in pairedDevices){
                list.add(device)
                Log.i("service",""+device.toString())
            }
        } else{
            Toast.makeText(this, "no paired devices found", Toast.LENGTH_SHORT).show()
        }
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,list)
        binding.listBt.adapter = adapter
        binding.listBt.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address
            val intent = Intent(this,ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            startActivity(intent)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH){
            if (resultCode == Activity.RESULT_OK){
                if (bA.isEnabled){
                    Toast.makeText(this,"Bluetooth has been enabled",Toast.LENGTH_SHORT).show()
                }else Toast.makeText(this,"Bluetooth has been disabled",Toast.LENGTH_SHORT).show()

            } else if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this,"Bluetooth enabling has been canceled",Toast.LENGTH_SHORT).show()
            }
        }
    }
}