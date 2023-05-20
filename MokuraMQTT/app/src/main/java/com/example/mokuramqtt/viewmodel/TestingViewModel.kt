package com.example.mokuramqtt.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.mokuramqtt.database.Hardware
import com.example.mokuramqtt.database.Mokura
import com.example.mokuramqtt.model.UserModel
import com.example.mokuramqtt.repository.MQTTRepository
import com.example.mokuramqtt.repository.MokuraRepository

class TestingViewModel(private val mqttRepository: MQTTRepository): ViewModel(){

    fun connect(context: Context) = mqttRepository.connect(context)
    fun subscribe(topic: String) = mqttRepository.subscribe(topic)
    fun publishUser(user: UserModel) = mqttRepository.publishUser(user)

    fun publishHardware(hardware: Hardware) = mqttRepository.publishHardware(hardware)

    fun publishLogging(mokura: Mokura) = mqttRepository.publishLogging(mokura)

    fun getUser() = mqttRepository.getUser()

}