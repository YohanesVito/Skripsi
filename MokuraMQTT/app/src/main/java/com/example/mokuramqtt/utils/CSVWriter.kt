package com.example.mokuramqtt.utils

import android.os.Environment
import android.util.Log
import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.helper.DateHelper
import java.io.File
import java.io.*
import java.util.*

class CSVLogger2{

    private val csvHeader = arrayOf(
        "timeStamp",
        "idLogging",
        "idHardware",
        "idUser",
        "speed",
        "rpm",
        "battery",
        "dutyCycle",
        "compass",
        "lat",
        "lon"
    ) // Headers for the CSV file

    fun logToCsv(row: Mokura) {

        val idLogging = row.idLogging.toString()
        val idHardware = row.idHardware.toString()
        val idUser = row.idUser.toString()
        val timeStamp = row.timeStamp
        val speed = row.speed
        val rpm = row.rpm
        val battery= row.battery
        val dutyCycle= row.dutyCycle
        val compass= row.compass
        val lat= row.lat
        val lon= row.lon

        val currentTime = System.currentTimeMillis()
        val date = Date(currentTime)
        val formatter = DateHelper.getCurrentDate2()
        val dateString = formatter.format(date)

//        val directory = Environment.getExternalStorageDirectory().toString()

        val folder = Environment.getExternalStorageDirectory().toString()
        val f = File(folder,"MokuraLoggingData")
        f.mkdirs()

//        /storage/emulated/0/MokuraLoggingData/log_2023-03-15-07:53:56:85.csv
        val fileName = Environment.getExternalStorageDirectory().toString() + "/MokuraLoggingData/log_$dateString.csv"
        Log.d("path","$fileName")

        val csvFile = File(fileName)

        // Create a new CSV file every 15 minutes
        if (!csvFile.exists() || currentTime - csvFile.lastModified() >= 15 * 60 * 1000) {
            csvFile.createNewFile()
            val fileWriter = FileWriter(csvFile, true)
            fileWriter.append(csvHeader.joinToString(","))
            fileWriter.append("\n")
            fileWriter.close()
        }

        // Append data to the CSV file
        val fileWriter = FileWriter(csvFile, true)
        //fileWriter.append("$dateString,$data")
        fileWriter.append("$timeStamp,$idLogging,$idHardware,$idUser,$speed,$rpm,$battery,$dutyCycle,$compass,$lat,$lon")
        fileWriter.append("\n")
        fileWriter.close()
    }
}


