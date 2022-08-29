package com.example.speedometer

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.example.speedometer.databinding.ActivityMainBinding
import com.example.speedometer.databinding.ActivitySecondBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.ServerSocket
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

        val address = "192.168.2.11"
        val port = 6969

        val btConnect = binding.button
        btConnect.setOnClickListener {
            val thread = Thread(MyServerThread())
            thread.start()
            if (btConnect.text == "Connect"){
                btConnect.text = "Disconnect"
                active = true
//                CoroutineScope(IO).launch{
//
////                    client(address,port)
//                }
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

class MyServerThread : Runnable {
    private lateinit var socket: Socket
    private lateinit var inputStreamReader: InputStreamReader
    private lateinit var bufferedReader: BufferedReader
    private lateinit var handler: Handler
    private lateinit var message: String
    override fun run() {
        try {
            val serverSocket= ServerSocket(6969)
            while (true){
                socket = serverSocket.accept()
                inputStreamReader = InputStreamReader(socket.getInputStream())
                bufferedReader= BufferedReader(inputStreamReader)
                message = bufferedReader.readLine()

                handler.post{
                    run(){
//                        Toast.makeText(withContext(IO),message,Toast.LENGTH_SHORT)
                        Log.d("msg",message)
                    }
                }
            }
        }catch (e: IOException){
            e.printStackTrace()
        }
    }

}