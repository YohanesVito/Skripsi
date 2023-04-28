package com.example.mokuramqtt.utils

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mokuramqtt.Constants
import com.example.mokuramqtt.Constants.Companion.BLUETOOTH_LOADING
import com.example.mokuramqtt.Constants.Companion.BLUETOOTH_NOT_LOADING
import com.example.mokuramqtt.Constants.Companion.MESSAGE_READ
import com.example.mokuramqtt.Constants.Companion.mUUID
import java.io.IOException
import java.io.InputStream

private const val TAG = "BLUETOOTH_SERVICES"

class BluetoothService(
    // handler that gets info from Bluetooth service
    private val mContext: Context,
    private val mHandler: Handler,
    private val mAddress: String,

): Runnable {
    private lateinit var mBluetoothSocket: BluetoothSocket
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mConnectedThread: ConnectedThread
    private var mIsConnected: Boolean = false


    @RequiresApi(Build.VERSION_CODES.S)
    override fun run() {

        val readMsg = mHandler.obtainMessage(BLUETOOTH_LOADING)
        readMsg.sendToTarget()

        try{
            if (!mIsConnected){
                val bluetoothManager = mContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                mBluetoothAdapter = bluetoothManager.adapter
                val device: BluetoothDevice = mBluetoothAdapter.getRemoteDevice(mAddress)
                if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(
                        mContext as Activity, arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                        Constants.REQUEST_ENABLE_BLUETOOTH
                    )
                }
                mBluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(mUUID)
                mBluetoothAdapter.cancelDiscovery()
                mBluetoothSocket.connect()

                mConnectedThread = ConnectedThread(mBluetoothSocket)
                mConnectedThread.start()

                mIsConnected = true

                val msgHandler = mHandler.obtainMessage(BLUETOOTH_NOT_LOADING)
                msgHandler.sendToTarget()


            }
        }catch (e: IOException){
            mIsConnected = false
            e.printStackTrace()
        }
    }

    interface DisconnectListener {
        fun onDisconnected()
    }

    fun disconnect(listener: DisconnectListener) {
        // disconnect logic here
        if (mIsConnected) {
            try {
                mConnectedThread.cancel()
                mBluetoothSocket.close()
                mIsConnected = false
            } catch (e: IOException) {
                Log.e(TAG, "Error closing socket", e)
            }
        }

        listener.onDisconnected()
    }

    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmBuffer: ByteArray = ByteArray(25) // mmBuffer store for the stream

        override fun run() {
            var numBytes: Int // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.e(TAG, "Error reading from input stream", e)
                    break
                }

                // Send the obtained bytes to the UI activity.
                val readMsg = mHandler.obtainMessage(
                    MESSAGE_READ, numBytes, -1,
                    mmBuffer)

                readMsg.sendToTarget()
            }
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            if (mIsConnected) {
                try {
                    mmSocket.close()
                    mBluetoothSocket.close()
                    mIsConnected = false
                } catch (e: IOException) {
                    Log.e(TAG, "Error closing socket", e)
                }
            }
        }
    }
}

