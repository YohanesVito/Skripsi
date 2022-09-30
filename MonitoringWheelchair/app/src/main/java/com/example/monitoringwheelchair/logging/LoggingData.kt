package com.example.monitoringwheelchair.logging

import android.content.ContentValues.TAG
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoggingData {
     fun sendData(data: Data) {
        val client = ApiConfig.getApiService().sendData(data.timeStamp,data.speed,data.rpm,data.battery)
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
}