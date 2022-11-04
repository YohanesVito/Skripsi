package com.example.monitoringwheelchair.logging

import android.content.ContentValues.TAG
import android.os.Environment
import android.util.Log
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class LoggingData(private val data: Data){

     fun sendData() {
        val client = ApiConfig.getApiService().sendData(data.timeStamp!!,data.speed!!,data.rpm!!,data.battery!!,data.dutyCycle!!,data.compass!!,data.lat!!,data.lon!!)
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

    fun sendDataList(data: ArrayList<Data>) {
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


    fun logData(){
        //create directory in folder ABA
        val folder = Environment.getExternalStorageDirectory().toString()
        val f = File(folder,"MokuraLoggingData")
        f.mkdir()

        ///storage/emulated/0/ABA/test.wav
        val fileName = Environment.getExternalStorageDirectory().toString() + "/MokuraLoggingData/Logging1.csv"
        Log.d("path","$fileName")

        val rows = listOf(listOf(data.timeStamp, data.speed, data.rpm, data.battery, data.compass, data.dutyCycle, data.lat, data.lon))
        csvWriter().writeAll(rows, fileName, append = true)

    }

}
