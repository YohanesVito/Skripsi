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
import java.io.OutputStream

private const val TAG = "MY_APP_DEBUG_TAG"

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

                val msgHandler = mHandler.obtainMessage(BLUETOOTH_NOT_LOADING)
                msgHandler.sendToTarget()
            }
        }catch (e: IOException){
            mIsConnected = false
            e.printStackTrace()
        }
    }

     fun disconnect(){
        mConnectedThread.cancel()
    }

    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream
        private val mmBuffer: ByteArray = ByteArray(25) // mmBuffer store for the stream

        override fun run() {
            var numBytes: Int // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                // Read from the InputStream.
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    break
                }

                // Send the obtained bytes to the UI activity.
                val readMsg = mHandler.obtainMessage(
                    MESSAGE_READ, numBytes, -1,
                    mmBuffer)

                readMsg.sendToTarget()
            }
        }

        // Call this from the main activity to send data to the remote device.
//        fun write(bytes: ByteArray) {
//            try {
//                mmOutStream.write(bytes)
//            } catch (e: IOException) {
//                Log.e(TAG, "Error occurred when sending data", e)
//
//                // Send a failure message back to the activity.
//                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
//                val bundle = Bundle().apply {
//                    putString("toast", "Couldn't send data to the other device")
//                }
//                writeErrorMsg.data = bundle
//                handler.sendMessage(writeErrorMsg)
//                return
//            }
//
//            // Share the sent message with the UI activity.
//            val writtenMsg = handler.obtainMessage(
//                MESSAGE_WRITE, -1, -1, mmBuffer)
//            writtenMsg.sendToTarget()
//        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }


}