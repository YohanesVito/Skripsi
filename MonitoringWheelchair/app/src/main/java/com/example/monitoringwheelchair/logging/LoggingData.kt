package com.example.monitoringwheelchair.logging

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class LoggingData(private val data: ArrayList<Data>){

//     fun sendData() {
//        val client = ApiConfig.getApiService().sendData(data.timeStamp!!,data.speed!!,data.rpm!!,data.battery!!,data.dutyCycle!!,data.compass!!,data.lat!!,data.lon!!)
//        client.enqueue(object : Callback<Data> {
//            override fun onResponse(
//                call: Call<Data>,
//                response: Response<Data>
//            ) {
//                if (response.isSuccessful) {
//                    val responseBody = response.body()
//                    if (responseBody != null) {
//                        Log.d(TAG, "onSuccess: ${response.message()}")
//                    } else {
//                        Log.e(TAG, "onFailure: ${response.message()}")
//                    }
//                }
//            }
//            override fun onFailure(call: Call<Data>, t: Throwable) {
//                Log.e(TAG, "onFailure: ${t.message}")
//            }
//        })
//    }

    fun sendDataList() {
        val client = ApiConfig.getApiService().sendDataList(data)
        client.enqueue(object : Callback<Data> {
            override fun onResponse(
                call: Call<Data>,
                response: Response<Data>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Log.d(TAG, "onSuccess: ${response.message()}")
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }
            }
            override fun onFailure(call: Call<Data>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun logData(){

        //create directory in folder ABA
        val folder = Environment.getExternalStorageDirectory().toString()
        val f = File(folder,"MokuraLoggingData")
        f.mkdir()

        ///storage/emulated/0/MokuraLoggingData/Logging1.csv
        val fileName = Environment.getExternalStorageDirectory().toString() + "/MokuraLoggingData/Logging-${
            LocalDateTime.now()}.csv"
        Log.d("path","$fileName")

        //create header csv
        val header = listOf("timeStamp","speed","rpm","battery","compass","dutyCycle","lat","lon")
        csvWriter().open(File(fileName)) {
            writeRow(header)
        }

        val listdata = ArrayList<Data>().toList()
//        val rows = listOf(listOf(data.timeStamp, data.speed, data.rpm, data.battery, data.compass, data.dutyCycle, data.lat, data.lon))
        val rows = listOf(listdata)
        csvWriter().writeAll(rows, fileName, append = true)

    }

}
