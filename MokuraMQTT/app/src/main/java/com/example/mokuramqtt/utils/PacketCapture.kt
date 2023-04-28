package com.example.mokuramqtt.utils

import android.os.Environment
import java.io.FileWriter
import java.io.File
import java.io.IOException

class PacketCapture {

    private val folder = Environment.getExternalStorageDirectory().toString()
    private val directory = File(folder, "MokuraLoggingData")
    private val fileWriter: FileWriter

    init {
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "log.txt")
        fileWriter = FileWriter(file, true)
    }

    fun write(value: Long) {
        try {
            fileWriter.appendln(value.toString())
            fileWriter.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun close() {
        try {
            fileWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

class PacketCapture2{

    private val folder = Environment.getExternalStorageDirectory().toString()
    private val directory = File(folder, "MokuraLoggingData")
    private val fileWriter: FileWriter

    init {
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "log.txt")
        fileWriter = FileWriter(file, true)
    }

    fun write(value: Long) {
        try {
            fileWriter.appendln(value.toString())
            fileWriter.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun close() {
        try {
            fileWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}



