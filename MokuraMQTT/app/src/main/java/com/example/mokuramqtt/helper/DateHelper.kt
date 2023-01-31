package com.example.mokuramqtt.helper

import java.text.SimpleDateFormat
import java.util.*

object DateHelper {
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SS", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }
}