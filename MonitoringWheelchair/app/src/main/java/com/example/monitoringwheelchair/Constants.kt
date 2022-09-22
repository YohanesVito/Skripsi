package com.example.monitoringwheelchair

import java.util.*

interface Constants {
    companion object {
        // Message types sent from the BluetoothChatService Handler
        const val MESSAGE_STATE_CHANGE = 1
        const val MESSAGE_READ = 2
        const val MESSAGE_WRITE = 3
        const val MESSAGE_DEVICE_NAME = 4
        const val MESSAGE_TOAST = 5

        // Key names received from the BluetoothChatService Handler
        const val DEVICE_NAME = "device_name"
        const val TOAST = "toast"

        //permission
        const val REQUEST_ENABLE_BLUETOOTH = 1
        const val locationPermissionCode = 2

        //uuid
        var mUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        //timer
        const val TIMER_UPDATED = "timerUpdated"
        const val TIME_EXTRA = "timeExtra"

        const val PREFERENCES = "prefs"
        const val START_TIME_KEY = "startKey"
        const val STOP_TIME_KEY = "stopKey"
    }
}