package com.example.speedometer

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.example.speedometer.databinding.ActivityMainBinding
import com.example.speedometer.databinding.ActivitySendDataBinding
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import kotlin.coroutines.coroutineContext

class SendDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySendDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val server = MyServer(this)
        val thread = Thread(server)
        thread.start()

        binding.btSend.setOnClickListener {
            val ip = binding.etIp.text.toString()
            val message = binding.etMessage.text.toString()

            val bgtask = BackgroundTask()
            bgtask.execute(ip,message)
        }
    }
}

class BackgroundTask : AsyncTask<String,Void,String>(){
    companion object{
        private lateinit var socket: Socket
        private lateinit var dataOutputStream: DataOutputStream
        private var ip : String? = null
        private var message : String? = null
    }
    @Deprecated("Deprecated in Java")
    override fun doInBackground(vararg p0: String?): String? {
        ip = p0[0]
        message = p0[1]

        try {
            socket = Socket(ip,6969)
            dataOutputStream = DataOutputStream(socket.getOutputStream())
            dataOutputStream.writeUTF(message)

            dataOutputStream.close()
            socket.close()
        }
        catch(e: IOException){
            e.printStackTrace()
        }
        return null
    }

}

class MyServer(private val mContext: Context) : Runnable{
    companion object{
        private lateinit var serverSocket: ServerSocket
        private lateinit var socket: Socket
        private lateinit var dataInputStream: DataInputStream
        private var message : String? = null
        private var handler= Handler()
    }
    override fun run() {
        try {
            serverSocket = ServerSocket(6969)
            while (true){
                socket = serverSocket.accept()
                dataInputStream = DataInputStream(socket.getInputStream())
                message = dataInputStream.readUTF()
                handler.post(Runnable(){
                    run(){
                        Toast.makeText(mContext,"message received from client"+ message,Toast.LENGTH_SHORT).show()
                    }
                })

            }
        }
        catch (e:IOException){
            e.printStackTrace()
        }
    }

}