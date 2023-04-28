package com.example.mokuramqtt

import java.util.*

interface Constants {
    companion object {
        // Message types sent from the BluetoothChatService Handler
        const val BLUETOOTH_LOADING = 0
        const val BLUETOOTH_NOT_LOADING = 6
        const val MESSAGE_READ = 2

        //permission
        const val REQUEST_EXTERNAL_STORAGE_PERMISSION = 660
        const val REQUEST_ENABLE_BLUETOOTH = 661
        const val locationPermissionCode = 662
        const val REQUEST_CODE_PERMISSIONS = 663
        val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        //uuid
        var mUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        //speedometer and rpm animation duration
        const val DURATION = 200L

    }
}