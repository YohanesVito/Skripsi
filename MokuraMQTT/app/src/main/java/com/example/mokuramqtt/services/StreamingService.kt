package com.example.mokuramqtt.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mokuramqtt.R
import com.example.mokuramqtt.database.MokuraNew
import com.example.mokuramqtt.helper.DateHelper
import com.example.mokuramqtt.model.Result
import com.example.mokuramqtt.remote.response.InsertLoggingNewResponse
import com.example.mokuramqtt.remote.retrofit.ApiConfig
import com.example.mokuramqtt.remote.retrofit.ApiService
import com.example.mokuramqtt.repository.MokuraRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val NOTIFICATION_ID = 1234
private val CHANNEL_ID = "my_channel_id"

class StreamingService : Service() {
    private lateinit var dataRepository: DataRepository

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundService() {
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Streaming Data")
            .setContentText("Streaming in progress")
            .setSmallIcon(R.drawable.ic_notification_icon)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        dataRepository = DataRepository(applicationContext)
        dataRepository.dataLocation.observeForever { location ->
            // Handle data changes here
            val lat = location[0].toString()
            val lon = location[1].toString()
            val time = DateHelper.getCurrentDate()

            // Perform operations with lat and lon
                val newMokura = MokuraNew(
                    id = "bf49543d-d4d9-4fd9-bb24-2179fca3f713",
                    name = "Mokura 2_$time",
                    speed = 50,
                    battery = 60,
                    throtle = 20,
                    lat = lat,
                    long = lon,
                    status = 1,
                )

            sendDummy(newMokura)
        }
    }

    fun sendDummy(dummyData: MokuraNew): LiveData<Result<Boolean>> {
        val result = MutableLiveData<Result<Boolean>>()
        result.value = Result.Loading
        ApiConfig.getApiService().sendData(dummyData).enqueue(object : Callback<InsertLoggingNewResponse> {
            override fun onResponse(
                call: Call<InsertLoggingNewResponse>,
                response: Response<InsertLoggingNewResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null){
                        Log.d("SendDummy","Sent: $responseBody")
                        result.value = Result.Success(true)
                    }
                }else {
                    Log.d("SendDummy","Failed: ${response.message()}")
                    result.value = Result.Error(response.message())
                }
            }
            override fun onFailure(call: Call<InsertLoggingNewResponse>, t: Throwable) {
                Log.d("SendDummy","Failed: ${t.message}")

                result.value = Result.Error("Can't Connect Retrofit")

            }
        })
        return result
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up any resources used for streaming
    }
}
