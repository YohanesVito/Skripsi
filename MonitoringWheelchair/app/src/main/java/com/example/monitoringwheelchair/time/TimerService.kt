package com.example.monitoringwheelchair.time

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.monitoringwheelchair.Constants
import com.example.monitoringwheelchair.Constants.Companion.TIME_EXTRA
import java.util.*

class TimerService: Service() {
    private val timer = Timer()
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("t6","test")
        val time = intent?.getDoubleExtra(TIME_EXTRA,0.0)
        timer.scheduleAtFixedRate(time?.let { TimeTask(it) },0,1000)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

    private inner class TimeTask(private var time: Double): TimerTask(){
        override fun run() {
            val intent= Intent(Constants.TIMER_UPDATED)
            time++
            intent.putExtra(TIME_EXTRA,time)
            sendBroadcast(intent)
        }

    }
}