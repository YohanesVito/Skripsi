package com.example.mokuramqtt.utils

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.mokuramqtt.database.Hardware
import com.example.mokuramqtt.database.MQTT
import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.helper.DateHelper
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.model.UserPublishModel
import com.example.mokuramqtt.remote.response.InsertLoggingResponse
import com.example.mokuramqtt.viewmodel.MQTTViewModel
import com.google.gson.Gson
import info.mqtt.android.service.Ack
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MQTTService {
    private lateinit var mqttClient: MqttAndroidClient
    private var sentTimeStamp: String = ""
    private var receivedTimeStamp: String = ""
    companion object{
        const val TAG = "AndroidMqttClient"
        private const val serverURI = "tcp://35.171.206.57:1883"
        private const val userTopic = "mokura/user"
        private const val hardwareTopic = "mokura/hardware"
        private const val loggingTopic = "mokura/logging"
    }

    fun connect(context: Context) {
        mqttClient = MqttAndroidClient(context, serverURI, "kotlin_client", Ack.AUTO_ACK)
        mqttClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d(TAG, "Receive message: ${message.toString()} from topic: $topic")
            }

            override fun connectionLost(cause: Throwable?) {
                Log.d(TAG, "Connection lost ${cause.toString()}")
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
            }
        })
        val options = MqttConnectOptions()
        try {
            mqttClient.connect(options, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Connection success")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Connection failure"+exception?.message.toString())
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }

    }

    fun disconnect() {
        try {
            mqttClient.disconnect(null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Disconnected")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to disconnect")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun subscribe(topic: String, qos: Int = 1, mqttViewModel: MQTTViewModel) {
        try {
            mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Subscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to subscribe $topic")
                }
            })

            mqttClient.setCallback(object : MqttCallback {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    // Handle the received message here
                    receivedTimeStamp = DateHelper.getCurrentDate()
                    val payload = message?.payload?.toString(Charsets.UTF_8)
                    val gson = Gson()
                    val response: InsertLoggingResponse = gson.fromJson(payload, InsertLoggingResponse::class.java)
                    Log.d(TAG, "Received message: $payload from topic: $topic")
                    Log.d(TAG, "Received JSON: $response from topic: $topic")

                    // Add your logic to handle the incoming message
                    val timeDifference = DateHelper.calculateTimeDifference(sentTimeStamp,receivedTimeStamp)
                    val timeTransmission = DateHelper.calculateTimeDifference(sentTimeStamp,response.serverTimeStr!!)

                    val mMQTT = MQTT(
                        receivedTimeStamp = receivedTimeStamp,
                        sentTimeStamp = receivedTimeStamp,
                        packetSize = response.packetSize.toString(),
                        timeDifference = timeDifference.toString(),
                        timeTransmission = timeTransmission.toString()
                    )
                    Log.d("MQTTRESPON", mMQTT.toString())

                    mqttViewModel.insertMQTT(mMQTT)
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.d(TAG, "Connection lost ${cause.toString()}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {

                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun unsubscribe(topic: String) {
        try {
            mqttClient.unsubscribe(topic, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Unsubscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to unsubscribe $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publish(topic: String, msg: String, qos: Int = 1, retained: Boolean = false) {
        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            message.qos = qos
            message.isRetained = retained
            mqttClient.publish(topic, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "$msg published to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to publish $msg to $topic")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun publishUser(user: UserModel) {
        val mUser = UserPublishModel(
            id_user =  user.id_user,
            username= user.name,
            email= user.email,
            password= user.password,
        )
        val gson = Gson()
        val userJson = gson.toJson(mUser)
        val mqttMessage = MqttMessage()
        mqttMessage.payload = userJson.toByteArray()
        mqttClient.publish(userTopic, mqttMessage)

    }

    fun publishHardware(hardware: Hardware) {
        val mqttMessage = MqttMessage()
        mqttMessage.payload = hardware.toString().toByteArray()
        mqttClient.publish(hardwareTopic, mqttMessage)
    }

    fun publishLogging(mokura: Mokura) {
        val mqttMessage = MqttMessage()
        mqttMessage.payload = mokura.toString().toByteArray()
        mqttClient.publish(loggingTopic, mqttMessage)

    }

    fun publishArrayLogging(mArray: ArrayList<Mokura>) {
        val gson = Gson()
        val arrayJson = gson.toJson(mArray)
        val mqttMessage = MqttMessage()
        mqttMessage.qos = 0
        mqttMessage.payload = arrayJson.toString().toByteArray()

        //get timestamp before publish
        sentTimeStamp = DateHelper.getCurrentDate()
        mqttClient.publish(loggingTopic, mqttMessage)
    }
}