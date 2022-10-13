package com.example.monitoringwheelchair.bluetooth

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.monitoringwheelchair.Constants
import com.example.monitoringwheelchair.Constants.Companion.MESSAGE_READ
import com.example.monitoringwheelchair.Constants.Companion.mUUID
import java.io.IOException
import java.io.InputStream

class ConnectToDevice(c: Context, m_address: String, mHandler: Handler): AsyncTask<Void, Void, String>(){
    private var connectSuccess: Boolean = true
    lateinit var m_progress: ProgressDialog
    var m_bluetoothSocket: BluetoothSocket? = null
    lateinit var m_bluetoothAdapter: BluetoothAdapter
    var m_isConnected: Boolean = false
    private val context: Context
    private val m_address: String
    private val mHandler: Handler

    init {
        this.context = c
        this.m_address = m_address
        this.mHandler = mHandler
    }

    @Deprecated("Deprecated in Java")
    override fun onPreExecute() {
        super.onPreExecute()
        m_progress = ProgressDialog.show(context,"Connecting...","Please Wait")
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void?): String? {
        try{
            if (m_bluetoothSocket==null || !m_isConnected){
                m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                if ((ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(
                        context as Activity, arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                        Constants.REQUEST_ENABLE_BLUETOOTH
                    )
                }
                m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(mUUID)
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                m_bluetoothSocket!!.connect()

                ConnectedThread(m_bluetoothSocket!!).start()

            }
        }catch (e: IOException){
            connectSuccess = false
            e.printStackTrace()
        }
        return null
    }

    @Deprecated("Deprecated in Java")
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (!connectSuccess){
            Log.d("data","can't connect")
        }else{
            m_isConnected = true
        }
        m_progress.dismiss()

    }

    fun disconnect(){
        if (m_bluetoothSocket!=null){
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private inner class ConnectedThread(mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmBuffer: ByteArray = ByteArray(25) // mmBuffer store for the stream

        override fun run() {
            Log.d("msgtol", "hhee")
            while (true) {
                val numBytes: Int = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d("msgtol", e.printStackTrace().toString())
                }
                val readMsg = mHandler.obtainMessage(
                    MESSAGE_READ, numBytes, -1,
                    mmBuffer
                )
                readMsg.sendToTarget()
            }
        }

    }
}