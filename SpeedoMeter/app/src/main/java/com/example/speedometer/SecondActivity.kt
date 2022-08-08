package com.example.speedometer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.speedometer.databinding.ActivityMainBinding
import com.example.speedometer.databinding.ActivitySecondBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Inet4Address
import java.net.Socket
import java.util.*
import kotlin.coroutines.CoroutineContext

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding
    private var active: Boolean = false
    private var data : String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val address = "192.168.100.8"
        val port = 6969

        val btConnect = binding.button
        btConnect.setOnClickListener {
            if (btConnect.text == "Connect"){
                btConnect.text = "Disconnect"
                active = true
                CoroutineScope(IO).launch{
                    client(address,port)
                }
            }else{
                active = false
                btConnect.text = "Connect"
            }
        }


    }

    private suspend fun client(address: String, port: Int){
        val connection = Socket(address,port)
//        val writer: OutputStream = connection.getOutputStream()
//        writer.write(1)
        val reader = InputStreamReader(connection.getInputStream())
        val br = BufferedReader(reader)

        val input = br.readLine()
        this.runOnUiThread {
            data = input.toString()
            binding.textView.text =data
        }


//        reader.close()
//        writer.close()
//        connection.close()
    }
}