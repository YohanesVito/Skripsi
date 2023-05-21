package com.example.mokuramqtt.helper

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSSSSS", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    fun calculateTimeDifference(timeStart: String, timeEnd: String): Double {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSSSSS", Locale.getDefault())

        val timestamp1 = dateFormat.parse(timeStart)
        val timestamp2 = dateFormat.parse(timeEnd)

        return (timestamp2.time - timestamp1.time) / 1000.0
    }
}