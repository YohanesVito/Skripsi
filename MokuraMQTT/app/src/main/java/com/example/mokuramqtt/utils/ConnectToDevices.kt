package com.example.mokuramqtt.utils

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
import com.example.mokuramqtt.Constants
import com.example.mokuramqtt.Constants.Companion.MESSAGE_READ
import com.example.mokuramqtt.Constants.Companion.mUUID
import java.io.IOException
import java.io.InputStream

class ConnectToDevice(c: Context, m_address: String, mHandler: Handler): AsyncTask<Void, Void, String>(){
    private var connectSuccess: Boolean = true
    private lateinit var mProgress: ProgressDialog
    private lateinit var mBluetoothSocket: BluetoothSocket
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private var mIsConnected: Boolean = false

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
        mProgress = ProgressDialog.show(context,"Connecting...","Please Wait")
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg params: Void?): String? {
        try{
            if (!mIsConnected){
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                val device: BluetoothDevice = mBluetoothAdapter.getRemoteDevice(m_address)
                if ((ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(
                        context as Activity, arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                        Constants.REQUEST_ENABLE_BLUETOOTH
                    )
                }
                mBluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(mUUID)
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                mBluetoothSocket.connect()

                ConnectedThread(mBluetoothSocket).start()
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
            mIsConnected = true
        }
        mProgress.dismiss()

    }

    fun disconnect(){
        try {
            mBluetoothSocket.close()
            mIsConnected = false
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

    private inner class ConnectedThread(mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmBuffer: ByteArray = ByteArray(25) // mmBuffer store for the stream

        override fun run() {
            while (true) {
                val numBytes: Int = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d("Error", e.printStackTrace().toString())
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