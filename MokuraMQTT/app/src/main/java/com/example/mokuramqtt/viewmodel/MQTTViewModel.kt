package com.example.mokuramqtt.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mokuramqtt.database.Hardware
import com.example.mokuramqtt.database.MQTT
import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.repository.MQTTRepository

class MQTTViewModel(private val mqttRepository: MQTTRepository): ViewModel(){

    val arrayLogging: MutableLiveData<ArrayList<Mokura>> by lazy {
        MutableLiveData<ArrayList<Mokura>>()
    }

    val allData: LiveData<List<MQTT>> = mqttRepository.getRow()

    fun connect(context: Context) = mqttRepository.connect(context)

    fun disconnect() = mqttRepository.disconnect()

    fun subscribe(topic: String, mqttViewModel: MQTTViewModel) = mqttRepository.subscribe(topic, mqttViewModel = mqttViewModel)

    fun unsubscribe(topic: String) = mqttRepository.unsubscribe(topic)
    fun publishArrayLogging(mArray: ArrayList<Mokura>) = mqttRepository.publishArrayLogging(mArray)

    fun publishUser(user: UserModel) = mqttRepository.publishUser(user)

    fun getUser() = mqttRepository.getUser()

    fun insertMQTT(mMQTT: MQTT)= mqttRepository.insertMQTT(mMQTT)

    fun publishHardware(hardware: Hardware) = mqttRepository.publishHardware(hardware)

    fun publishLogging(mokura: Mokura) = mqttRepository.publishLogging(mokura)
}