package com.example.mokuramqtt.utils

import android.content.Context
import android.util.Log
import com.example.mokuramqtt.database.Hardware
import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.model.UserPublishModel
import com.google.gson.Gson
import info.mqtt.android.service.Ack
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MQTTService {
    private lateinit var mqttClient: MqttAndroidClient
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

    fun subscribe(topic: String, qos: Int = 1) {
        try {
            mqttClient.subscribe(topic, qos, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Subscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d(TAG, "Failed to subscribe $topic")
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
}